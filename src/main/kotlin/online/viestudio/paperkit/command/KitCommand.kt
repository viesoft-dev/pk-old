package online.viestudio.paperkit.command

import net.kyori.adventure.text.Component
import online.viestudio.paperkit.command.argument.Argument
import online.viestudio.paperkit.theme.Theme
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.koin.core.component.KoinComponent

typealias Arguments = Array<String>

fun KitCommand.executor(): CommandExecutor = KitCommandExecutor(this)

fun KitCommand.tabCompleter(): TabCompleter = KitCommandTabCompleter(this)

fun KitCommand.adapter(): Command = KitCommandAdapter(this)

interface KitCommand : KoinComponent {

    val name: String
    val aliases: List<String>
    val description: String
    val permission: String
    val theme: Theme
    val help: Component
    val declaredArguments: List<Argument>
    val minArguments: Int
    val maxArguments: Int
    val subCommands: List<KitCommand>

    suspend fun ensureInit()

    suspend fun execute(sender: CommandSender, args: Arguments)

    suspend fun complete(sender: CommandSender, args: Arguments): List<String>
}