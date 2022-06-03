package online.viestudio.paperkit.koin

import online.viestudio.paperkit.config.HopliteConfigLoaderBuilderFactory
import online.viestudio.paperkit.config.HopliteConfigLoaderBuilderFactoryImpl
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.bind
import org.koin.dsl.module

internal object Global {

    private val application: KoinApplication = startKoin {
        modules(
            module {
                single { HopliteConfigLoaderBuilderFactoryImpl() } bind HopliteConfigLoaderBuilderFactory::class
            }
        )
    }
    val koin: Koin get() = application.koin
}