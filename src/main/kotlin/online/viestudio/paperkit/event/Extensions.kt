@file:Suppress("unused")

package online.viestudio.paperkit.event

import online.viestudio.paperkit.plugin.KitPlugin
import org.bukkit.event.Event

fun KitPlugin.callEvent(event: Event) = server.pluginManager.callEvent(event)