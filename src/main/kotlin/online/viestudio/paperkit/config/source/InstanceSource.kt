package online.viestudio.paperkit.config.source

import java.io.InputStream

/**
 * Represents instance of config.
 *
 * @param instance — of the config.
 */
data class InstanceSource<T : Any>(
    private val instance: T,
) : SerializableSource(instance::class) {

    override fun inputStream(): InputStream? = instance.encodeToStringOrNull()?.byteInputStream()
}
