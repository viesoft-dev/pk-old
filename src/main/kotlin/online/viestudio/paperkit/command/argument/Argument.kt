package online.viestudio.paperkit.command.argument

typealias Validator = suspend (input: String) -> String?
typealias Completer = suspend (input: String) -> List<String>

interface Argument {

    val name: String
    val description: String
    val isRequired: Boolean
    val validator: Validator
    val completer: Completer

    interface Builder {

        var name: String
        var description: String
        var isRequired: Boolean
        var validator: Validator
        var completer: Completer

        fun build(): Argument
    }

    companion object {

        fun builder() = ArgumentImpl.Builder()
    }
}