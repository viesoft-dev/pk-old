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
        override var validator: Validator = { _, _ -> null }
        override var completer: Completer = { _, _ -> emptyList() }

        override fun name(name: String): Builder = apply {
            this.name = name
        }

        override fun description(description: String): Builder = apply {
            this.description = description
        }

        override fun required() = apply {
            isRequired(true)
        }

        override fun isRequired(isRequired: Boolean): Builder = apply {
            this.isRequired = isRequired
        }

        override fun validator(validator: Validator): Builder = apply {
            this.validator = validator
        }

        override fun completer(completer: Completer): Builder = apply {
            this.completer = completer
        }

        override fun build() = ArgumentImpl(name, description, isRequired, validator, completer)
    }
}