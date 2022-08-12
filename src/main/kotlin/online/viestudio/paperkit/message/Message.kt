package online.viestudio.paperkit.message

import kotlinx.serialization.Serializable

@Serializable(MessageSerializer::class)
data class Message(
    val content: List<String>,
)