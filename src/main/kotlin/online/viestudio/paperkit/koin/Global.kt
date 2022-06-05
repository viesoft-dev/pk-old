package online.viestudio.paperkit.koin

import online.viestudio.paperkit.config.loader.ConfigLoader
import online.viestudio.paperkit.config.loader.HopliteConfigLoader
import online.viestudio.paperkit.config.writer.ConfigWriter
import online.viestudio.paperkit.config.writer.SnakeYamlConfigWriter
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.bind
import org.koin.dsl.module

internal object Global {

    private val application: KoinApplication = startKoin {
        modules(
            module {
                single { HopliteConfigLoader("yaml") } bind ConfigLoader::class
                single { SnakeYamlConfigWriter() } bind ConfigWriter::class
            }
        )
    }
    val koin: Koin get() = application.koin
}