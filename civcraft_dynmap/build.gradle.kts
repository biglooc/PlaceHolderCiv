plugins {
    java
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

repositories {
    mavenCentral()

    // Dynmap API mirrors (niet altijd op Central):
    maven(url = "https://maven.elmakers.com/repository/")
    maven(url = "https://repo.minebench.de/")
    maven(url = "https://mvn.lumine.io/repository/maven-public/")
    maven(url = "https://www.iani.de/nexus/content/repositories/releases/")
}

dependencies {
    testImplementation("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT")
    // Verwijder deze als je 'm had (conflicteert met Paper):
    // compileOnly("org.spigotmc:spigot-api:1.20.6-R0.1-SNAPSHOT")

    // Dynmap API – exclude de oude Bukkit/Spigot om capability-conflict te voorkomen
    compileOnly("org.dynmap:dynmap-api:1.9") {
    }

    // Jouw CivCraft core als compileOnly (server levert 'm runtime mee)
    compileOnly(project(":civcraft"))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

