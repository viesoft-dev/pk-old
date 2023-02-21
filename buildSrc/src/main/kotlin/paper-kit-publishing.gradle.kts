import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.repositories

plugins {
    `maven-publish`
}

publishing {
    if (!isJitPack) {
        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/viesoft-dev/paper-kit")
                credentials {
                    username = System.getenv("GPR_USERNAME")
                    password = System.getenv("GPR_TOKEN")
                }
            }
        }
    }
    publications {
        create<MavenPublication>("gpr") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()

            pom {
                name.set("PaperKit ${project.name.replaceFirstChar { it.uppercase() }}")
                description.set(project.description)
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
        }
    }
}
