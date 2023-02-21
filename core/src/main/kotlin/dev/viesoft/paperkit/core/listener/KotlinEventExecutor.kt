package dev.viesoft.paperkit.core.listener

import dev.viesoft.paperkit.core.plugin.IKotlinPlugin
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import org.bukkit.event.Event
import org.bukkit.event.Listener
import org.bukkit.plugin.EventExecutor
import java.lang.reflect.Method
import kotlin.reflect.full.callSuspend
import kotlin.reflect.jvm.jvmName
import kotlin.reflect.jvm.kotlinFunction

internal class KotlinEventExecutor(
    private val eventClass: Class<out Event>,
    private val methodListener: Method,
    private val plugin: IKotlinPlugin,
) : EventExecutor {

    override fun execute(listener: Listener, event: Event) {
        if (!eventClass.isAssignableFrom(event::class.java)) return
        plugin.scope.launch(start = CoroutineStart.UNDISPATCHED) {
            runCatching {
                if (methodListener.kotlinFunction?.isSuspend == true) {
                    methodListener.kotlinFunction!!.callSuspend(listener, event)
                } else {
                    methodListener.invoke(listener, event)
                }
            }.onFailure {
                plugin.log.warn(it) {
                    "Unable to call event listener ${methodListener.toGenericString()} on ${listener::class.jvmName}"
                }
            }
        }
    }
}
