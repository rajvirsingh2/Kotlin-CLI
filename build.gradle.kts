plugins {
    kotlin("jvm") version "2.0.0"
    application
    kotlin("plugin.serialization") version "2.0.0"
}

group = "com.rajvir.kotlin_cli"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.0")
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.github.ajalt.clikt:clikt:5.0.1")
   // optional support for rendering markdown in help messages
    implementation("com.github.ajalt.clikt:clikt-markdown:5.0.1")
    implementation("com.github.ajalt.mordant:mordant:2.2.0")
    implementation("com.charleskorn.kaml:kaml:0.55.0")
    implementation("commons-codec:commons-codec:1.15")
}

application {
    mainClass.set("com.rajvir.kotlin_cli.MainKt")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }

    // pull in every runtime dependency (dirs or JARs) lazily,
    // so Gradle’s configuration cache stays happy
    from({
        configurations.runtimeClasspath.get()
            .map { if (it.isDirectory) it else zipTree(it) }
    }) {
        exclude("META-INF/*.DSA", "META-INF/*.SF", "META-INF/*.RSA")
    }

    // optional but handy when dependencies contain duplicate resources
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
