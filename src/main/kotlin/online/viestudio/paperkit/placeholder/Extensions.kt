package online.viestudio.paperkit.placeholder

const val PLACEHOLDER_FORMAT = "{%s}"

fun List<String>.applyPlaceholders(vararg placeholders: Pair<String, Any>) = map { it.applyPlaceholders(*placeholders) }

fun String.applyPlaceholders(vararg placeholders: Pair<String, Any>): String {
    var temp = this
    placeholders.forEach {
        temp = temp.replace(PLACEHOLDER_FORMAT.format(it.first), it.second.toString())
    }
    return temp
}
