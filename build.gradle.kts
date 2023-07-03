@file:Suppress("VulnerableLibrariesLocal")

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.8.21"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    application
}

group = "org.thepitcommunityserver"
version = "0.0.1-BETA"

repositories {
    mavenCentral()
    mavenLocal()

    maven { url = uri("https://dl.bintray.com/kotlin/kotlin-eap") }
    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots") }
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
    maven { url = uri("https://repo.dmulloy2.net/nexus/repository/public/") }
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
    maven {
        name = "citizens-repo"
        url = uri("https://maven.citizensnpcs.co/repo")
    }
    maven("https://jitpack.io/")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.google.inject:guice:4.1.0")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("org.spigotmc:spigot:1.8.8-R0.1-SNAPSHOT")
    implementation("software.amazon.awssdk:dynamodb:2.17.34")
    implementation("software.amazon.awssdk:apache-client:2.17.34")
    implementation(platform("software.amazon.awssdk:bom:2.20.96"))
    implementation("software.amazon.awssdk:dynamodb-enhanced")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.12.5")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.12.5")

    compileOnly("net.citizensnpcs:citizens-main:2.0.30-SNAPSHOT") {
        exclude(group = "*", module = "*")
    }

    testImplementation(kotlin("test"))
    testImplementation("io.kotest:kotest-runner-junit5:4.6.3")
    testImplementation("io.kotest:kotest-assertions-core:4.6.3")
    testImplementation("io.mockk:mockk:1.13.5")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

project.setProperty("mainClassName", "org.thepitcommunityserver.Main")

tasks.named<ShadowJar>("shadowJar") {
    archiveFileName.set(findProperty("archiveFileName") as String)
}

tasks.register<Copy>("localBuild") {
    dependsOn("shadowJar")

    val out = findProperty("archiveFileName") as String

    from(layout.buildDirectory.file("libs/$out"))
    into(".local-server/plugins")
}
