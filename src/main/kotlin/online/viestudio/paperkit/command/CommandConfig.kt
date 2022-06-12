package online.viestudio.paperkit.command

import kotlinx.serialization.Serializable
import online.viestudio.paperkit.command.argument.ArgumentConfig

@Serializable
data class CommandConfig(
    val name: String,
    val aliases: List<String>? = null,
    val description: String,
    val permission: String,
    val arguments: Map<String, ArgumentConfig>? = null,
) {

    fun argument(name: String): ArgumentConfig {
        return arguments?.get(name)
            ?: throw IllegalStateException("Argument $name not found in config of command ${this.name}")
    }
}
