package online.viestudio.paperkit.config.source

import java.io.InputStream
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

/**
 * Represents instance of config created with defaults.
 *
 * @param clazz — of the config.
 */
data class DefaultsSource(
    val clazz: KClass<out Any>,
) : SerializableSource(clazz) {

    override fun inputStream(): InputStream? = createDefaultAndSerialize()?.byteInputStream()

    private fun createDefaultAndSerialize() = clazz.createDefaultOrNull()?.encodeToStringOrNull()

    private fun KClass<out Any>.createDefaultOrNull(): Any? =
        runCatching { primaryConstructor?.callBy(emptyMap()) }.onFailure { it.printStackTrace() }.getOrNull()
}
