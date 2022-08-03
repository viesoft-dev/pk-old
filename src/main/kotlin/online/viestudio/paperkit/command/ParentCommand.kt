package online.viestudio.paperkit.command

import net.kyori.adventure.text.Component
import online.viestudio.paperkit.adventure.appendText
import online.viestudio.paperkit.adventure.showTextOnHover
import online.viestudio.paperkit.adventure.text
import online.viestudio.paperkit.style.buildBeautifulHelp
import online.viestudio.paperkit.util.lineSeparator
import org.bukkit.command.CommandSender

abstract class ParentCommand(
    subCommands: List<KitCommand>,
    strategy: SubCommandStrategy = SubCommandStrategy.FIRST,
) : BaseKitCommand(subCommands, strategy) {

    override val help: Component
        get() = text {
            append(appearance.prefix)
            appendText {
                content(" ${name.replaceFirstChar { it.uppercase() }}")
                color(appearance.accent)
            }
            showTextOnHover {
                content(description)
                color(appearance.accent)
            }
            appendText(lineSeparator)

            subCommands.forEachIndexed { index, subCommand ->
                append(subCommand.buildBeautifulHelp())
                if (index != subCommands.lastIndex) {
                    appendText(lineSeparator)
                }
            }
        }

    final override suspend fun onExecute(sender: CommandSender, args: Arguments): Boolean = false
}
