package online.viestudio.paperkit.config.writer

import online.viestudio.paperkit.config.source.FileSource
import online.viestudio.paperkit.config.source.Source

/**
 * Component to work with config write operations.
 */
interface ConfigWriter {

    /**
     * Takes first valid source from [sources] then merge it into [target].
     * If target doesn't contain some fields which source do, then their will be created.
     */
    fun merge(target: FileSource, vararg sources: Source)

    /**
     * Takes first valid source from [sources] then merge it into [target].
     * If target doesn't contain some fields which source do, then their will be created.
     */
    fun merge(target: FileSource, sources: List<Source>)

    /**
     * Takes first valid source from [sources] then if [target] exists, merge the source to it, else write.
     */
    fun writeOrMergeIfExists(target: FileSource, vararg sources: Source)

    /**
     * Takes first valid source from [sources] then if [target] exists, merge the source to it, else write.
     */
    fun writeOrMergeIfExists(target: FileSource, sources: List<Source>)

    /**
     * Takes first valid source from [sources] then write into [target] if it doesn't exist.
     */
    fun writeIfNotExists(target: FileSource, vararg sources: Source)

    /**
     * Takes first valid source from [sources] then write into [target] if it doesn't exist.
     */
    fun writeIfNotExists(target: FileSource, sources: List<Source>)

    /**
     * Takes first valid source from [sources] then write into [target].
     */
    fun write(target: FileSource, vararg sources: Source)

    /**
     * Takes first valid source from [sources] then write into [target].
     */
    fun write(target: FileSource, sources: List<Source>)

}