val compileClasspath = configurations.getByName("compileClasspath")

plugins{
    java
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

java {
    // Eén duidelijke Java 8-config
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

val civLibs = rootProject.file("civcraft/libs")

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/groups/public/")
    flatDir { dirs(civLibs) } // lokale jars
}

dependencies {

    // --- Spigot/CraftBukkit 1.12.2 (NMS/CraftBukkit nodig!) ---
    // Optie A: via BuildTools (aanrader – zet dan craftbukkit in je lokale maven):
    // compileOnly("org.bukkit:craftbukkit:1.12.2-R0.1-SNAPSHOT")
    // (óf compileOnly("org.spigotmc:spigot:1.12.2-R0.1-SNAPSHOT"))

    // Optie B: via lokale server-jar (moet de **server** zijn, niet alleen API!)
    // Zet de daadwerkelijke bestandsnaam goed (meestal ~30–50 MB groot):
    compileOnly(files("$civLibs/spigot-1.12.2.jar"))

    // Alle overige plugin-jars uit libs/, spigot-*.jar uitsluiten
    compileOnly(fileTree(civLibs) {
        include("*.jar")
        exclude("spigot-*.jar")
    })

    // --- Runtime/Compile libs ---
    // MySQL 5.1.x voor com.mysql.jdbc.StringUtils (Connector/J 8.x heeft deze niet meer)
    implementation("mysql:mysql-connector-java:8.0.33")

    // Apache Commons IO voor FileUtils
    implementation("commons-io:commons-io:2.6")

    // Logging + Hikari (zoals je had)
    implementation("com.zaxxer:HikariCP:3.4.5")
    implementation("org.slf4j:slf4j-api:1.7.36")
    implementation("org.slf4j:slf4j-simple:1.7.36")

    // Guava
    implementation("com.google.guava:guava:21.0")

    // Annotations voor @Nonnull/@Nullable
    compileOnly("javax.annotation:javax.annotation-api:1.3.2")

    // --- Tests ---
    testImplementation("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.hamcrest:hamcrest:2.2")
    testImplementation("org.mockito:mockito-core:4.11.0")
    testImplementation("org.mockito:mockito-inline:4.11.0")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(listOf("-Xlint:deprecation", "-Xlint:unchecked"))
}

// Tests (JUnit 4)
tasks.test {
    useJUnit()
    testLogging {
        events("passed", "failed", "skipped", "standardOut", "standardError")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showCauses = true
        showExceptions = true
        showStackTraces = true
    }
}

tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar"){
    archiveClassifier.set("")         // publiceer alleen shadow
    // minimize() // optioneel; uitzetten als je iets mist
    relocate("com.zaxxer.hikari", "com.avrgaming.shaded.hikari")
}

tasks.register<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("spigotCivcraftJar") {
    archiveBaseName.set("spigot-civcraft")
    archiveVersion.set("")
    archiveClassifier.set("")

    from(sourceSets.main.get().output)

    dependencies {
        exclude(dependency("com.google.guava:guava"))
    }

    with(tasks.jar.get())

    manifest {
        attributes(
            "Main-Class" to "org.bukkit.craftbukkit.Main",
            "Implementation-Title" to "Spigot-CivCraft",
            "Implementation-Version" to project.version,
        )
    }
}

tasks.build { dependsOn(tasks.named("shadowJar")) }
tasks.jar { enabled = false }
