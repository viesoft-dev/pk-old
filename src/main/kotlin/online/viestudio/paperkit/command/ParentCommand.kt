package online.viestudio.paperkit.command

import net.kyori.adventure.text.Component
import online.viestudio.paperkit.adventure.appendText
import online.viestudio.paperkit.adventure.showTextOnHover
import online.viestudio.paperkit.adventure.text
import online.viestudio.paperkit.utils.lineSeparator
import org.bukkit.command.CommandSender

abstract class ParentCommand(
    override val name: String,
    override val aliases: List<String> = emptyList(),
    override val description: String,
    override val permission: String,
    override val subCommands: List<KitCommand>,
) : BaseKitCommand() {

    override val help: Component by lazy {
        text {
            content("——> /$name")
            color(theme.accent)
            showTextOnHover {
                content(description)
                color(theme.accent)
            }
            appendText(lineSeparator)

            subCommands.forEachIndexed { index, subCommand ->
                append(subCommand.help)
                if (index != subCommands.lastIndex) {
                    appendText(lineSeparator)
                }
            }
        }
    }

    final override suspend fun onExecute(sender: CommandSender, args: Arguments): Boolean = false
}