package online.viestudio.paperkit.command.argument

data class ArgumentImpl(
    override val name: String,
    override val description: String,
    override val isRequired: Boolean,
    override val validator: Validator,
    override val completer: Completer,
) : Argument {

    class Builder : Argument.Builder {

        override var name: String = ""
        override var description: String = ""
        override var isRequired: Boolean = true
        override var validator: Validator = { null }
        override var completer: Completer = { emptyList() }

        override fun build() = ArgumentImpl(name, description, isRequired, validator, completer)
    }
}