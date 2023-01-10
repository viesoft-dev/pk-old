package online.viestudio.paperkit.paper

import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Delay
import kotlinx.coroutines.Runnable
import online.viestudio.paperkit.plugin.KitPlugin
import kotlin.coroutines.CoroutineContext

internal class ServerCoroutineDispatcher(
    private val plugin: KitPlugin,
    private val controller: ServerCoroutineController,
) : CoroutineDispatcher(), Delay {

    override fun scheduleResumeAfterDelay(timeMillis: Long, continuation: CancellableContinuation<Unit>) {
        val task = plugin.server.scheduler.runTaskLater(
            plugin,
            Runnable {
                continuation.apply { resumeUndispatched(Unit) }
            },
            timeMillis / 50
        )
        continuation.invokeOnCancellation { task.cancel() }
    }

    override fun isDispatchNeeded(context: CoroutineContext): Boolean {
        controller.unblockIfNeeded()
        return !plugin.server.isPrimaryThread
    }

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        with(plugin) {
            if (!isEnabled) return
            server.scheduler.runTask(plugin, block)
        }
    }
}
