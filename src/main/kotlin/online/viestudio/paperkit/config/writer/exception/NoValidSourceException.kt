package online.viestudio.paperkit.config.writer.exception

import online.viestudio.paperkit.config.source.FileSource
import online.viestudio.paperkit.config.source.Source

data class NoValidSourceException(
    val sources: List<Source>,
    val fileSource: FileSource,
    val action: String,
) : Exception(
    """
        No valid source has found while $action into config file ${fileSource.file.path}.
        Provided sources: ${sources.joinToString(", ")}.
    """.trimIndent()
)
