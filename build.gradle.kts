///////////////////////////////////////////////////////////////////////////
// Versions
///////////////////////////////////////////////////////////////////////////

val paper = "1.19-R0.1-SNAPSHOT"
val coroutines = "1.6.4"
val hoplite = "2.3.1"
val kotlinxSerialization = "1.3.3"
val koin = "3.2.0"
val kaml = "0.46.0"
val miniMessage = "4.11.0"
val mysqlConnectorJava = "8.0.29"
val sqliteJdbc = "3.36.0.3"
val postgresql = "42.4.0"
val exposed = "0.38.2"

///////////////////////////////////////////////////////////////////////////
// Settings
///////////////////////////////////////////////////////////////////////////

plugins {
    kotlin("jvm") version "1.7.10"
    kotlin("plugin.serialization") version "1.7.10"
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("org.jetbrains.dokka") version "1.7.10"
}

group = "online.viestudio"
version = "2.1.0"

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
    // Paper
    api("io.papermc.paper", "paper-api", paper)
    api("net.kyori", "adventure-text-minimessage", miniMessage)
    // (De)Serialization
    api("com.charleskorn.kaml", "kaml", kaml)
    api("org.jetbrains.kotlinx", "kotlinx-serialization-json", kotlinxSerialization)
    api("org.jetbrains.kotlinx", "kotlinx-serialization-cbor", kotlinxSerialization)
    // Dependency injection
    api("io.insert-koin", "koin-core", koin)
    // Database
    api("mysql", "mysql-connector-java", mysqlConnectorJava)
    api("org.xerial", "sqlite-jdbc", sqliteJdbc)
    api("org.postgresql", "postgresql", postgresql)
    api("org.jetbrains.exposed", "exposed-core", exposed)
    api("org.jetbrains.exposed", "exposed-dao", exposed)
    api("org.jetbrains.exposed", "exposed-jdbc", exposed)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "online.viestudio"
            artifactId = "paper-kit"
            version = version

            from(components["java"])
            artifact(tasks.kotlinSourcesJar)
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
        archiveFileName.set("${project.name}-${project.version}-framework.jar")

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