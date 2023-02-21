plugins {
    kotlin("jvm")
    `paper-kit-publishing`
    `paper-kit-module`
}

description = "The core library of the PaperKit set of libraries."

dependencies {
    api(libs.kotlin.coroutines)
    api(libs.kotlin.logging) {
        // Included in Paper.
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
    compileOnly(libs.paper.api)
    api(kotlin("reflect"))
}

with(tasks) {
    compileKotlin {
        kotlinOptions {
            freeCompilerArgs += arrayOf(
                "-opt-in=kotlinx.coroutines.DelicateCoroutinesApi",
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-opt-in=kotlinx.coroutines.InternalCoroutinesApi",
            )
        }
    }
}
