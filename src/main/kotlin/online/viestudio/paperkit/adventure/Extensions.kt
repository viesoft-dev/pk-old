@file:Suppress("unused")

package online.viestudio.paperkit.adventure

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

fun hexColor(hex: String) = TextColor.fromHexString(hex)

val Component.asPlainText: String get() = PlainTextComponentSerializer.plainText().serialize(this)

val String.asComponent: Component get() = MiniMessage.miniMessage().deserialize(this)

val Audience.name
    get() = when (this) {
        is Player -> name
        is CommandSender -> name
        else -> toString()
    }

inline fun text(block: TextComponent.Builder.() -> Unit) = Component.text().apply(block).build()

inline fun TextComponent.Builder.showTextOnHover(block: TextComponent.Builder.() -> Unit) {
    hoverEvent(HoverEvent.showText(text(block)))
}

inline fun TextComponent.Builder.appendText(block: TextComponent.Builder.() -> Unit): TextComponent.Builder {
    append(text(block))
    return this
}

infix fun TextComponent.Builder.appendText(string: String): TextComponent.Builder {
    append(Component.text(string))
    return this
}
