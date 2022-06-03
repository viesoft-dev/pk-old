package online.viestudio.paperkit.koin

import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

internal object Global {

    val application: KoinApplication = startKoin {}
    val koin: Koin get() = application.koin
}