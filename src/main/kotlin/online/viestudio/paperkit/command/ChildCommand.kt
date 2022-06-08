package online.viestudio.paperkit.command

abstract class ChildCommand(
    name: String,
    description: String = "",
    permission: String = "$name.execute",
) : BaseKitCommand(
    name = name,
    description = description,
    permission = permission
)