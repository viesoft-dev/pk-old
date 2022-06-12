package online.viestudio.paperkit.message

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.minimessage.MiniMessage
import online.viestudio.paperkit.placeholder.applyPlaceholders
import online.viestudio.paperkit.util.lineSeparator

fun message(text: String): List<String> = text.split(lineSeparator)

val List<String>.asComponent get() = MiniMessage.miniMessage().deserialize(joinToString(lineSeparator))

fun <T : Audience> T.message(message: List<String>, vararg placeholders: Pair<String, Any>) =
    message(message.applyPlaceholders(*placeholders))

fun <T : Audience> T.message(message: List<String>) = sendMessage(message.asComponent)

