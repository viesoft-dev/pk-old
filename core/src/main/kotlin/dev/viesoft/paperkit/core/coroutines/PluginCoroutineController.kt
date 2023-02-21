package dev.viesoft.paperkit.core.coroutines

import dev.viesoft.paperkit.core.plugin.IKotlinPlugin
import kotlinx.coroutines.*
import java.util.concurrent.locks.LockSupport

internal class PluginCoroutineController(private val plugin: IKotlinPlugin) {

    private val server get() = plugin.server

    private var isEnabled: Boolean = false
    private val currentTick get() = craftSchedulerTickField.get(scheduler)
    private val scheduler get() = server.scheduler
    private val craftSchedulerTickField by lazy {
        craftSchedulerClazz.getDeclaredField("currentTick").apply { isAccessible = true }
    }
    private val craftSchedulerHeartBeatMethod by lazy {
        craftSchedulerClazz.getDeclaredMethod("mainThreadHeartbeat", Int::class.java)
    }
    private val craftSchedulerClazz
        get() = Class.forName("org.bukkit.craftbukkit.%s.scheduler.CraftScheduler".format(nmsVersion))
    private val nmsVersion: String
        get() = javaClass.getPackage().name.replace(".", ",").split(",")[3]
    private val isPrimaryThreadBlocked get() = LockSupport.getBlocker(primaryThread) != null
    private lateinit var primaryThread: Thread

    fun runControlled(block: suspend CoroutineScope.() -> Unit) {
        isEnabled = true
        val result = runBlocking {
            runCatching { block() }
        }
        isEnabled = false
        result.getOrThrow()
    }

    fun unblockIfNeeded() {
        if (!isEnabled) return
        if (server.isPrimaryThread) {
            primaryThread = Thread.currentThread()
        }
        if (!this::primaryThread.isInitialized) return
        if (isPrimaryThreadBlocked) {
            GlobalScope.launch(Dispatchers.Default) { invokeHeartBeat() }
        }
    }

    private fun invokeHeartBeat() {
        craftSchedulerHeartBeatMethod.invoke(scheduler, currentTick)
    }
}
