package online.viestudio.paperkit.config.loader.exception

import online.viestudio.paperkit.config.source.Source
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmName

data class NoValidSourceException(
    val sources: List<Source>,
    val clazz: KClass<out Any>,
) : Exception(
    """
        No valid source has found while loading config ${clazz.jvmName}.
        Provided sources: ${sources.joinToString(", ")}.
    """.trimIndent()
)
