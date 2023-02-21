rootProject.name = "paper-kit"

include("core")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            library("coroutines-core", "org.jetbrains.kotlinx", "kotlinx-coroutines-core").version("1.6.4")
            library("paper-api", "io.papermc.paper", "paper-api").version("1.19.3-R0.1-SNAPSHOT")
            library("kotlin-logging", "io.github.microutils", "kotlin-logging-jvm").version("3.0.5")
        }
    }
}
include("untitled")
