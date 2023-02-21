plugins {
    kotlin("jvm") version "1.8.10" apply false
    kotlin("plugin.serialization") version "1.8.10" apply false
    id("org.jetbrains.dokka") version "1.7.20" apply false
    id("com.github.johnrengelman.shadow") version "7.1.2" apply false
}

group = "dev.viesoft"
version = "0.1.0-M1"

subprojects {
    group = "${rootProject.group}.paperkit"
    version = rootProject.version

    repositories {
        mavenCentral()
        maven("https://papermc.io/repo/repository/maven-public/")
    }
}
