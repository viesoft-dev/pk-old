package online.viestudio.paperkit.command.argument

import online.viestudio.paperkit.util.lineSeparator

data class ArgumentImpl(
    private val configProvider: () -> ArgumentConfig,
    override val isRequired: Boolean,
    override val validator: Validator,
    override val completer: Completer,
) : Argument {

    override val name: String get() = configProvider().name
    override val description: String get() = configProvider().description.joinToString(lineSeparator)

    class Builder : Argument.Builder {

        override lateinit var configProvider: () -> ArgumentConfig
        override var isRequired: Boolean = true
        override var validator: Validator = { _, _, _ -> null }
        override var completer: Completer = { _, _, _ -> emptyList() }

        override fun config(configProvider: () -> ArgumentConfig) {
            this.configProvider = configProvider
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

        override fun build() = ArgumentImpl(configProvider, isRequired, validator, completer)
    }
}
