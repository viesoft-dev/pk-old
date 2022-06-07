package online.viestudio.paperkit.koin

import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.module

internal object Global {

    private val application: KoinApplication = startKoin {
        modules(
            module {
            }
        )
    }
    val koin: Koin get() = application.koin
}