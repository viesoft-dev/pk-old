package online.viestudio.paperkit.bukkit

import online.viestudio.paperkit.nms.nmsVersion
import org.bukkit.Server
import org.bukkit.entity.Player

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

internal fun Server.syncCommands() {
    this::class.java.getMethod("syncCommands").invoke(this)
}

inline fun Server.onlinePlayers(action: (Player) -> Unit) = onlinePlayers.forEach(action)