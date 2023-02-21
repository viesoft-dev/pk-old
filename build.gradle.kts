plugins {
    // Could not find the module group and name for shadow.
    id("com.github.johnrengelman.shadow") version "7.1.2" apply false
}

group = "dev.viesoft"
version = "0.1.0-M1"

subprojects {
    group = "${rootProject.group}.paperkit"
    version = rootProject.version

    repositories {
        mavenCentral()
        // Paper
        maven("https://papermc.io/repo/repository/maven-public/")
    }
}
