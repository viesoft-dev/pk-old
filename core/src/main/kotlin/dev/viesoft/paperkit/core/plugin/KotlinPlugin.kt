package dev.viesoft.paperkit.core.plugin

import dev.viesoft.paperkit.core.command.KotlinCommandExecutor
import dev.viesoft.paperkit.core.command.KotlinTabCompleter
import dev.viesoft.paperkit.core.coroutines.AsyncPluginDispatcher
import dev.viesoft.paperkit.core.coroutines.PluginCoroutineController
import dev.viesoft.paperkit.core.coroutines.PluginDispatcher
import kotlinx.coroutines.*
import mu.KLogger
import mu.toKLogger
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.CancellationException
import kotlin.coroutines.CoroutineContext

@Suppress("unused")
abstract class KotlinPlugin : JavaPlugin(), IKotlinPlugin, KotlinCommandExecutor, KotlinTabCompleter {

    private val controller by lazy { PluginCoroutineController(this) }
    private val dispatcher by lazy { PluginDispatcher(this, controller) }
    final override val context: CoroutineContext by lazy { newContext("plugin", dispatcher) }
    final override val scope: CoroutineScope by lazy { CoroutineScope(context) }
    private val asyncDispatcher by lazy { AsyncPluginDispatcher(this, controller) }
    final override val asyncContext: CoroutineContext by lazy { newContext("async", asyncDispatcher) }
    final override val asyncScope: CoroutineScope by lazy { CoroutineScope(asyncContext) }
    final override val log: KLogger = slF4JLogger.toKLogger()

    final override fun reloadConfig() {
        controller.runControlled { loadConfig() }
    }

    final override fun onLoad() {
        scope.ensureActive()
        asyncScope.ensureActive()
        runBlocking { onLoaded() }
    }

    final override fun onEnable() {
        controller.runControlled {
            loadConfig()
            onEnabled()
        }
    }

    final override fun onDisable() {
        try {
            runBlocking { onDisabled() }
        } finally {
            val cancellationException = CancellationException("Stopping the plugin")
            dispatcher.cancelChildren(cancellationException)
            asyncDispatcher.cancelChildren(cancellationException)
        }
    }

    final override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>
    ): Boolean {
        var isSuccessful = true
        scope.launch {
            isSuccessful = execute(sender, command, label, args.toList())
        }
        return isSuccessful
    }

    override suspend fun execute(
        sender: CommandSender,
        command: Command,
        label: String,
        args: List<String>
    ): Boolean {
        log.warn { "${this::class.qualifiedName} set as executor of the $label command, but the method #execute was not implemented." }
        return false
    }

    final override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<String>
    ): List<String>? {
        var tabComplete: List<String>? = null
        scope.launch {
            tabComplete = tabComplete(sender, command, alias, args.toList())
        }
        return tabComplete
    }

    override suspend fun tabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: List<String>
    ): List<String>? {
        log.warn { "${this::class.qualifiedName} set as executor of the $label command, but the method #tabComplete was not implemented." }
        return null
    }

    private fun newContext(name: String, dispatcher: CoroutineDispatcher): CoroutineContext {
        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            log.warn(throwable) { "An exception has occurred in a coroutine within the $name context." }
        }
        return exceptionHandler + SupervisorJob() + dispatcher
    }
}
