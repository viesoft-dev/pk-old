package online.viestudio.paperkit.paper

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import online.viestudio.paperkit.bukkit.craftSchedulerHeartBeatMethod
import online.viestudio.paperkit.bukkit.craftSchedulerTickField
import online.viestudio.paperkit.plugin.KitPlugin
import org.bukkit.Server
import java.util.concurrent.locks.LockSupport

internal class ServerCoroutineController(
    private val plugin: KitPlugin,
) {

    private var isEnabled: Boolean = false
    private val currentTick get() = craftSchedulerTickField.get(scheduler)
    private val craftSchedulerTickField get() = server.craftSchedulerTickField
    private val scheduler get() = server.scheduler
    private val craftSchedulerHeartBeatMethod get() = server.craftSchedulerHeartBeatMethod
    private val server: Server get() = plugin.server
    private lateinit var primaryThread: Thread

    fun runControlled(block: suspend CoroutineScope.() -> Unit) {
        isEnabled = true
        runBlocking { block() }
        isEnabled = false
    }

    fun unblockIfNeeded() {
        if (!isEnabled) return
        if (plugin.server.isPrimaryThread) {
            primaryThread = Thread.currentThread()
        }
        if (!this::primaryThread.isInitialized) return

        plugin.scope.launch {
            LockSupport.getBlocker(primaryThread) ?: return@launch
            invokeHeartBeat()
        }
    }

    private fun invokeHeartBeat() {
        craftSchedulerHeartBeatMethod.invoke(scheduler, currentTick)
    }
}