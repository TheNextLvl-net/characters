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
    api("net.thenextlvl:nbt:4.0.0")
    api("net.thenextlvl.core:paper:3.0.0-pre1")
    compileOnly("io.papermc.paper:paper-api:1.21.10-R0.1-SNAPSHOT")
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.addAll(listOf("--add-reads", "net.thenextlvl.characters=ALL-UNNAMED"))
}

tasks.withType<Test>().configureEach {
    jvmArgs("--add-reads", "net.thenextlvl.characters=ALL-UNNAMED")
}

tasks.withType<JavaExec>().configureEach {
    jvmArgs("--add-reads", "net.thenextlvl.characters=ALL-UNNAMED")
}

tasks.withType<Javadoc>().configureEach {
    val options = options as StandardJavadocDocletOptions
    options.tags("apiNote:a:API Note:", "implSpec:a:Implementation Requirements:")
    options.addStringOption("-add-reads", "net.thenextlvl.characters=ALL-UNNAMED")
}

publishing {
    publications.create<MavenPublication>("maven") {
        artifactId = "characters"
        groupId = "net.thenextlvl"
        pom.url.set("https://thenextlvl.net/docs/characters")
        pom.scm {
            val repository = "TheNextLvl-net/characters"
            url.set("https://github.com/$repository")
            connection.set("scm:git:git://github.com/$repository.git")
            developerConnection.set("scm:git:ssh://github.com/$repository.git")
        }
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