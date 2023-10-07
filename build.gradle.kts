import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.nitro4friends"
version = "1.0-SNAPSHOT"

val ktorVersion: String by project
val exposedVersion: String by project

repositories {
    mavenCentral()
    maven("https://repo.flawcra.cc/mirrors")
}

val shadowDependencies = listOf(
    "io.javalin:javalin-bundle:5.6.2",

    "io.ktor:ktor-client-core:$ktorVersion",
    "io.ktor:ktor-client-okhttp:$ktorVersion",
    "io.ktor:ktor-client-cio:$ktorVersion",
    "io.ktor:ktor-client-cio-jvm:$ktorVersion",
    "io.ktor:ktor-serialization-kotlinx-json:$ktorVersion",
    "io.ktor:ktor-client-content-negotiation:$ktorVersion",


    "org.jetbrains.exposed:exposed-core:$exposedVersion",
    "org.jetbrains.exposed:exposed-dao:$exposedVersion",
    "org.jetbrains.exposed:exposed-jdbc:$exposedVersion",
    "org.jetbrains.exposed:exposed-java-time:$exposedVersion",
    "com.zaxxer:HikariCP:5.0.1",

    "com.github.TheFruxz:Ascend:2023.3",

    "io.github.cdimascio:dotenv-kotlin:6.4.1",
)

dependencies {
    testImplementation(kotlin("test"))

    shadowDependencies.forEach { dependency ->
        implementation(dependency)
        shadow(dependency)
    }
}


tasks {

    test {
        useJUnitPlatform()
    }

    build {
        dependsOn("shadowJar")
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
        kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
    }

    withType<ShadowJar> {
        mergeServiceFiles()
        configurations = listOf(project.configurations.shadow.get())
        archiveFileName.set("Nitro4Friends.jar")
    }
}

kotlin {
    jvmToolchain(17)
}