@file:Suppress("unused")

package online.viestudio.paperkit.listener

import online.viestudio.paperkit.bukkit.getHandlerListFor
import online.viestudio.paperkit.koin.plugin
import online.viestudio.paperkit.plugin.KitPlugin
import org.bukkit.Warning
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.AuthorNagException
import org.bukkit.plugin.RegisteredListener
import org.koin.core.component.KoinComponent
import java.lang.Deprecated
import java.lang.reflect.Method
import kotlin.Any
import kotlin.Suppress
import kotlin.apply
import kotlin.getOrElse
import kotlin.getValue
import kotlin.reflect.jvm.jvmName
import kotlin.run
import kotlin.runCatching

private val Method.eventHandlerAnnotation: EventHandler? get() = getAnnotation(EventHandler::class.java)

private val Any.eventMethods: Set<Method>
    get() = mutableListOf<Method>().apply {
        addAll(this@eventMethods.javaClass.methods)
        addAll(this@eventMethods.javaClass.declaredMethods)
    }.filter { !it.isBridge && !it.isSynthetic }.toSet()

private val Method.eventClass: Class<out Event>?
    get() = runCatching {
        parameterTypes[0].asSubclass(Event::class.java)
    }.getOrElse { return null }

@Suppress("NOTHING_TO_INLINE")
inline fun KitListener.register() {
    val plugin by plugin<KitPlugin>()
    register(plugin)
}

fun KitListener.register(plugin: KitPlugin) {
    val pluginManager = plugin.server.pluginManager
    val listeners = listeners(plugin)
    listeners.forEach { (clazz, registeredListeners) ->
        val handlerList = pluginManager.getHandlerListFor(clazz)
        handlerList.registerAll(registeredListeners)
    }
}

private fun KitListener.listeners(plugin: KitPlugin): Map<Class<out Event>, MutableSet<RegisteredListener>> {
    val eventMethods = eventMethods
    val listeners = mutableMapOf<Class<out Event>, MutableSet<RegisteredListener>>()

    fun processListener(method: Method, eventClass: Class<out Event>, eventHandler: EventHandler) {
        eventClass.logWarnIfDeprecated(method, plugin)
        val executor = KitEventExecutor(eventClass, method, plugin)
        val listener = RegisteredListener(this, executor, eventHandler.priority, plugin, eventHandler.ignoreCancelled)
        listeners.computeIfAbsent(eventClass) { mutableSetOf() }.add(listener)
    }

    eventMethods.forEach { method ->
        val eventHandler = method.eventHandlerAnnotation ?: return@forEach
        val eventClass = method.eventClass ?: run {
            plugin.log.w {
                """
                    Unable to register listener ${method.name} on ${this::class.jvmName}.
                    Expected single parameter of type ${Event::class.jvmName}.
                    Actual: ${method.toGenericString()}
                    
                    This problem is related to ${plugin.name} plugin, report it to the developers.
                """
            }
            return@forEach
        }
        processListener(method, eventClass, eventHandler)
    }
    return listeners
}

private fun Class<*>.logWarnIfDeprecated(method: Method, plugin: KitPlugin) {
    while (Event::class.java.isAssignableFrom(this)) {
        if (getAnnotation(Deprecated::class.java) == null) superclass.logWarnIfDeprecated(method, plugin)
        val warning = getAnnotation(Warning::class.java) ?: return
        if (!plugin.server.warningState.printFor(warning)) return
        plugin.log.w(AuthorNagException(null)) {
            """
                Registered listener for Deprecated event $name on method ${method.toGenericString()}.
                ${warning.reason.ifEmpty { "Server performance will be affected." }}
                
                This problem is related to ${plugin.name} plugin, report it to the developers.
            """
        }
    }
}

fun KitListener.unregister() {
    HandlerList.unregisterAll(this)
}

interface KitListener : Listener, KoinComponent
