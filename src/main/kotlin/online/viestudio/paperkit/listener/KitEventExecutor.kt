package online.viestudio.paperkit.listener

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import online.viestudio.paperkit.plugin.KitPlugin
import org.bukkit.event.Event
import org.bukkit.event.Listener
import org.bukkit.plugin.EventExecutor
import java.lang.reflect.Method
import kotlin.reflect.full.callSuspend
import kotlin.reflect.jvm.jvmName
import kotlin.reflect.jvm.kotlinFunction

internal class KitEventExecutor(
    private val eventClass: Class<out Event>,
    private val methodListener: Method,
    private val plugin: KitPlugin,
) : EventExecutor {

    override fun execute(listener: Listener, event: Event) {
        if (!eventClass.isAssignableFrom(event::class.java)) return
        val scope = if (event.isAsynchronous) {
            plugin.scope
        } else {
            plugin.serverScope
        }
        scope.launch(start = CoroutineStart.UNDISPATCHED) {
            runCatching {
                methodListener.kotlinFunction?.callSuspend(listener, event)
            }.recoverCatching {
                methodListener.invoke(listener, event)
            }.onFailure {
                plugin.log.w { "Unable to call event listener ${methodListener.toGenericString()} on ${listener::class.jvmName}" }
            }
        }
    }
}