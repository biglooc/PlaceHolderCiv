plugins {
    java
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

sourceSets {
    named("main") {
        java {
            // Exclude legacy NoCheatPlus hooks that are incompatible with 1.21
            exclude("com/avrgaming/civcraft/nocheat/**")
        }
    }
}

dependencies {
    // Use Paper API at compile-time only
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")

    // Additional libraries
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("org.slf4j:slf4j-api:2.0.13")
    implementation("org.slf4j:slf4j-simple:2.0.13")

    // Database driver
    implementation("mysql:mysql-connector-java:8.0.33")

    // Compile-only APIs used by the plugin at runtime from the server
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("commons-io:commons-io:2.15.1")
    // Code uses org.apache.commons.lang.*, which is Commons Lang 2.x
    compileOnly("commons-lang:commons-lang:2.6")

    compileOnly("org.jetbrains:annotations:24.1.0")

    testImplementation("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
    // Support legacy JUnit4 tests and Mockito
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.12.0")
    // JUnit 5 (may be used by newer tests)
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
}

tasks.test {
    useJUnitPlatform()
}

// Shadow jar zoals je al had
tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    archiveClassifier.set("")
}

tasks.build { dependsOn(tasks.named("shadowJar")) }
