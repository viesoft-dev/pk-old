package online.viestudio.paperkit.config.writer

import online.viestudio.paperkit.config.source.FileSource
import online.viestudio.paperkit.config.source.Source

internal abstract class BaseConfigWriter : ConfigWriter {

    final override fun write(target: FileSource, vararg sources: Source) = write(target, sources.toList())

    final override fun merge(target: FileSource, vararg sources: Source) = merge(target, sources.toList())

    final override fun writeIfNotExists(target: FileSource, vararg sources: Source) =
        writeIfNotExists(target, sources.toList())

    final override fun writeIfNotExists(target: FileSource, sources: List<Source>) {
        if (target.file.exists()) return
        write(target, sources)
    }

    final override fun writeOrMergeIfExists(target: FileSource, vararg sources: Source) =
        writeOrMergeIfExists(target, sources.toList())

    final override fun writeOrMergeIfExists(target: FileSource, sources: List<Source>) {
        if (target.file.exists()) {
            merge(target, sources)
        } else {
            write(target, sources)
        }
    }
}
