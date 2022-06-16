package online.viestudio.paperkit.command

typealias CommandInitializer = () -> KitCommand

class CommandsDeclaration {

    private val initializers: MutableList<CommandInitializer> = mutableListOf()
    val commands: List<KitCommand> by lazy { initializers.map { it() } }

    fun register(initializer: CommandInitializer) {
        initializers.add(initializer)
    }
}
