package online.viestudio.paperkit.config.loader

import online.viestudio.paperkit.config.source.Source
import kotlin.reflect.KClass

internal class ConfigProviderImpl<T : Any>(
    private val configLoader: ConfigLoader,
    private val clazz: KClass<T>,
    private val source: Array<out Source>,
) : ConfigProvider<T> {

    override fun provide(): T = configLoader.loadOrThrow(clazz, *source)
}