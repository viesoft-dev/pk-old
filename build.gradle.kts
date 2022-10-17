///////////////////////////////////////////////////////////////////////////
// Versions
///////////////////////////////////////////////////////////////////////////

val paper = "1.19-R0.1-SNAPSHOT"
val coroutines = "1.6.4"
val hoplite = "2.5.2"
val kotlinxSerialization = "1.4.0-RC"
val koin = "3.2.0"
val kaml = "0.47.0"
val miniMessage = "4.11.0"
val mysqlConnectorJava = "8.0.30"
val sqliteJdbc = "3.39.2.0"
val postgresql = "42.4.2"
val exposed = "0.39.2"
val ktor = "2.1.0"
val hikariCp = "5.0.1"

///////////////////////////////////////////////////////////////////////////
// Settings
///////////////////////////////////////////////////////////////////////////

plugins {
    kotlin("jvm") version "1.7.10"
    kotlin("plugin.serialization") version "1.7.10"
    id("com.google.devtools.ksp") version "1.7.10-1.0.6"
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("org.jetbrains.dokka") version "1.7.10"
    id("com.github.ben-manes.versions") version "0.43.0"
}

group = "online.viestudio"
version = "4.0.0"

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
    // Http Client
    api("io.ktor", "ktor-client-core", ktor)
    api("io.ktor", "ktor-client-okhttp", ktor)
    // Database
    api("com.zaxxer", "HikariCP", hikariCp)
    api("mysql", "mysql-connector-java", mysqlConnectorJava)
    api("org.xerial", "sqlite-jdbc", sqliteJdbc)
    api("org.postgresql", "postgresql", postgresql)
    api("org.jetbrains.exposed", "exposed-core", exposed)
    api("org.jetbrains.exposed", "exposed-dao", exposed)
    api("org.jetbrains.exposed", "exposed-jdbc", exposed)
    // Gradle
    compileOnly("com.github.paper-kit", "gradle-paper-kit", project.version.toString())
    ksp("com.github.paper-kit", "gradle-paper-kit", project.version.toString())
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


sourceSets {
    main {
        java {
            srcDir(file("build/generated/ksp/main/kotlin"))
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
            freeCompilerArgs = freeCompilerArgs + arrayOf(
                "-opt-in=kotlin.RequiresOptIn",
                "-opt-in=kotlinx.coroutines.DelicateCoroutinesApi"
            )
        }
    }
}