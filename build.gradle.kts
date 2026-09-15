plugins {
    id("java")
    // Shadow plugin for shading dependencies
    id("com.gradleup.shadow") version "8.3.5"
    // Paper run task plugin (provides runServer)
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

group = "sh.unbreaking"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    // Paper API dependency
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
}

java {
    // Java 21 is required for modern Paper/Minecraft servers
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
    runServer {
        // Sets the Paper version to download and test against
        minecraftVersion("1.21.4")
        jvmArgs("-Xms2G", "-Xmx2G")
    }

    processResources {
        val props = mapOf("version" to version, "description" to project.description)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    build {
        dependsOn(shadowJar)
    }
}