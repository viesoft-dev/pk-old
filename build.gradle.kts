///////////////////////////////////////////////////////////////////////////
// Versions
///////////////////////////////////////////////////////////////////////////

val paperVersion = "1.19-R0.1-SNAPSHOT"
val coroutinesVersion = "1.6.4"
val hopliteVersion: String by project
val kotlinxSerializationVersion = "1.3.3"
val koinVersion = "3.2.0"
val kamlVersion = "0.46.0"
val miniMessageVersion = "4.11.0"
val mysqlConnectorJavaVersion = "8.0.29"
val sqliteJdbcVersion = "3.36.0.3"
val postgresqlVersion = "42.4.0"
val exposedVersion = "0.38.2"

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
    api("org.jetbrains.kotlinx", "kotlinx-coroutines-core", coroutinesVersion)
    // Configuration
    api("com.sksamuel.hoplite", "hoplite-yaml", hopliteVersion)
    // Paper
    api("io.papermc.paper", "paper-api", paperVersion)
    api("net.kyori", "adventure-text-minimessage", miniMessageVersion)
    // (De)Serialization
    api("com.charleskorn.kaml", "kaml", kamlVersion)
    api("org.jetbrains.kotlinx", "kotlinx-serialization-json", kotlinxSerializationVersion)
    api("org.jetbrains.kotlinx", "kotlinx-serialization-cbor", kotlinxSerializationVersion)
    // Dependency injection
    api("io.insert-koin", "koin-core", koinVersion)
    // Database
    api("mysql", "mysql-connector-java", mysqlConnectorJavaVersion)
    api("org.xerial", "sqlite-jdbc", sqliteJdbcVersion)
    api("org.postgresql", "postgresql", postgresqlVersion)
    api("org.jetbrains.exposed", "exposed-core", exposedVersion)
    api("org.jetbrains.exposed", "exposed-dao", exposedVersion)
    api("org.jetbrains.exposed", "exposed-jdbc", exposedVersion)
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