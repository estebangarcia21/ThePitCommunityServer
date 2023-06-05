@file:Suppress("VulnerableLibrariesLocal")

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile

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

    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots") }
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
    maven { url = uri("https://repo.dmulloy2.net/nexus/repository/public/") }
    maven { url = uri("https://dl.bintray.com/kotlin/kotlin-eap") }
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.google.inject:guice:4.1.0")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("org.spigotmc:spigot:1.8.8-R0.1-SNAPSHOT")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}

project.setProperty("mainClassName", "org.thepitcommunityserver.Main")

tasks.register<ShadowJar>("localBuild") {
    archiveFileName.set("ThePitCommunityServer.jar")
    destinationDirectory.set(file(".local-server/plugins"))

    from (project.sourceSets["main"].output)
}
