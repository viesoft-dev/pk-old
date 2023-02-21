@file:Suppress("unused")

package dev.viesoft.paperkit.core.listener

import dev.viesoft.paperkit.core.plugin.IKotlinPlugin
import org.bukkit.Warning
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.AuthorNagException
import org.bukkit.plugin.IllegalPluginAccessException
import org.bukkit.plugin.PluginManager
import org.bukkit.plugin.RegisteredListener
import java.lang.Deprecated
import java.lang.reflect.Method
import kotlin.Any
import kotlin.Suppress
import kotlin.apply
import kotlin.getOrElse
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

private fun KotlinListener.listeners(plugin: IKotlinPlugin): Map<Class<out Event>, MutableSet<RegisteredListener>> {
    val eventMethods = eventMethods
    val listeners = mutableMapOf<Class<out Event>, MutableSet<RegisteredListener>>()

    fun processListener(method: Method, eventClass: Class<out Event>, eventHandler: EventHandler) {
        eventClass.logWarnIfDeprecated(method, plugin)
        val executor = KotlinEventExecutor(eventClass, method, plugin)
        val listener =
            RegisteredListener(this, executor, eventHandler.priority, plugin, eventHandler.ignoreCancelled)
        listeners.computeIfAbsent(eventClass) { mutableSetOf() }.add(listener)
    }

    eventMethods.forEach { method ->
        val eventHandler = method.eventHandlerAnnotation ?: return@forEach
        val eventClass = method.eventClass ?: run {
            plugin.log.warn {
                """
                    Unable to register listener ${method.name} on ${this::class.jvmName}.
                    Expected single parameter of type ${Event::class.jvmName}.
                    Actual: ${method.toGenericString()}
                    
                    This problem is related to the ${plugin.name} plugin, report to its developers.
                """
            }
            return@forEach
        }
        processListener(method, eventClass, eventHandler)
    }
    return listeners
}

private fun Class<*>.logWarnIfDeprecated(method: Method, plugin: IKotlinPlugin) {
    while (Event::class.java.isAssignableFrom(this)) {
        if (getAnnotation(Deprecated::class.java) == null) superclass.logWarnIfDeprecated(method, plugin)
        val warning = getAnnotation(Warning::class.java) ?: return
        if (!plugin.server.warningState.printFor(warning)) return
        plugin.log.warn(AuthorNagException(null)) {
            """
                Registered listener for Deprecated event $name on method ${method.toGenericString()}.
                ${warning.reason.ifEmpty { "Server performance will be affected." }}
                
                This problem is related to the ${plugin.name} plugin, report to its developers.
            """
        }
    }
}

private fun PluginManager.getHandlerListFor(event: Class<out Event>): HandlerList {
    val registrationClass = getRegistrationClass(event)
    val method = registrationClass.getDeclaredMethod("getHandlerList").apply { isAccessible = true }
    return method(this, event) as HandlerList
}

private fun getRegistrationClass(clazz: Class<out Event>): Class<out Event> {
    return try {
        clazz.getDeclaredMethod("getHandlerList")
        clazz
    } catch (e: NoSuchMethodException) {
        if (clazz.superclass != null && clazz.superclass != Event::class.java
            && Event::class.java.isAssignableFrom(clazz.superclass)
        ) {
            getRegistrationClass(clazz.superclass.asSubclass(Event::class.java))
        } else {
            throw IllegalPluginAccessException("Unable to find handler list for event " + clazz.name + ". Static getHandlerList method required!")
        }
    }
}

interface KotlinListener : Listener {

    fun register(plugin: IKotlinPlugin) {
        val pluginManager = plugin.server.pluginManager
        val listeners = listeners(plugin)
        listeners.forEach { (clazz, registeredListeners) ->
            val handlerList = pluginManager.getHandlerListFor(clazz)
            handlerList.registerAll(registeredListeners)
        }
    }

    fun unregister() {
        HandlerList.unregisterAll(this)
    }
}
