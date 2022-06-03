///////////////////////////////////////////////////////////////////////////
// Versions
///////////////////////////////////////////////////////////////////////////

val paper = "1.18.2-R0.1-SNAPSHOT"
val coroutines = "1.6.2"
val hoplite = "2.1.5"
val kotlinxSerialization = "1.3.3"
val koin = "3.1.6"
val ronfy = "1.1"
val ktorm = "3.4.1"

///////////////////////////////////////////////////////////////////////////
// Settings
///////////////////////////////////////////////////////////////////////////

plugins {
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.serialization") version "1.6.21"
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("org.jetbrains.dokka") version "1.6.21"
}

group = "online.viestudio"
version = "1.0-dev"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    // Kotlin
    api("org.jetbrains.kotlinx", "kotlinx-coroutines-core", coroutines)
    // Configuration
    api("com.sksamuel.hoplite", "hoplite-yaml", hoplite)
    api("com.github.vie10", "ronfy", ronfy)
    // Paper
    api("io.papermc.paper", "paper-api", paper)
    // Json
    api("org.jetbrains.kotlinx", "kotlinx-serialization-json", kotlinxSerialization)
    // Dependency injection
    api("io.insert-koin", "koin-core", koin)
    // Database
    api("org.ktorm", "ktorm-core", ktorm)
}

publishing {
    repositories {
        maven("https://maven.pkg.github.com/paper-kit/paper-kit") {
            name = "GitHubPackages"
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

///////////////////////////////////////////////////////////////////////////
// Tasks
///////////////////////////////////////////////////////////////////////////

with(tasks) {
    processResources {
        val properties = linkedMapOf(
            "version" to project.version.toString(),
        )

        filesMatching(
            setOf("plugin.yml")
        ) {
            expand(properties)
        }
    }

    shadowJar {
        archiveFileName.set("${project.name}-${project.version}.jar")

        fun ResolvedDependency.containsOrParents(content: String): Boolean {
            return name.contains(content) || parents.find { it.containsOrParents(content) } != null
        }

        dependencies {
            exclude {
                it.containsOrParents("paper-api")
            }
        }
    }

    wrapper {
        gradleVersion = "7.4.2"
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.apply {
            jvmTarget = "17"
            freeCompilerArgs = freeCompilerArgs.plus("-opt-in=kotlin.RequiresOptIn")
        }
    }
}