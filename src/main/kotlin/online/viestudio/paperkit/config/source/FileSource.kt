package online.viestudio.paperkit.config.source

import java.io.File
import java.io.InputStream

/**
 * Represents file source.
 *
 * @param path to the file.
 */
data class FileSource(
    val path: String,
) : Source {

    val file by lazy { File(path) }

    override fun inputStream(): InputStream? = if (file.exists()) {
        file.inputStream()
    } else null
}