package online.viestudio.paperkit.plugin

import online.viestudio.paperkit.bukkit.syncCommands
import online.viestudio.paperkit.collections.concurrentSetOf
import online.viestudio.paperkit.command.CommandsDeclaration
import online.viestudio.paperkit.command.KitCommand
import online.viestudio.paperkit.command.KitCommandAdapter
import online.viestudio.paperkit.command.adapter
import online.viestudio.paperkit.config.ConfigurationDeclaration
import online.viestudio.paperkit.config.loader.ConfigLoader
import online.viestudio.paperkit.config.loader.HopliteConfigLoader
import online.viestudio.paperkit.config.source.DefaultsSource
import online.viestudio.paperkit.config.source.FileSource
import online.viestudio.paperkit.config.source.ResourceSource
import online.viestudio.paperkit.config.writer.ConfigWriter
import online.viestudio.paperkit.config.writer.SnakeYamlConfigWriter
import online.viestudio.paperkit.koin.Global
import online.viestudio.paperkit.koin.KoinModulesContainer
import online.viestudio.paperkit.koin.pluginQualifier
import online.viestudio.paperkit.listener.KitListener
import online.viestudio.paperkit.listener.register
import online.viestudio.paperkit.listener.unregister
import online.viestudio.paperkit.plugin.KitPlugin.State
import online.viestudio.paperkit.plugin.exception.InvalidPluginStateException
import online.viestudio.paperkit.theme.Appearance
import online.viestudio.paperkit.util.safeRunWithMeasuring
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
abstract class BaseKitPlugin(
    nThreads: Int = Runtime.getRuntime().availableProcessors(),
) : ScopeKitPlugin(nThreads) {

    final override lateinit var appearance: Appearance
    private val container by lazy { KoinModulesContainer().apply { export() } }
    private val configurationDeclaration by lazy {
        ConfigurationDeclaration(this, configLoader).apply { declareConfiguration() }
    }
    private val commandsDeclaration by lazy {
        CommandsDeclaration().apply { declareCommands() }
    }
    protected val configWriter: ConfigWriter by lazy { SnakeYamlConfigWriter() }
    protected val configLoader: ConfigLoader by lazy { HopliteConfigLoader("yaml") }
    private val bindedListeners: MutableSet<KitListener> = concurrentSetOf()
    private val qualifier get() = pluginQualifier
    private val appearanceSources by lazy {
        listOf(
            FileSource(dataFolder.resolve("appearance.yml").path),
            ResourceSource(this, "appearance.yml"),
            DefaultsSource(Appearance::class)
        )
    }
    private val pluginModule: KoinModule by lazy {
        module {
            single(StringQualifier(name)) { this@BaseKitPlugin } bind KitPlugin::class
            single(qualifier) { this@BaseKitPlugin } bind KitPlugin::class
        }
    }

    protected open fun CommandsDeclaration.declareCommands() {}

    protected open fun ConfigurationDeclaration.declareConfiguration() {}

    protected open fun KoinModulesContainer.export() {}

    final override suspend fun start(): Boolean = safeRunWithMeasuring {
        ensureStateOrThrow(State.Stopped)
        state = State.Starting
        reloadResources()
        registerListeners()
        registerCommands()
        onStart()
    }.onSuccess {
        state = State.Started
        log.d { "Plugin started in $it millis." }
    }.onFailure {
        if (state == State.Starting) {
            state = State.Stopped
        }
        log.w(it) { "Plugin starting failed." }
    }.isSuccess

    private fun registerListeners() {
        bindedListeners.forEach(KitListener::register)
    }

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
        unregisterListeners()
        unregisterCommands()
        onStop()
    }.onSuccess {
        state = State.Stopped
        log.d { "Plugin stopped in $it millis." }
    }.onFailure {
        state = State.Stopped
        log.w(it) { "Plugin stopping failed." }
    }.isSuccess

    private fun unregisterListeners() {
        bindedListeners.forEach(KitListener::unregister)
    }

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
        writeConfig()
        reloadAppearance()
        getKoin().apply {
            unloadModules(listOf(configurationDeclaration.module))
            loadModules(listOf(configurationDeclaration.module))
        }
        onReloadResources()
    }.onSuccess {
        log.d { "Resources loaded in $it millis." }
    }.onFailure {
        log.w(it) { "Loading resources failed." }
    }.isSuccess

    private fun reloadAppearance() {
        appearance = configLoader.loadOrThrow(Appearance::class, appearanceSources)
    }

    private fun writeConfig() {
        writeAppearance()
        configurationDeclaration.map.filter { (_, sources) ->
            sources.firstOrNull() is FileSource && sources.size > 1
        }.forEach { (_, sources) ->
            val target = sources.first() as FileSource
            configWriter.writeOrMergeIfExists(target, *sources.toTypedArray().copyOfRange(1, sources.size))
        }
    }

    private fun writeAppearance() {
        configWriter.writeOrMergeIfExists(
            appearanceSources.first() as FileSource,
            appearanceSources.subList(1, appearanceSources.size)
        )
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
        configLoader.apply {
            addPlaceholderSources(appearanceSources)
        }
    }

    final override suspend fun freeUpResources() {
        getKoin().apply {
            unloadModules(container.modules)
            unloadModules(listOf(configurationDeclaration.module, pluginModule))
        }
    }

    final override suspend fun bindListener(listener: KitListener) {
        bindedListeners.add(listener)
        if (state == State.Started) listener.register()
    }

    private fun ensureStateOrThrow(vararg expectedStates: State) {
        if (state !in expectedStates) throw InvalidPluginStateException(this, expectedStates.toSet())
    }
}
