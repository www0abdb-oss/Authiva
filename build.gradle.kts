plugins {
    java
    id("io.papermc.paperweight.userdev") version "2.0.0-SNAPSHOT"
    id("xyz.jpenilla.run-paper") version "3.0.2"
    id("com.gradleup.shadow") version "9.3.1"
}

group = project.property("groupId") as String
version = project.property("projectVersion") as String

repositories {
    mavenCentral()

    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    paperweight.paperDevBundle(
        project.property("paperApiVersion") as String
    )

    implementation("org.xerial:sqlite-jdbc:3.50.3.0")
    implementation("org.bstats:bstats-bukkit:3.2.1")

    testImplementation(platform("org.junit:junit-bom:6.0.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-core:5.+")
    testImplementation("org.mockito:mockito-junit-jupiter:5.+")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

paperweight.reobfArtifactConfiguration.set(
    io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION
)

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

tasks {
    test {
        useJUnitPlatform()
    }

    jar {
        archiveBaseName.set("Authiva")
        archiveClassifier.set("plain")
    }

    // ------------------------------------------------------------
    // Standard
    // Desktop/server platforms:
    // Linux glibc, Windows and macOS
    // ------------------------------------------------------------
    shadowJar {
        configurations = listOf(project.configurations.runtimeClasspath.get())

        dependencies {
            exclude {
                it.moduleGroup == "org.junit"
            }
        }

        relocate(
            "org.bstats",
            "${project.group}.libs.bstats"
        )

        exclude("org/sqlite/native/FreeBSD/**")
        exclude("org/sqlite/native/Linux-Android/**")
        exclude("org/sqlite/native/Linux-Musl/**")
        exclude("org/sqlite/native/Linux/ppc64/**")
        exclude("org/sqlite/native/Linux/riscv64/**")

        archiveBaseName.set("Authiva")
        archiveClassifier.set("")
    }

    // ------------------------------------------------------------
    // Linux
    // Linux native libraries
    // ------------------------------------------------------------
    register<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJarLinux") {
        configurations = listOf(project.configurations.runtimeClasspath.get())

        dependencies {
            exclude {
                it.moduleGroup == "org.junit"
            }
        }

        relocate(
            "org.bstats",
            "${project.group}.libs.bstats"
        )

        // Remove non-Linux SQLite natives
        exclude("org/sqlite/native/FreeBSD/**")
        exclude("org/sqlite/native/Linux-Android/**")
        exclude("org/sqlite/native/Linux-Musl/**")
        exclude("org/sqlite/native/Mac/**")
        exclude("org/sqlite/native/Windows/**")

        archiveBaseName.set("Authiva")
        archiveClassifier.set("linux")
    }

    // ------------------------------------------------------------
    // Minimal
    // Linux x86_64 only
    // ------------------------------------------------------------
    register<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJarMinimal") {
        configurations = listOf(project.configurations.runtimeClasspath.get())

        dependencies {
            exclude {
                it.moduleGroup == "org.junit"
            }
        }

        relocate(
            "org.bstats",
            "${project.group}.libs.bstats"
        )

        // Keep only Linux x86_64 SQLite native library
        exclude("org/sqlite/native/FreeBSD/**")
        exclude("org/sqlite/native/Linux-Android/**")
        exclude("org/sqlite/native/Linux-Musl/**")
        exclude("org/sqlite/native/Linux/aarch64/**")
        exclude("org/sqlite/native/Linux/arm/**")
        exclude("org/sqlite/native/Linux/armv6/**")
        exclude("org/sqlite/native/Linux/armv7/**")
        exclude("org/sqlite/native/Linux/ppc64/**")
        exclude("org/sqlite/native/Linux/riscv64/**")
        exclude("org/sqlite/native/Linux/x86/**")
        exclude("org/sqlite/native/Mac/**")
        exclude("org/sqlite/native/Windows/**")

        archiveBaseName.set("Authiva")
        archiveClassifier.set("minimal")
    }

    // ------------------------------------------------------------
    // Full
    // ALL SQLite native libraries / ALL supported platforms
    // ------------------------------------------------------------
    register<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJarFull") {
        configurations = listOf(project.configurations.runtimeClasspath.get())

        dependencies {
            exclude {
                it.moduleGroup == "org.junit"
            }
        }

        relocate(
            "org.bstats",
            "${project.group}.libs.bstats"
        )

        // IMPORTANT:
        // No SQLite native exclusions here.
        // This keeps all SQLite natives:
        // Linux
        // Linux-Android
        // Linux-Musl
        // Windows
        // Mac
        // FreeBSD
        // PPC64
        // RISC-V
        //
        // Full build is expected to be larger than 10 MB.

        archiveBaseName.set("Authiva")
        archiveClassifier.set("full")
    }

    build {
        dependsOn(shadowJar)
        dependsOn("shadowJarLinux")
        dependsOn("shadowJarMinimal")
        dependsOn("shadowJarFull")
    }
}

tasks.processResources {
    val props = mapOf("version" to project.version)
    inputs.properties(props)

    filesMatching("plugin.yml") {
        expand(props)
    }
}
