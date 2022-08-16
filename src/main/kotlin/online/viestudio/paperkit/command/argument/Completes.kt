@file:Suppress("unused")

package online.viestudio.paperkit.command.argument

import org.bukkit.Server
import kotlin.reflect.KClass
import kotlin.reflect.full.staticFunctions

fun Argument.Builder.onlinePlayersCompleter(server: Server, excludeSender: Boolean = true) {
    completer { sender, _, _ ->
        val nicknames = server.onlinePlayers.map { it.name }
        if (excludeSender) {
            nicknames.filter { it != sender.name }
        } else nicknames
    }
}

inline fun Argument.Builder.emptyCompleter(crossinline block: suspend () -> List<String>) {
    completer { _, _, _ -> block() }
}

inline fun Argument.Builder.argsCompleter(crossinline block: suspend (args: Array<String>, input: String) -> List<String>) {
    completer { _, args, input -> block(args, input) }
}

inline fun Argument.Builder.inputCompleter(crossinline block: suspend (input: String) -> List<String>) {
    completer { _, _, input -> block(input) }
}

fun <T> Argument.Builder.completer(vararg elements: T, prepare: suspend (T) -> String = { it.toString() }) {
    completer { _, _, _ -> elements.map { prepare(it) } }
}

@Suppress("UNCHECKED_CAST")
fun <T : Any> Argument.Builder.enumCompleter(enum: KClass<T>, prepare: suspend (T) -> String = { it.toString() }) {
    completer { _, _, _ ->
        val values = enum.staticFunctions.find { it.name == "values" }!!.call() as Array<T>
        values.map { prepare(it) }
    }
}