package online.viestudio.paperkit.command

import kotlinx.coroutines.launch
import online.viestudio.paperkit.koin.plugin
import online.viestudio.paperkit.plugin.KitPlugin
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.koin.core.component.KoinComponent

class KitCommandExecutor(
    private val command: KitCommand,
) : CommandExecutor, KoinComponent {

    private val plugin: KitPlugin by plugin()

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        plugin.serverScope.launch { this@KitCommandExecutor.command.execute(sender, args) }
        return true
    }
}