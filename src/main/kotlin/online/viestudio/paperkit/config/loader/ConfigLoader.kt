package online.viestudio.paperkit.config.loader

import online.viestudio.paperkit.config.source.Source
import kotlin.reflect.KClass

/**
 * Component to work with config input operations.
 */
interface ConfigLoader {

    /**
     * Loads the config from first valid source, or throws if all of them are invalid.
     *
     * @throws online.viestudio.paperkit.config.loader.exception.NoValidSourceException if no valid source has found
     * @param T generic type of the config.
     * @param kClass class of the config.
     * @param sources sources to load the config from.
     * @return loaded config
     */
    fun <T : Any> loadOrThrow(kClass: KClass<T>, vararg sources: Source): T

    fun <T : Any> loadOrThrow(kClass: KClass<T>, sources: List<Source>): T

    fun <T : Any> load(kClass: KClass<T>, vararg sources: Source): Result<T>

    fun <T : Any> load(kClass: KClass<T>, sources: List<Source>): Result<T>

    fun <T : Any> provider(kClass: KClass<T>, vararg source: Source): ConfigProvider<T>

    fun <T : Any> provider(kClass: KClass<T>, source: List<Source>): ConfigProvider<T>

    fun addPlaceholderSources(vararg sources: Source)

    fun addPlaceholderSources(sources: List<Source>)
}
