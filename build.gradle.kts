plugins {
    kotlin("jvm") version "1.8.21"
    id("com.github.johnrengelman.shadow") version "6.1.0"
    application
}

group = "org.thepitcommunityserver"
version = "1.0-SNAPSHOT"

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

application {
    mainClass.set("MainKt")
}