package online.viestudio.paperkit.command.kit

import online.viestudio.paperkit.command.ParentCommand

class PaperKitCommand : ParentCommand(
    name = "kit",
    description = "Main command of Paper-Kit framework.",
    aliases = listOf("kt"),
    permission = "paper-kit.execute",
    subCommands = listOf(ReloadCommand())
)