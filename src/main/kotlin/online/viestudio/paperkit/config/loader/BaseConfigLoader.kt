package online.viestudio.paperkit.config.loader

import online.viestudio.paperkit.config.source.Source
import kotlin.reflect.KClass

abstract class BaseConfigLoader : ConfigLoader {

    private val _placeholderSources: MutableList<List<Source>> = mutableListOf()
    protected val placeholderSources = _placeholderSources

    final override fun <T : Any> loadOrThrow(clazz: KClass<T>, sources: List<Source>): T =
        load(clazz, sources).getOrThrow()

    final override fun <T : Any> loadOrThrow(clazz: KClass<T>, vararg sources: Source): T =
        loadOrThrow(clazz, sources.toList())

    final override fun <T : Any> load(clazz: KClass<T>, vararg sources: Source): Result<T> =
        load(clazz, sources.toList())

    final override fun <T : Any> provider(clazz: KClass<T>, vararg source: Source): ConfigProvider<T> =
        provider(clazz, source.toList())

    override fun <T : Any> provider(clazz: KClass<T>, source: List<Source>): ConfigProvider<T> =
        ConfigProviderImpl(this, clazz, source)

    final override fun addPlaceholderSources(vararg sources: Source) = addPlaceholderSources(sources.toList())

    final override fun addPlaceholderSources(sources: List<Source>) {
        _placeholderSources.add(sources)
    }
}