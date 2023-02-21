import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    org.jetbrains.kotlin.jvm
    org.jetbrains.dokka
    `maven-publish`
}

repositories {
    mavenCentral()
}

tasks {
    withType<KotlinCompile> {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
            useK2.set(true)
        }
    }

    withType<JavaCompile>() {
        targetCompatibility = "17"
    }
}

val dokkaJar by tasks.registering(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles Kotlin docs with Dokka"
    archiveClassifier.set("javadoc")
    from(tasks.dokkaHtml)
}

publishing {
    publications.withType<MavenPublication>().configureEach {
        from(components["java"])
        artifact(tasks.kotlinSourcesJar)
        artifact(dokkaJar)
    }
}
