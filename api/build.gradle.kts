plugins {
    id("java")
    id("java-library")
    id("maven-publish")
}

group = rootProject.group
version = rootProject.version


java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
    withSourcesJar()
    withJavadocJar()
}

tasks.compileJava {
    options.release.set(21)
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.thenextlvl.net/releases")
    maven("https://repo.thenextlvl.net/snapshots")
}

dependencies {
    api("net.thenextlvl:nbt:3.0.0-pre1")
    api("net.thenextlvl.core:paper:2.3.0-pre4")
    compileOnly("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT")
}

publishing {
    publications.create<MavenPublication>("maven") {
        artifactId = "characters"
        groupId = "net.thenextlvl"
        from(components["java"])
    }
    repositories.maven {
        val channel = if ((version as String).contains("-pre")) "snapshots" else "releases"
        url = uri("https://repo.thenextlvl.net/$channel")
        credentials {
            username = System.getenv("REPOSITORY_USER")
            password = System.getenv("REPOSITORY_TOKEN")
        }
    }
}