package online.viestudio.paperkit.command

abstract class ChildCommand(
    override val name: String,
    override val description: String,
    override val permission: String,
    minArguments: Int? = null,
    maxArguments: Int? = null,
) : BaseKitCommand(minArguments = minArguments, maxArguments = maxArguments)