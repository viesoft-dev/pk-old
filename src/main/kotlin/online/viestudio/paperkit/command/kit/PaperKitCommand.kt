package online.viestudio.paperkit.command.kit

import online.viestudio.paperkit.command.ParentCommand
import online.viestudio.paperkit.config.kit.CommandsConfig.Companion.commandsConfig

internal class PaperKitCommand : ParentCommand(
    subCommands = listOf(ReloadCommand())
) {

    override val config get() = commandsConfig.paperKit
}