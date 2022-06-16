package online.viestudio.paperkit.plugin

import kotlinx.coroutines.*
import online.viestudio.paperkit.paper.ServerCoroutineController
import online.viestudio.paperkit.paper.ServerCoroutineDispatcher
import kotlin.coroutines.CoroutineContext

abstract class ScopeKitPlugin(
    nThreads: Int,
) : BasicKitPlugin() {

    @OptIn(DelicateCoroutinesApi::class)
    final override val scope: CoroutineScope by lazy {
        CoroutineScope(exceptionHandler + SupervisorJob() + newFixedThreadPoolContext(nThreads, name))
    }
    final override val context: CoroutineContext get() = scope.coroutineContext
    final override val serverContext: CoroutineContext get() = serverScope.coroutineContext
    final override val serverScope: CoroutineScope by lazy {
        CoroutineScope(exceptionHandler + SupervisorJob() + serverCoroutineDispatcher)
    }
    private val serverCoroutineDispatcher by lazy {
        ServerCoroutineDispatcher(this, serverCoroutineController)
    }
    private val serverCoroutineController by lazy {
        ServerCoroutineController(this)
    }
    private val exceptionHandler
        get() = CoroutineExceptionHandler { _, throwable ->
            log.w(throwable) { "An unexpected exception has occurred" }
        }

    final override fun reloadConfig() {
        runBlocking {
            reloadResources()
        }
    }

    final override fun onLoad() {
        context.ensureActive()
        serverContext.ensureActive()
        runBlocking {
            prepareToStart()
        }
    }

    protected abstract suspend fun prepareToStart()

    final override fun onEnable() {
        serverCoroutineController.runControlled {
            start()
        }
    }

    final override fun onDisable() {
        runBlocking {
            stop()
            freeUpResources()
        }
        with(CancellationException("Disabling plugin")) {
            context.cancelChildren(this)
            serverContext.cancelChildren(this)
            scope.cancel(this)
            serverScope.cancel(this)
        }
    }

    protected abstract suspend fun freeUpResources()
}
