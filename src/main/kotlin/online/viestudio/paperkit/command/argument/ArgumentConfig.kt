package online.viestudio.paperkit.command.argument

import kotlinx.serialization.Serializable

@Serializable
data class ArgumentConfig(
    val name: String,
    val description: List<String>,
)