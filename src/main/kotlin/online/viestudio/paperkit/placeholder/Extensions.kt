package online.viestudio.paperkit.placeholder

import online.viestudio.paperkit.message.Message

const val PLACEHOLDER_FORMAT = "{%s}"

fun Message.applyPlaceholders(vararg placeholders: Pair<String, Any>) = map { it.applyPlaceholders(*placeholders) }

fun String.applyPlaceholders(vararg placeholders: Pair<String, Any>): String {
    var temp = this
    placeholders.forEach {
        temp = temp.replace(PLACEHOLDER_FORMAT.format(it.first), it.second.toString())
    }
    return temp
}