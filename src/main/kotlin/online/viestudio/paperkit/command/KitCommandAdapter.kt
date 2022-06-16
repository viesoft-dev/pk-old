package online.viestudio.paperkit.command

import kotlinx.coroutines.runBlocking
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class KitCommandAdapter(
    private val command: KitCommand,
) : Command(command.name, command.description, "", command.aliases) {

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<String>): Boolean {
        runBlocking { command.execute(sender, args) }
        return true
    }

    override fun tabComplete(sender: CommandSender, alias: String, args: Array<String>): MutableList<String> {
        val currentArgument = args.lastOrNull() ?: return mutableListOf()
        return runBlocking { command.complete(sender, args) }.filter {
            it.startsWith(currentArgument, true)
        }.toMutableList()
    }

    fun isAdapterOf(kitCommand: KitCommand) = kitCommand === command
}
