package online.viestudio.paperkit.config

import online.viestudio.paperkit.config.loader.ConfigLoader
import online.viestudio.paperkit.config.source.DefaultsSource
import online.viestudio.paperkit.config.source.FileSource
import online.viestudio.paperkit.config.source.ResourceSource
import online.viestudio.paperkit.config.source.Source
import online.viestudio.paperkit.plugin.KitPlugin
import org.koin.core.module.Module
import org.koin.dsl.module
import kotlin.reflect.KClass

class ConfigurationDeclaration(
    private val plugin: KitPlugin,
    val loader: ConfigLoader,
) {

    val module: Module = module {}
    val map: MutableMap<KClass<*>, List<Source>> = HashMap()

    /**
     * Register config [T] for loading from source [source] if valid.
     *
     * @param T
     * @param source
     */
    inline infix fun <reified T : Any> KClass<T>.loadFrom(source: Source) = loadFrom(listOf(source))

    /**
     * Register config [T] for loading from first valid source in [sources].
     * If file source is presented, and the file doesn't exist, then write content of the next valid source into it.
     *
     * @param T
     * @param sources
     */
    inline infix fun <reified T : Any> KClass<T>.loadFrom(sources: List<Source>) {
        val provider = loader.provider(this, *sources.toTypedArray())
        module.single(createdAtStart = true) { provider.provide() }
        map[this] = sources
    }

    /**
     * Creates [FileSource] to the path in plugin directory.
     */
    fun file(path: String) = FileSource(plugin.dataFolder.resolve(path).path)

    /**
     * Creates [ResourceSource] to the path.
     */
    fun resource(path: String) = ResourceSource(plugin, path)

    /**
     * Creates [DefaultsSource] of class.
     */
    fun <T : Any> defaults(kClass: KClass<T>) = DefaultsSource(kClass)

    infix fun Source.or(source: Source): List<Source> = listOf(this, source)

    infix fun List<Source>.or(source: Source): List<Source> = toMutableList().apply { add(source) }
}