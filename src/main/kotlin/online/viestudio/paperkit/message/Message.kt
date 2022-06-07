package online.viestudio.paperkit.message

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.minimessage.MiniMessage
import online.viestudio.paperkit.placeholder.applyPlaceholders
import online.viestudio.paperkit.utils.lineSeparator

fun message(text: String): Message = text.split(lineSeparator)

typealias Message = List<String>

val Message.asComponent get() = MiniMessage.miniMessage().deserialize(joinToString(lineSeparator))

fun <T : Audience> T.message(message: Message, vararg placeholders: Pair<String, Any>) =
    message(message.applyPlaceholders(*placeholders))

fun <T : Audience> T.message(message: Message) = sendMessage(message.asComponent)

