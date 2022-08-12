package online.viestudio.paperkit.command

import online.viestudio.paperkit.adventure.appendText
import online.viestudio.paperkit.adventure.showTextOnHover
import online.viestudio.paperkit.adventure.text
import online.viestudio.paperkit.style.buildBeautifulHelp
import online.viestudio.paperkit.util.lineSeparator
import org.bukkit.command.CommandSender

abstract class ParentCommand(
    subCommands: List<KitCommand>,
) : BaseKitCommand(subCommands) {


    override suspend fun CommandSender.sendHelp() {
        val component = with(appearance) {
            text {
                append(prefix)
                appendText {
                    content(" ${name.replaceFirstChar { it.uppercase() }}")
                    color(accent)
                }
                showTextOnHover {
                    content(description)
                    color(accent)
                }
                appendText(lineSeparator)
                subCommands.forEachIndexed { index, subCommand ->
                    if (!hasPermission(subCommand.permission)) return@forEachIndexed
                    append(subCommand.buildBeautifulHelp())
                    if (index != subCommands.lastIndex) {
                        appendText(lineSeparator)
                    }
                }
            }
        }
        sendMessage(component)
    }

    final override suspend fun onExecute(sender: CommandSender, args: Arguments): Boolean = false
}
