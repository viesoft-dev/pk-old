package dev.viesoft.paperkit.core.command

import org.bukkit.command.Command
import org.bukkit.command.CommandSender

/**
 * Kotlin alternative for the [org.bukkit.command.CommandExecutor] interface.
 * @see dev.viesoft.paperkit.core.command.setKotlinExecutor
 */
fun interface KotlinCommandExecutor {

    suspend fun execute(sender: CommandSender, command: Command, label: String, args: List<String>): Boolean
}
