package online.viestudio.paperkit.plugin

import kotlinx.coroutines.CoroutineScope
import online.viestudio.paperkit.logger.KitLogger
import org.bukkit.plugin.Plugin
import org.koin.core.component.KoinComponent
import kotlin.coroutines.CoroutineContext

interface KitPlugin : Plugin, KoinComponent {

    val state: State
    val scope: CoroutineScope
    val context: CoroutineContext
    val serverScope: CoroutineScope
    val serverContext: CoroutineContext
    val log: KitLogger
    val isPrimaryThread: Boolean
    val version: String

    suspend fun start(): Boolean

    suspend fun stop(): Boolean

    suspend fun reload(): Boolean

    enum class State {
        Starting,
        Started,
        Stopping,
        Stopped,
    }
}