@file:Suppress("VulnerableLibrariesLocal")

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.22"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    application
}

group = "org.thepitcommunityserver"
version = "0.0.1-BETA"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://repo.dmulloy2.net/nexus/repository/public/")
    maven("https://repo.papermc.io/repository/maven-public/")
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
    compileOnly("org.spigotmc:spigot:1.8.8-R0.1-SNAPSHOT") // compileOnly - provided by server

    // AWS SDK - only what you need
    implementation(platform("software.amazon.awssdk:bom:2.20.96"))
    implementation("software.amazon.awssdk:dynamodb")
    implementation("software.amazon.awssdk:dynamodb-enhanced")
    implementation("software.amazon.awssdk:apache-client")

    implementation("com.fasterxml.jackson.core:jackson-databind:2.12.5")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.12.5")

    compileOnly("net.citizensnpcs:citizens-main:2.0.30-SNAPSHOT") {
        exclude(group = "*", module = "*")
    }

    testImplementation(kotlin("test"))
    testImplementation("io.kotest:kotest-runner-junit5:4.6.3")
    testImplementation("io.kotest:kotest-assertions-core:4.6.3")
    testImplementation("io.mockk:mockk:1.13.5")
    testImplementation("org.spigotmc:spigot:1.8.8-R0.1-SNAPSHOT")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

// Kotlin compiler options (correct way for 1.9.x)
tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.addAll("-Xbackend-threads=4", "-Xjvm-default=all")
    }
}

application {
    mainClass.set("org.thepitcommunityserver.Main")
}

tasks.named<ShadowJar>("shadowJar") {
    archiveFileName.set(findProperty("archiveFileName") as String)

    // Exclude bloat
    exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
    exclude("META-INF/NOTICE*", "META-INF/LICENSE*", "META-INF/DEPENDENCIES")
    exclude("META-INF/maven/**")
    exclude("META-INF/versions/**")
    exclude("**/module-info.class")
    exclude("mozilla/**")

    // Minimize - only include used classes
    minimize {
        exclude(dependency("org.jetbrains.kotlin:.*"))
        exclude(dependency("com.google.inject:.*"))
        exclude(dependency("software.amazon.awssdk:.*"))
        exclude(dependency("com.fasterxml.jackson..*:.*"))
    }

    // Merge service files (needed for AWS SDK)
    mergeServiceFiles()
}

tasks.register<Copy>("localBuild") {
    dependsOn("shadowJar")
    val out = findProperty("archiveFileName") as String
    from(layout.buildDirectory.file("libs/$out"))
    into(".local-server/plugins")
}

// Development tasks
tasks.register("checkDocker") {
    group = "development"
    doLast {
        val result = ProcessBuilder("cmd", "/c", "docker", "info")
            .redirectErrorStream(true)
            .start()
        if (result.waitFor() != 0) {
            throw GradleException("Docker Desktop is not running. Please start Docker Desktop first.")
        }
        println("✓ Docker Desktop is running")
    }
}

tasks.register("runDatabase") {
    group = "development"
    dependsOn("checkDocker")

    doLast {
        val checkProcess = ProcessBuilder("cmd", "/c", "docker-compose", "ps", "-q")
            .directory(file("dynamodb"))
            .redirectErrorStream(false)
            .start()
        val output = checkProcess.inputStream.bufferedReader().readText().trim()
        checkProcess.waitFor()

        if (output.isNotEmpty()) {
            println("✓ Database container already running")
        } else {
            println("Starting database container...")
            val startProcess = ProcessBuilder("cmd", "/c", "docker-compose", "up", "-d")
                .directory(file("dynamodb"))
                .redirectErrorStream(true)
                .start()
            startProcess.inputStream.bufferedReader().forEachLine { println(it) }
            if (startProcess.waitFor() != 0) {
                throw GradleException("Failed to start database container")
            }
            println("✓ Database container started")
        }
    }
}

tasks.register("runClient") {
    group = "development"
    doLast {
        val prismPath = "C:\\Users\\Admin\\AppData\\Local\\Programs\\PrismLauncher\\prismlauncher.exe"
        ProcessBuilder(prismPath, "--launch", "1.8.8-dev")
            .redirectOutput(ProcessBuilder.Redirect.DISCARD)
            .redirectError(ProcessBuilder.Redirect.DISCARD)
            .start()
        println("✓ Client launched")
    }
}

fun killExistingServer() {
    println("Checking for existing server on port 25565...")
    ProcessBuilder(
        "powershell", "-Command",
        "Get-NetTCPConnection -LocalPort 25565 -ErrorAction SilentlyContinue | ForEach-Object { Stop-Process -Id \$_.OwningProcess -Force -ErrorAction SilentlyContinue }"
    ).redirectErrorStream(true).start().waitFor()
    println("✓ Previous server stopped (if any)")
    Thread.sleep(1000)
}

fun startServer(projectDir: File) {
    println("Starting server (Ctrl+C to stop)...")
    println("=".repeat(50))

    val process = ProcessBuilder(
        "java",
        "-Xmx4G", "-Xms4G",
        "-XX:+UseG1GC",
        "-XX:+ParallelRefProcEnabled",
        "-XX:MaxGCPauseMillis=200",
        "-XX:+UnlockExperimentalVMOptions",
        "-XX:+DisableExplicitGC",
        "-XX:G1NewSizePercent=30",
        "-XX:G1MaxNewSizePercent=40",
        "-XX:G1HeapRegionSize=8M",
        "-XX:G1ReservePercent=20",
        "-XX:G1MixedGCCountTarget=4",
        "-XX:InitiatingHeapOccupancyPercent=15",
        "-XX:G1MixedGCLiveThresholdPercent=90",
        "-XX:SurvivorRatio=32",
        "-XX:+UseStringDeduplication",
        "-jar", "server.jar", "nogui"
    )
        .directory(File(projectDir, ".local-server"))
        .redirectErrorStream(true)
        .start()

    val outputThread = Thread {
        process.inputStream.bufferedReader().forEachLine { line ->
            println(line)
            System.out.flush()
        }
    }
    outputThread.start()
    process.waitFor()
    outputThread.join()
    println("Server stopped with exit code: ${process.exitValue()}")
}

tasks.register("runDev") {
    group = "development"
    dependsOn("localBuild", "runDatabase", "runClient")
    doLast {
        killExistingServer()
        startServer(projectDir)
    }
}

tasks.register("restartServer") {
    group = "development"
    dependsOn("localBuild")
    doLast {
        killExistingServer()
        startServer(projectDir)
    }
}

tasks.register("stopServer") {
    group = "development"
    doLast {
        killExistingServer()
    }
}