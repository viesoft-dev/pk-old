package dev.viesoft.paperkit.core.command

import org.bukkit.command.Command
import org.bukkit.command.CommandSender

/**
 * Kotlin alternative for the [org.bukkit.command.TabCompleter] interface.
 * @see dev.viesoft.paperkit.core.command.setKotlinTabCompleter
 */
fun interface KotlinTabCompleter {

    suspend fun tabComplete(sender: CommandSender, command: Command, label: String, args: List<String>): List<String>?
}
