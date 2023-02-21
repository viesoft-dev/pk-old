package dev.viesoft.paperkit.core.util

import dev.viesoft.paperkit.core.plugin.IKotlinPlugin
import org.bukkit.plugin.Plugin

internal fun Plugin.expectKotlin(): IKotlinPlugin {
    return this as? IKotlinPlugin ?: error(buildString {
        append("Consider implementing the ")
        append(IKotlinPlugin::class.qualifiedName)
        append(" interface, if you are trying to provide a custom implementation.")
    })
}
