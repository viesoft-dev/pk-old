package online.viestudio.paperkit.command

import kotlinx.coroutines.launch
import online.viestudio.paperkit.koin.plugin
import online.viestudio.paperkit.plugin.KitPlugin
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.koin.core.component.KoinComponent

class KitCommandTabCompleter(
    private val kitCommand: KitCommand,
) : TabCompleter, KoinComponent {

    private val plugin: KitPlugin by plugin()

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>,
    ): MutableList<String> {
        val currentArgument = args.lastOrNull() ?: return mutableListOf()
        var result = emptyList<String>()
        plugin.serverScope.launch {
            result = kitCommand.complete(sender, args).filter {
                it.startsWith(currentArgument, true)
            }
        }
        return result.toMutableList()
    }
}
