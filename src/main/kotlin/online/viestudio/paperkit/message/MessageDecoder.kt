package online.viestudio.paperkit.message

import com.sksamuel.hoplite.*
import com.sksamuel.hoplite.decoder.Decoder
import com.sksamuel.hoplite.decoder.toValidated
import kotlin.reflect.KType
import kotlin.reflect.full.starProjectedType

class MessageDecoder : Decoder<Message> {

    private val supportedTypes = arrayOf(Message::class.starProjectedType)

    override fun decode(node: Node, type: KType, context: DecoderContext): ConfigResult<Message> = runCatching {
        when (node) {
            is StringNode -> Message(listOf(node.value))
            is ArrayNode -> Message(node.elements.filterIsInstance<StringNode>().map { it.value })
            else -> throw IllegalStateException()
        }
    }.toValidated {
        ConfigFailure.DecodeError(node, type)
    }

    override fun supports(type: KType): Boolean = type in supportedTypes
}