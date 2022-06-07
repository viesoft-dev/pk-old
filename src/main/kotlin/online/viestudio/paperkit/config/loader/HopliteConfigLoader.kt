package online.viestudio.paperkit.config.loader

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addStreamSource
import online.viestudio.paperkit.config.loader.exception.NoValidSourceException
import online.viestudio.paperkit.config.source.Source
import kotlin.reflect.KClass

internal class HopliteConfigLoader(
    private val extension: String,
) : BaseConfigLoader() {

    private val builder
        get() = ConfigLoaderBuilder.empty()
            .withClassLoader(this::class.java.classLoader)
            .addDefaults()

    override fun <T : Any> load(clazz: KClass<T>, vararg sources: Source): Result<T> = runCatching {
        val builder = builder
        var validSources = 0
        sources.apply { reverse() }.forEach { source ->
            source.inputStream()?.also {
                validSources++
                builder.addStreamSource(it, extension)
            }
        }
        if (validSources == 0) throw NoValidSourceException(sources.toList(), clazz)
        builder.build().loadConfigOrThrow(clazz, emptyList())
    }
}