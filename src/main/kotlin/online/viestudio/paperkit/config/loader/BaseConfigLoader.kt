package online.viestudio.paperkit.config.loader

import online.viestudio.paperkit.config.source.Source
import kotlin.reflect.KClass

abstract class BaseConfigLoader : ConfigLoader {

    final override fun <T : Any> loadOrThrow(clazz: KClass<T>, vararg sources: Source): T {
        return load(clazz, *sources).getOrThrow()
    }

    final override fun <T : Any> provider(clazz: KClass<T>, vararg source: Source): ConfigProvider<T> {
        return ConfigProviderImpl(this, clazz, source)
    }
}