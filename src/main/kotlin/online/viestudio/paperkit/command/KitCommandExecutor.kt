package online.viestudio.paperkit.command

import kotlinx.coroutines.runBlocking
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class KitCommandExecutor(
    private val command: KitCommand,
) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        runBlocking { this@KitCommandExecutor.command.execute(sender, args) }
        return true
    }
}