package online.viestudio.paperkit.command.argument

import kotlin.reflect.KClass
import kotlin.reflect.full.staticFunctions

fun <T> Argument.Builder.completer(vararg elements: T, prepare: (T) -> String = { it.toString() }) {
    completer { _, _ -> elements.map(prepare) }
}

@Suppress("UNCHECKED_CAST")
fun <T : Any> Argument.Builder.enumCompleter(enum: KClass<T>, prepare: (T) -> String = { it.toString() }) {
    completer { _, _ ->
        val values = enum.staticFunctions.find { it.name == "values" }!!.call() as Array<T>
        values.map(prepare)
    }
}