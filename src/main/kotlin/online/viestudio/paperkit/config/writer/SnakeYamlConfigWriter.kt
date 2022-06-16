package online.viestudio.paperkit.config.writer

import online.viestudio.paperkit.config.source.FileSource
import online.viestudio.paperkit.config.source.Source
import online.viestudio.paperkit.config.writer.exception.NoValidSourceException
import org.bukkit.configuration.file.YamlConfiguration
import java.io.InputStream

internal class SnakeYamlConfigWriter : BaseConfigWriter() {

    override fun merge(target: FileSource, sources: List<Source>) {
        val inputStream =
            sources.firstValidOrNull() ?: throw NoValidSourceException(sources.toList(), target, "merging")
        inputStream.use {
            val default = YamlConfiguration.loadConfiguration(inputStream.reader())
            val actual = YamlConfiguration.loadConfiguration(target.file)
            (default mergeTo actual).save(target.file)
        }
    }

    private infix fun YamlConfiguration.mergeTo(to: YamlConfiguration): YamlConfiguration {
        getKeys(true).forEach { path ->
            if (!to.contains(path)) {
                to.set(path, get(path))
            }
            val defaultComments = getComments(path)
            val actualComments = to.getComments(path)
            if (actualComments != defaultComments) {
                to.setComments(path, defaultComments)
            }
        }
        return to
    }

    override fun write(target: FileSource, sources: List<Source>) {
        val inputStream =
            sources.firstValidOrNull() ?: throw NoValidSourceException(sources.toList(), target, "writing")
        inputStream.use {
            target.file.apply {
                parentFile.apply { mkdirs() }
                writeBytes(it.readAllBytes())
            }
        }
    }

    private fun List<Source>.firstValidOrNull(): InputStream? {
        for (source in this) {
            source.inputStream()?.also { return it }
        }
        return null
    }
}
