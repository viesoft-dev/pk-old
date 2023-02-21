package dev.viesoft.paperkit.core.command

import dev.viesoft.paperkit.core.util.expectKotlin
import kotlinx.coroutines.launch
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.PluginCommand

inline fun PluginCommand.setKotlinTabCompleter(
    crossinline kotlinTabCompleter: suspend (
        sender: CommandSender,
        command: Command,
        label: String,
        args: List<String>
    ) -> List<String>
) = apply {
    setKotlinTabCompleter(KotlinTabCompleter { sender, command, label, args ->
        kotlinTabCompleter(sender, command, label, args)
    })
}

fun PluginCommand.setKotlinTabCompleter(kotlinTabCompleter: KotlinTabCompleter) = apply {
    val plugin = plugin.expectKotlin()

    setTabCompleter { sender, command, label, args ->
        var tabComplete: List<String>? = null
        plugin.scope.launch {
            tabComplete = kotlinTabCompleter.tabComplete(sender, command, label, args.toList())
        }
        tabComplete
    }
}

inline fun PluginCommand.setKotlinExecutor(
    crossinline kotlinCommandExecutor: suspend (
        sender: CommandSender,
        command: Command,
        label: String,
        args: List<String>
    ) -> Boolean
) = apply {
    setKotlinExecutor(KotlinCommandExecutor { sender, command, label, args ->
        kotlinCommandExecutor(sender, command, label, args)
    })
}

fun PluginCommand.setKotlinExecutor(kotlinCommandExecutor: KotlinCommandExecutor) = apply {
    val plugin = plugin.expectKotlin()

    setExecutor { sender, command, label, args ->
        var isSuccessful = false
        plugin.scope.launch {
            isSuccessful = kotlinCommandExecutor.execute(sender, command, label, args.toList())
        }
        isSuccessful
    }
}
