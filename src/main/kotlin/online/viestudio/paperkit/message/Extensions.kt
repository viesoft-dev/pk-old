@file:Suppress("unused")

package online.viestudio.paperkit.message

import net.kyori.adventure.audience.Audience
import online.viestudio.paperkit.adventure.asComponent
import online.viestudio.paperkit.adventure.name
import online.viestudio.paperkit.placeholder.applyPlaceholders
import online.viestudio.paperkit.util.lineSeparator
import org.bukkit.inventory.meta.ItemMeta

val Message.asComponent get() = content.joinToString(lineSeparator).asComponent

fun message(content: String) = Message(content.split(lineSeparator))

fun Audience.message(content: String) = message(online.viestudio.paperkit.message.message(content))

fun Audience.message(message: Message, vararg placeholders: Pair<String, Any>) =
    message(message.applyPlaceholders(*placeholders))

fun Audience.message(message: Message) {
    sendMessage(message.content.joinToString(lineSeparator).applyPlaceholders("target" to name).asComponent)
}

fun Message.applyPlaceholders(vararg placeholders: Pair<String, Any>): Message =
    copy(content = content.applyPlaceholders(*placeholders))

fun ItemMeta.displayName(message: Message) = displayName(message.asComponent)

fun ItemMeta.lore(message: Message) = lore(message.content.map { it.asComponent })