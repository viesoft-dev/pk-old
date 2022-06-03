package online.viestudio.paperkit.paper

import kotlinx.coroutines.launch
import online.viestudio.paperkit.bukkit.craftSchedulerHeartBeatMethod
import online.viestudio.paperkit.bukkit.craftSchedulerTickField
import online.viestudio.paperkit.plugin.KitPlugin
import org.bukkit.Server
import java.util.concurrent.locks.LockSupport

internal class PrimaryCoroutineController(
    private val plugin: KitPlugin
) {

    private val currentTick get() = craftSchedulerTickField.get(scheduler)
    private val craftSchedulerTickField get() = server.craftSchedulerTickField
    private val scheduler get() = server.scheduler
    private val craftSchedulerHeartBeatMethod get() = server.craftSchedulerHeartBeatMethod
    private val server: Server get() = plugin.server
    private lateinit var primaryThread: Thread

    fun unblockIfNeeded() {
        if (plugin.isPrimaryThread) {
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