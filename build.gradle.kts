plugins {
    java
    id("io.papermc.paperweight.userdev") version "2.0.0-SNAPSHOT"
    id("xyz.jpenilla.run-paper") version "3.0.2"
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
    paperweight.paperDevBundle(project.property("paperApiVersion") as String)

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
}
