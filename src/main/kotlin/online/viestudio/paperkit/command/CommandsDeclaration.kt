package online.viestudio.paperkit.command

class CommandsDeclaration {

    private val _commands: MutableList<KitCommand> = mutableListOf()
    val commands: List<KitCommand> = _commands

    fun register(command: KitCommand) {
        _commands.add(command)
    }
}