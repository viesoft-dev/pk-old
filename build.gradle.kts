//region Versions
val paperVersion: String by project
val coroutinesVersion: String by project
val hopliteVersion: String by project
val kotlinxSerializationVersion: String by project
val koinVersion: String by project
val kamlVersion: String by project
val miniMessageVersion: String by project
val mysqlConnectorJavaVersion: String by project
val sqliteJdbcVersion: String by project
val postgresqlVersion: String by project
val exposedVersion: String by project
val ktorVersion: String by project
val hikariCpVersion: String by project
val paperKitGradleVersion: String by project
//endregion

plugins {
    kotlin("jvm") version "1.8.0"
    kotlin("plugin.serialization") version "1.8.10"
    id("com.google.devtools.ksp") version "1.8.0-1.0.8"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("org.jetbrains.dokka") version "1.7.20"
    `maven-publish`
}

group = "online.viestudio"
version = "5.0.0"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    // Kotlin
    api(kotlin("reflect"))
    api("org.jetbrains.kotlinx", "kotlinx-coroutines-core", coroutinesVersion)
    // Configuration
    implementation("com.sksamuel.hoplite", "hoplite-yaml", hopliteVersion)
    // Paper
    api("io.papermc.paper", "paper-api", paperVersion)
    // (De)Serialization
    implementation("com.charleskorn.kaml", "kaml", kamlVersion)
    api("org.jetbrains.kotlinx", "kotlinx-serialization-json", kotlinxSerializationVersion)
    api("org.jetbrains.kotlinx", "kotlinx-serialization-cbor", kotlinxSerializationVersion)
    // Dependency injection
    api("io.insert-koin", "koin-core", koinVersion)
    // Database
    api("com.zaxxer", "HikariCP", hikariCpVersion)
    api("mysql", "mysql-connector-java", mysqlConnectorJavaVersion)
    api("org.xerial", "sqlite-jdbc", sqliteJdbcVersion)
    api("org.postgresql", "postgresql", postgresqlVersion)
    api("org.jetbrains.exposed", "exposed-core", exposedVersion)
    api("org.jetbrains.exposed", "exposed-dao", exposedVersion)
    api("org.jetbrains.exposed", "exposed-jdbc", exposedVersion)
    // Gradle
    compileOnly("com.github.paper-kit", "gradle-paper-kit", paperKitGradleVersion)
    ksp("com.github.paper-kit", "gradle-paper-kit", paperKitGradleVersion)
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

with(tasks) {
    processResources {
        val properties = linkedMapOf(
            "version" to project.version.toString(),
        )

        filesMatching(
            setOf("plugin.yml")
        ) {
            expand(project.properties + properties)
        }
    }

    shadowJar {
        archiveFileName.set("${project.name}-${project.version}-framework.jar")

        dependencies {
            // Declared as libraries in plugin.yml so will be downloaded on first start.
            exclude(dependency("org.jetbrains.kotlin:kotlin-stdlib"))
            exclude(dependency("org.jetbrains.kotlin:kotlin-reflect"))
            exclude(dependency("org.jetbrains.kotlinx:kotlinx-coroutines-core"))
            exclude(dependency("org.jetbrains.kotlinx:kotlinx-serialization-cbor"))
            exclude(dependency("org.jetbrains.kotlinx:kotlinx-serialization-json"))
            exclude(dependency("io.insert-koin:koin-core"))
            // Part of the server.
            exclude(dependency("org.jetbrains:annotations"))
            exclude(dependency("io.papermc:paper-api"))
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
                "-opt-in=kotlinx.coroutines.DelicateCoroutinesApi",
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-opt-in=kotlinx.coroutines.InternalCoroutinesApi",
            )
        }
    }
}
