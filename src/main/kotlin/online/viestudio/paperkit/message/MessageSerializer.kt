package online.viestudio.paperkit.message

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class MessageSerializer : KSerializer<Message> {

    private val stringListDeserializer = ListSerializer(String.serializer())

    @Suppress("OPT_IN_USAGE")
    override val descriptor: SerialDescriptor = SerialDescriptor("Message", stringListDeserializer.descriptor)

    override fun deserialize(decoder: Decoder): Message =
        Message(decoder.decodeSerializableValue(stringListDeserializer))

    override fun serialize(encoder: Encoder, value: Message) {
        encoder.encodeSerializableValue(stringListDeserializer, value.content)
    }
}