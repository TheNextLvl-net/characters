import io.papermc.hangarpublishplugin.model.Platforms

plugins {
    id("java")
    id("io.github.goooler.shadow") version "8.1.8"
    id("io.papermc.hangar-publish-plugin") version "0.1.2"
    id("net.minecrell.plugin-yml.paper") version "0.6.0"
}

group = "net.thenextlvl.npc"
version = "1.2.5"

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

tasks.compileJava {
    options.release.set(21)
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")

    implementation("org.bstats:bstats-bukkit:3.1.0")
    implementation(project(":api"))

    testImplementation("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.4")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.test {
    useJUnitPlatform()
}

tasks.shadowJar {
    minimize()
    relocate("org.bstats", "net.thenextlvl.npc.bstats")
}

paper {
    name = "Characters"
    main = "net.thenextlvl.npc.FakePlayerAPI"
    apiVersion = "1.21"
    website = "https://thenextlvl.net"
    authors = listOf("NonSwag")
    foliaSupported = true

    serverDependencies {
        register("HologramAPI") {
            required = true
        }
    }
}

val versionString: String = project.version as String
val isRelease: Boolean = !versionString.contains("-pre")

val versions: List<String> = (property("gameVersions") as String)
    .split(",")
    .map { it.trim() }

hangarPublish { // docs - https://docs.papermc.io/misc/hangar-publishing
    publications.register("plugin") {
        id.set("Characters")
        version.set(versionString)
        channel.set(if (isRelease) "Release" else "Snapshot")
        apiKey.set(System.getenv("HANGAR_API_TOKEN"))
        platforms.register(Platforms.PAPER) {
            jar.set(tasks.shadowJar.flatMap { it.archiveFile })
            platformVersions.set(versions)
        }
    }
}