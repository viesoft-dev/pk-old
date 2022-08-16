@file:Suppress("unused")

package online.viestudio.paperkit.command.argument

import org.bukkit.Server
import kotlin.reflect.KClass
import kotlin.reflect.full.staticFunctions

inline fun Argument.Builder.onlinePlayerValidator(
    server: Server,
    excludeSender: Boolean = true,
    crossinline onNotOnline: suspend () -> String,
) {
    validator { sender, _, input ->
        val onlinePlayers = if (excludeSender) {
            server.onlinePlayers.filter { it.name != sender.name }
        } else server.onlinePlayers
        val player = onlinePlayers.find { it.name == input }
        if (player == null) {
            onNotOnline()
        } else null
    }
}

inline fun Argument.Builder.intValidator(crossinline onNotInt: suspend () -> String) {
    validator { _, _, input ->
        val int = input.toIntOrNull()
        if (int == null) {
            onNotInt()
        } else null
    }
}

inline fun Argument.Builder.decimalValidator(crossinline onNotDec: suspend () -> String) {
    validator { _, _, input ->
        val int = input.toDoubleOrNull()
        if (int == null) {
            onNotDec()
        } else null
    }
}

inline fun Argument.Builder.argsValidator(crossinline block: suspend (args: Array<String>, input: String) -> String?) {
    validator { _, args, input -> block(args, input) }
}

inline fun Argument.Builder.inputValidator(crossinline validator: suspend (String) -> String?) {
    validator { _, _, input -> validator(input) }
}

inline fun <T : Any> Argument.Builder.enumValidator(
    enum: KClass<T>,
    crossinline prepare: suspend (String) -> String = { it },
    crossinline onNotEnum: suspend () -> String,
) {
    validator { _, _, input ->
        val preparedInput = prepare(input)
        val enumInstance = runCatching {
            enum.staticFunctions.find { it.name == "valueOf" }!!.call(preparedInput)
        }.getOrNull()
        if (enumInstance == null) {
            onNotEnum()
        } else null
    }
}