package online.viestudio.paperkit.command

import kotlinx.coroutines.runBlocking
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class KitCommandTabCompleter(
    private val kitCommand: KitCommand,
) : TabCompleter {

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>,
    ): MutableList<String> {
        val currentArgument = args.lastOrNull() ?: return mutableListOf()
        return runBlocking {
            kitCommand.complete(sender, args).filter {
                it.startsWith(currentArgument, true)
            }.toMutableList()
        }
    }
}