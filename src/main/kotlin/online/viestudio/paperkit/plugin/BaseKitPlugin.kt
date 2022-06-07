package online.viestudio.paperkit.plugin

import online.viestudio.paperkit.bukkit.syncCommands
import online.viestudio.paperkit.command.CommandsDeclaration
import online.viestudio.paperkit.command.KitCommand
import online.viestudio.paperkit.command.KitCommandAdapter
import online.viestudio.paperkit.command.adapter
import online.viestudio.paperkit.config.ConfigurationDeclaration
import online.viestudio.paperkit.config.source.FileSource
import online.viestudio.paperkit.config.writer.ConfigWriter
import online.viestudio.paperkit.koin.Global
import online.viestudio.paperkit.koin.KoinModulesContainer
import online.viestudio.paperkit.koin.config
import online.viestudio.paperkit.koin.pluginQualifier
import online.viestudio.paperkit.plugin.KitPlugin.State
import online.viestudio.paperkit.plugin.exception.InvalidPluginStateException
import online.viestudio.paperkit.theme.Theme
import online.viestudio.paperkit.utils.safeRunWithMeasuring
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.qualifier.StringQualifier
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.core.module.Module as KoinModule

/**
 * Implementation of [KitPlugin] supposed to be extended.
 *
 * @param nThreads Count of threads to allocate by its coroutine scope.
 * Default is equivalent to number of available processors.
 */
open class BaseKitPlugin(
    nThreads: Int = Runtime.getRuntime().availableProcessors(),
) : ScopeKitPlugin(nThreads) {

    final override val theme: Theme by config()
    private val container by lazy { KoinModulesContainer().apply { export() } }
    private val configurationDeclaration by lazy {
        ConfigurationDeclaration(
            this,
            get()
        ).apply { declareConfiguration() }
    }
    private val commandsDeclaration by lazy {
        CommandsDeclaration().apply { declareCommands() }
    }
    private val configWriter: ConfigWriter by inject()
    private val pluginModule: KoinModule by lazy {
        module {
            single(StringQualifier(name)) { this@BaseKitPlugin } bind KitPlugin::class
            single(pluginQualifier) { this@BaseKitPlugin } bind KitPlugin::class
        }
    }

    protected open fun CommandsDeclaration.declareCommands() {}

    protected open fun ConfigurationDeclaration.declareConfiguration() {}

    protected open fun KoinModulesContainer.export() {}

    final override suspend fun start(): Boolean = safeRunWithMeasuring {
        ensureStateOrThrow(State.Stopped)
        state = State.Starting
        reloadResources()
        registerCommands()
        onStart()
    }.onSuccess {
        state = State.Started
        log.d { "Plugin started in $it millis." }
    }.onFailure {
        state = State.Stopped
        log.w(it) { "Plugin starting failed." }
    }.isSuccess

    private suspend fun registerCommands() {
        commandsDeclaration.commands.forEach { registerCommand(it) }
        server.syncCommands()
    }

    private suspend fun registerCommand(command: KitCommand) {
        command.ensureInit()
        val adapter = command.adapter()
        val commandMap = server.commandMap
        if (!commandMap.register(name, adapter)) {
            log.w { "Command ${command.name} wasn't registered." }
        }
    }

    /**
     * Invoked when plugin is starting.
     * Supposed to override whenever needed.
     */
    protected open suspend fun onStart() {
        // Should do nothing by default.
    }

    final override suspend fun stop(): Boolean = safeRunWithMeasuring {
        ensureStateOrThrow(State.Started)
        state = State.Stopping
        unregisterCommands()
        onStop()
    }.onSuccess {
        state = State.Stopped
        log.d { "Plugin stopped in $it millis." }
    }.onFailure {
        state = State.Stopped
        log.w(it) { "Plugin stopping failed." }
    }.isSuccess

    private fun unregisterCommands() {
        commandsDeclaration.commands.forEach(this::unregisterCommand)
        server.syncCommands()
    }

    private fun unregisterCommand(kitCommand: KitCommand) {
        server.commandMap.knownCommands.apply {
            filter {
                val possiblyAdapter = it.value
                possiblyAdapter is KitCommandAdapter && possiblyAdapter.isAdapterOf(kitCommand)
            }.forEach {
                remove(it.key)
                server.commandAliases.remove(it.key)
            }
        }
    }

    /**
     * Invoked when plugin is stopping.
     * Supposed to override whenever needed.
     */
    protected open suspend fun onStop() {
        // Should do nothing by default.
    }

    final override suspend fun reloadResources(): Boolean = safeRunWithMeasuring {
        ensureStateOrThrow(State.Starting, State.Started)
        getKoin().apply {
            unloadModules(listOf(configurationDeclaration.module))
            loadModules(listOf(configurationDeclaration.module))
        }
        writeConfig()
        onReloadResources()
    }.onSuccess {
        log.d { "Resources loaded in $it millis." }
    }.onFailure {
        log.w(it) { "Loading resources failed." }
    }.isSuccess

    private fun writeConfig() {
        configurationDeclaration.map.filter { (_, sources) ->
            sources.firstOrNull() is FileSource && sources.size > 1
        }.forEach { (_, sources) ->
            val target = sources.first() as FileSource
            configWriter.writeOrMergeIfExists(target, *sources.toTypedArray().copyOfRange(1, sources.size))
        }
    }

    /**
     * Invoked when plugin is reloading resources.
     * Supposed to override whenever needed.
     */
    protected open suspend fun onReloadResources() {
        // Should do nothing by default
    }

    final override suspend fun prepareToStart() {
        Global.koin.apply {
            loadModules(container.modules)
            loadModules(listOf(pluginModule))
        }
        configurationDeclaration.apply {
            Theme::class loadFrom (file("theme.yml") or resource("theme.yml") or defaults(Theme::class))
        }
    }

    final override suspend fun freeUpResources() {
        getKoin().apply {
            unloadModules(container.modules)
            unloadModules(listOf(configurationDeclaration.module, pluginModule))
        }
    }

    private fun ensureStateOrThrow(vararg expectedStates: State) {
        if (state !in expectedStates) throw InvalidPluginStateException(this, expectedStates.toSet())
    }
}