package online.viestudio.paperkit.paper

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Runnable
import online.viestudio.paperkit.plugin.KitPlugin
import kotlin.coroutines.CoroutineContext

internal class ServerCoroutineDispatcher(
    private val plugin: KitPlugin,
    private val controller: ServerCoroutineController,
) : CoroutineDispatcher() {

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