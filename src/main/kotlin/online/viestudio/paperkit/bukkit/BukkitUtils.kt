package online.viestudio.paperkit.bukkit

import online.viestudio.paperkit.nms.nmsVersion
import org.bukkit.Server
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.plugin.PluginManager
import org.bukkit.plugin.SimplePluginManager

internal val Server.craftSchedulerTickField
    get() = craftSchedulerClazz.getDeclaredField("currentTick").apply {
        isAccessible = true
    }

internal val Server.craftSchedulerHeartBeatMethod
    get() = craftSchedulerClazz.getDeclaredMethod("mainThreadHeartbeat", Int::class.java)

internal val Server.craftSchedulerClazz
    get() = Class.forName(
        "org.bukkit.craftbukkit.%s.scheduler.CraftScheduler".format(
            nmsVersion
        )
    )

internal fun PluginManager.getHandlerListFor(event: Class<out Event>): HandlerList {
    val method = SimplePluginManager::class.java.getDeclaredMethod("getEventListeners", Class::class.java).apply {
        isAccessible = true
    }
    return method(this, event) as HandlerList
}

internal fun Server.syncCommands() {
    this::class.java.getMethod("syncCommands").invoke(this)
}

inline fun Server.onlinePlayers(action: (Player) -> Unit) = onlinePlayers.forEach(action)