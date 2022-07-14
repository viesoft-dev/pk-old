package online.viestudio.paperkit.plugin

import kotlinx.coroutines.CoroutineScope
import online.viestudio.paperkit.lifecycle.LifecycleListener
import online.viestudio.paperkit.listener.KitListener
import online.viestudio.paperkit.logger.KitLogger
import online.viestudio.paperkit.theme.Appearance
import org.bukkit.plugin.Plugin
import org.koin.core.component.KoinComponent
import kotlin.coroutines.CoroutineContext

interface KitPlugin : Plugin, KoinComponent {

    val state: State
    val log: KitLogger
    val version: String
    val appearance: Appearance

    /**
     * For running asynchronous tasks related to this plugin.
     *
     */
    val scope: CoroutineScope

    /**
     * For running asynchronous tasks related to this plugin.
     */
    val context: CoroutineContext

    /**
     * For running server tasks in main thread.
     */
    val serverScope: CoroutineScope

    /**
     * For running server tasks in main thread.
     */
    val serverContext: CoroutineContext

    /**
     * Starts all plugin processes.
     * Require plugin to be in [State.Stopped] state.
     *
     * @return true if started, or false if starting failed, or already is going.
     */
    suspend fun start(): Boolean

    /**
     * Stops all plugin processes.
     * Require plugin to be in [State.Started] state.
     *
     * @return true if stopped, or false if stopping already is going.
     */
    suspend fun stop(): Boolean

    /**
     * Reloads plugin resources such as configuration.
     * Require plugin to be in [State.Starting] or [State.Started] state.
     *
     * @return true if reloaded, or false if reloading failed, or plugin isn't started.
     */
    suspend fun reloadResources(): Boolean

    /**
     * Bind [KitListener] (un)registration to [KitPlugin] lifecycle.
     */
    suspend fun bindListener(listener: KitListener)

    /**
     * Register [LifecycleListener] to [KitPlugin] lifecycle.
     */
    suspend fun registerLifecycleListener(listener: LifecycleListener)

    /**
     * Represents state of plugin lifecycle.
     * @see KitPlugin
     */
    enum class State { Starting, Started, Stopping, Stopped, }
}
