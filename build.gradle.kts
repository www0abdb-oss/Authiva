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
    }

    shadowJar {
        configurations = project.configurations.runtimeClasspath.map {
            setOf(it)
        }

        dependencies {
            exclude {
                it.moduleGroup != "org.bstats"
            }
        }

        relocate(
            "org.bstats",
            "${project.group}.libs.bstats"
        )

        archiveBaseName.set("Authiva")
    }

    build {
        dependsOn(shadowJar)
    }
}
tasks.processResources {
    val props = mapOf("version" to project.version)
    inputs.properties(props)

    filesMatching("plugin.yml") {
        expand(props)
    }
}
