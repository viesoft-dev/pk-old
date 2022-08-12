package online.viestudio.paperkit.config.loader

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addStreamSource
import online.viestudio.paperkit.config.loader.exception.NoValidSourceException
import online.viestudio.paperkit.config.loader.hoplite.SerialNameParamMapper
import online.viestudio.paperkit.config.source.Source
import online.viestudio.paperkit.message.MessageDecoder
import kotlin.reflect.KClass

internal class HopliteConfigLoader(
    private val extension: String,
) : BaseConfigLoader() {

    private val builder
        get() = ConfigLoaderBuilder.empty()
            .withClassLoader(this::class.java.classLoader)
            .addDecoder(MessageDecoder())
            .addParameterMapper(SerialNameParamMapper())
            .addProperties()
            .addDefaults()

    private fun ConfigLoaderBuilder.addProperties(): ConfigLoaderBuilder {
        placeholderSources.forEach { sources ->
            sources.forEach { source ->
                source.inputStream()?.also { addStreamSource(it, extension) }
            }
        }
        return this
    }

    override fun <T : Any> load(clazz: KClass<T>, sources: List<Source>): Result<T> = runCatching {
        val builder = builder
        var validSources = 0
        sources.forEach { source ->
            source.inputStream()?.also {
                validSources++
                builder.addStreamSource(it, extension)
            }
        }
        if (validSources == 0) throw NoValidSourceException(sources.toList(), clazz)
        builder.build().loadConfigOrThrow(clazz, emptyList())
    }
}
