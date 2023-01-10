package online.viestudio.paperkit.command.argument.dsl

import online.viestudio.paperkit.command.argument.Argument

@ArgumentDSL
class ArgumentsDeclaration {

    private val _arguments: MutableList<Argument> = mutableListOf()
    val arguments: List<Argument> = _arguments

    @ArgumentDSL
    inline fun argument(block: Argument.Builder.() -> Unit) {
        addArgument(Argument.builder().apply(block).build())
    }

    fun addArgument(argument: Argument) {
        _arguments.add(argument)
    }
}
