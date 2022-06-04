package online.viestudio.paperkit.plugin

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addFileSource
import com.sksamuel.hoplite.addResourceSource
import kotlinx.coroutines.*
import online.viestudio.paperkit.config.ConfigLoader
import online.viestudio.paperkit.config.HopliteConfigLoaderBuilderFactory
import online.viestudio.paperkit.config.LambdaConfigLoader
import online.viestudio.paperkit.koin.Global
import online.viestudio.paperkit.koin.KoinModulesContainer
import online.viestudio.paperkit.logger.KitLogger
import online.viestudio.paperkit.paper.PrimaryCoroutineController
import online.viestudio.paperkit.paper.PrimaryCoroutineDispatcher
import online.viestudio.paperkit.plugin.KitPlugin.Companion.RESOURCES_CONFIG_DIRECTORY
import online.viestudio.paperkit.plugin.KitPlugin.State
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.inject
import org.koin.core.qualifier.StringQualifier
import org.koin.dsl.bind
import java.io.InputStream
import kotlin.coroutines.CoroutineContext
import kotlin.system.measureTimeMillis

abstract class BaseKitPlugin(
    nThreads: Int = Runtime.getRuntime().availableProcessors(),
) : JavaPlugin(), KitPlugin {

    @Volatile
    final override var state: State = State.Stopped

    @OptIn(DelicateCoroutinesApi::class)
    final override val scope: CoroutineScope by lazy {
        CoroutineScope(
            pluginScopeExceptionHandler + SupervisorJob() + newFixedThreadPoolContext(
                nThreads,
                name
            )
        )
    }
    final override val context: CoroutineContext get() = scope.coroutineContext
    final override val isPrimaryThread: Boolean get() = server.isPrimaryThread
    final override val log: KitLogger by lazy { KitLogger(logger) }
    final override val serverContext: CoroutineContext get() = serverScope.coroutineContext
    final override val serverScope: CoroutineScope by lazy {
        CoroutineScope(serverScopeExceptionHandler + SupervisorJob() + coroutineDispatcher)
    }
    final override val version: String get() = description.version
    protected val hopliteConfigLoaderBuilder: ConfigLoaderBuilder get() = hopliteConfigLoaderBuilderFactory.create()
    private val hopliteConfigLoaderBuilderFactory: HopliteConfigLoaderBuilderFactory by inject()
    private val koinModulesContainer by lazy { KoinModulesContainer() }
    private val coroutineDispatcher by lazy { PrimaryCoroutineDispatcher(this, coroutineController) }
    private val coroutineController by lazy { PrimaryCoroutineController(this) }
    private val registeredConfigLoaders: MutableList<ConfigLoader<*>> by lazy { ArrayList() }
    private val pluginScopeExceptionHandler
        get() = CoroutineExceptionHandler { _, throwable ->
            log.w(throwable) { "An unexpected exception has occurred" }
        }
    private val serverScopeExceptionHandler
        get() = CoroutineExceptionHandler { _, throwable ->
            log.w(throwable) { "An unexpected exception has occurred" }
        }

    final override fun onEnable() {
        coroutineController.runControlled {
            koinModulesContainer.loadModules()
            start()
        }
    }

    private suspend fun KoinModulesContainer.loadModules() = coroutineScope {
        val result: Result<Unit>
        val measuredMillis = measureTimeMillis {
            result = runCatching {
                module {
                    single(StringQualifier(name)) { this@BaseKitPlugin } bind KitPlugin::class
                }
                onLoadModules()
                Global.koin.apply {
                    loadModules(modules)
                }
            }
        }
        result.onFailure {
            log.w(it) { "Loading koin modules failed to an unexpected exception!" }
        }.onFailure {
            log.d { "Koin modules loaded in $measuredMillis millis." }
        }.isSuccess
    }

    protected open suspend fun KoinModulesContainer.onLoadModules() {
        // Should do nothing by default
    }

    final override suspend fun start() = coroutineScope {
        if (state != State.Stopped) return@coroutineScope false
        state = State.Starting
        val result: Result<Unit>
        val measuredMillis = measureTimeMillis {
            result = runCatching { onStart() }
        }
        result.onFailure {
            log.e(it) { "Starting plugin failed due to an unexpected exception!" }
            state = State.Stopped
        }.onSuccess {
            log.d { "Plugin started in $measuredMillis millis." }
            state = State.Started
        }.isSuccess
    }

    protected open suspend fun onStart() {
        // Should do nothing by default
    }

    final override fun onDisable() {
        runBlocking {
            stop()
            koinModulesContainer.unloadModules()
        }
        context.cancelChildren()
        scope.cancel("Disabling plugin")
    }

    private suspend fun KoinModulesContainer.unloadModules() = coroutineScope {
        getKoin().unloadModules(modules)
    }


    final override suspend fun stop() = coroutineScope {
        if (state != State.Started) return@coroutineScope false
        state = State.Stopping
        val result: Result<Unit>
        val measuredMillis = measureTimeMillis {
            result = runCatching {
                onStop()
            }
        }
        state = State.Stopped
        result.onFailure {
            log.e(it) { "Stopping plugin failed due to an unexpected exception!" }
        }.onSuccess {
            log.d { "Plugin stopped in $measuredMillis millis." }
        }.isSuccess
    }

    protected open suspend fun onStop() {
        // Should do nothing by default
    }

    override fun reloadConfig() {
        runBlocking { reload() }
    }

    final override suspend fun reload() = coroutineScope {
        if (state != State.Started) return@coroutineScope false
        val result: Result<Unit>
        val measuredMillis = measureTimeMillis {
            result = runCatching {
                supervisorScope {
                    registeredConfigLoaders.forEach {
                        async { it.load() }.start()
                    }
                }
                onReload()
            }
        }
        result.onFailure {
            log.w(it) { "Reloading plugin failed due to an unexpected exception!" }
        }.onSuccess {
            log.d { "Plugin reloaded in $measuredMillis millis." }
        }.isSuccess
    }

    protected open suspend fun onReload() {
        // Should do nothing by default
    }

    protected inline fun <reified T : Any> registeredConfigLoader(
        fileName: String,
        writeDefaultIfNotExists: Boolean = true,
    ): ConfigLoader<T> = LambdaConfigLoader<T> {
        loadConfig(fileName, writeDefaultIfNotExists)
    }.apply(this::registerConfigLoader)

    protected fun registerConfigLoader(configLoader: ConfigLoader<*>) {
        registeredConfigLoaders.add(configLoader)
    }

    protected fun unregisterConfigLoader(configLoader: ConfigLoader<*>) {
        registeredConfigLoaders.remove(configLoader)
    }

    protected inline fun <reified T : Any> loadConfig(fileName: String, writeDefaultIfNotExists: Boolean = true): T {
        if (writeDefaultIfNotExists) writeDefaultConfigIfNotExists(fileName)
        return hopliteConfigLoaderBuilder.withClassLoader(this::class.java.classLoader)
            .addFileSource(dataFolder.resolve(fileName), optional = true)
            .addResourceSource("/$RESOURCES_CONFIG_DIRECTORY/$fileName")
            .build()
            .loadConfigOrThrow()
    }

    protected fun writeDefaultConfigIfNotExists(fileName: String) {
        val file = dataFolder.apply {
            if (!exists()) mkdirs()
        }.resolve(fileName)
        if (file.exists()) return

        val defaultContentBytes =
            getResource("$RESOURCES_CONFIG_DIRECTORY/$fileName")?.use(InputStream::readAllBytes) ?: return
        file.outputStream().use { it.write(defaultContentBytes) }
    }
}