plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
    `maven-publish`
}

dependencies {
    api(libs.coroutines.core)
    compileOnly(libs.paper.api)
    api(libs.kotlin.logging) {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
    api(kotlin("reflect"))
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "dev.viesoft.paperkit"
            artifactId = "core"
            version = project.version.toString()

            pom {
                name.set("PaperKit Core")
                description.set("Core of the PaperKit set of libraries.")
                url.set("https://github.com/viesoft-dev/paper-kit")

                organization {
                    name.set("Viesoft")
                    url.set("https://github.com/viesoft-dev")
                }

                developers {
                    developer { name.set("The Viesoft Team") }
                }

                issueManagement {
                    system.set("GitHub")
                    url.set("https://github.com/viesoft-dev/paper-kit/issues")
                }

                licenses {
                    license {
                        name.set("GPL-3.0")
                        url.set("https://opensource.org/license/gpl-3-0/")
                    }
                }
            }

            from(components["java"])
            artifact(tasks.kotlinSourcesJar)
        }
    }
}

with(tasks) {
    compileKotlin {
        kotlinOptions.apply {
            useK2 = true
            jvmTarget = "17"
            freeCompilerArgs += arrayOf(
                "-opt-in=kotlinx.coroutines.DelicateCoroutinesApi",
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-opt-in=kotlinx.coroutines.InternalCoroutinesApi",
            )
        }
    }

    shadowJar {
        dependencies {
            // Do not shade dependencies provided by paper
            exclude(dependency("org.jetbrains:annotations"))
        }
        relocate("mu", "dev.viesoft.paperkit.lib.klog")
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}
