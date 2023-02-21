package dev.viesoft.paperkit.core.coroutines

import dev.viesoft.paperkit.core.plugin.IKotlinPlugin
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Delay
import kotlinx.coroutines.Runnable
import kotlin.coroutines.CoroutineContext

internal class AsyncPluginDispatcher(
    private val plugin: IKotlinPlugin,
    private val controller: PluginCoroutineController
) : CoroutineDispatcher(), Delay {

    private val server get() = plugin.server

    override fun scheduleResumeAfterDelay(timeMillis: Long, continuation: CancellableContinuation<Unit>) {
        val task = server.scheduler.runTaskLaterAsynchronously(
            plugin,
            Runnable { continuation.apply { resumeUndispatched(Unit) } },
            timeMillis / 50
        )
        continuation.invokeOnCancellation { task.cancel() }
    }

    override fun isDispatchNeeded(context: CoroutineContext): Boolean {
        controller.unblockIfNeeded()
        return plugin.server.isPrimaryThread
    }

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        if (!plugin.isEnabled) return
        server.scheduler.runTaskAsynchronously(plugin, block)
    }
}
