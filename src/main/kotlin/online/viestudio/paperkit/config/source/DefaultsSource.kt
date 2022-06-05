package online.viestudio.paperkit.config.source

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.KSerializer
import online.viestudio.paperkit.config.Comment
import org.bukkit.configuration.file.YamlConfiguration
import java.io.InputStream
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.*

/**
 * Represents instance of config created with defaults.
 *
 * @param clazz — of the config.
 */
data class DefaultsSource(
    val clazz: KClass<out Any>,
) : Source {

    private val content: String? by lazy { clazz.createDefaultOrNull()?.encodeToStringOrNull() }

    override fun inputStream(): InputStream? = content?.byteInputStream()

    private fun Any.mergeCommentsInto(configStr: String): String {
        val config = YamlConfiguration().apply { loadFromString(configStr) }
        this::class.memberProperties.forEach {
            it.mergeCommentsInto(path = it.name, config = config, holder = this)
        }
        return config.saveToString()
    }

    private fun KProperty1<out Any, *>.mergeCommentsInto(path: String, holder: Any, config: YamlConfiguration) {
        annotations.filterIsInstance(Comment::class.java).lastOrNull()?.also {
            config.setComments(path, it.content.trimIndent().split(System.lineSeparator()))
        }
        val children = call(holder) ?: return
        val clazz = children::class
        if (!clazz.isData) return
        children::class.memberProperties.forEach {
            it.mergeCommentsInto(path = "${path}.${it.name}", config = config, holder = children)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun Any.encodeToStringOrNull(): String? {
        val serializer = this::class.findSerializerOrNull() ?: return null
        return mergeCommentsInto(
            Yaml.default.encodeToString(serializer as KSerializer<Any>, this)
        )
    }

    private fun KClass<out Any>.findSerializerOrNull(): KSerializer<*>? {
        val serializer = companionObject?.memberFunctions?.find {
            it.returnType.isSubtypeOf(KSerializer::class.starProjectedType)
        } ?: return null
        return serializer.call(companionObjectInstance) as KSerializer<*>
    }

    private fun KClass<out Any>.createDefaultOrNull(): Any? =
        runCatching { primaryConstructor?.callBy(emptyMap()) }.getOrNull()
}