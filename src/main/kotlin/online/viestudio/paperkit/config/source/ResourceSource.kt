package online.viestudio.paperkit.config.source

import online.viestudio.paperkit.plugin.KitPlugin
import java.io.InputStream

/**
 * Represents resource file.
 *
 * @param plugin which contains the resource file.
 * @param path to the resource file.
 */
data class ResourceSource(
    private val plugin: KitPlugin,
    private val path: String,
) : Source {

    override fun inputStream(): InputStream? = plugin.getResource(path.removePrefix("/"))
}
