package online.viestudio.paperkit.config.source

import java.io.InputStream

sealed interface Source {

    /**
     * @return [InputStream] if source is valid, else null.
     */
    fun inputStream(): InputStream?
}