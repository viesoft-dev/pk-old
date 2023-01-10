package online.viestudio.paperkit.config.loader

import online.viestudio.paperkit.config.source.Source
import kotlin.reflect.KClass

internal abstract class BaseConfigLoader : ConfigLoader {

    private val _placeholderSources: MutableList<List<Source>> = mutableListOf()
    protected val placeholderSources = _placeholderSources

    final override fun <T : Any> loadOrThrow(kClass: KClass<T>, sources: List<Source>): T =
        load(kClass, sources).getOrThrow()

    final override fun <T : Any> loadOrThrow(kClass: KClass<T>, vararg sources: Source): T =
        loadOrThrow(kClass, sources.toList())

    final override fun <T : Any> load(kClass: KClass<T>, vararg sources: Source): Result<T> =
        load(kClass, sources.toList())

    final override fun <T : Any> provider(kClass: KClass<T>, vararg source: Source): ConfigProvider<T> =
        provider(kClass, source.toList())

    override fun <T : Any> provider(kClass: KClass<T>, source: List<Source>): ConfigProvider<T> =
        ConfigProviderImpl(this, kClass, source)

    final override fun addPlaceholderSources(vararg sources: Source) = addPlaceholderSources(sources.toList())

    final override fun addPlaceholderSources(sources: List<Source>) {
        _placeholderSources.add(sources)
    }
}
