plugins{
    java
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

java { toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8

}
val civLibs = rootProject.file("civcraft/libs")

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/groups/public/")
    flatDir { dirs(civLibs) }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}

dependencies {

    // kies precies één Spigot API (voorbeeld: 1.12.2). Gebruik óf Maven óf je lokale jar.
    // compileOnly("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT")
    compileOnly(files("$civLibs/spigot-1.12.2.jar"))
    compileOnly(files(rootProject.file("civcraft/libs/spigot-1.12.2.jar"))) // kies jouw versie

    // alle overige plugin-JARs uit libs, maar sluit spigot-jars uit
    compileOnly(fileTree(civLibs) {
        include("*.jar")
        exclude("spigot-*.jar")

    })
    implementation("com.zaxxer:HikariCP:3.4.5")
    implementation("org.slf4j:slf4j-api:1.7.36")
    implementation("org.slf4j:slf4j-simple:1.7.36")
    implementation("mysql:mysql-connector-java:8.0.33")

    // Tests (laat staan als je deze al hebt)
    testImplementation("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.hamcrest:hamcrest:2.2")
    testImplementation("org.mockito:mockito-core:4.11.0")
    testImplementation("org.mockito:mockito-inline:4.11.0")
    testImplementation("org.mockito:mockito-core:2.28.2")

    // ➜ Bukkit/Spigot API ook voor tests beschikbaar maken:
    val libsDir = rootProject.file("civcraft/libs")
    testCompileOnly(files("$libsDir/spigot-1.12.2.jar"))
    // (alternatief via Maven:)
    // testCompileOnly("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT")
    tasks.test {
        useJUnit() // JUnit 4
}

// civcraft/build.gradle.kts
tasks.withType<JavaCompile>().configureEach {
    options.encoding = "windows-1252"  // i.p.v. UTF-8
}
tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.addAll(listOf("-Xlint:deprecation", "-Xlint:unchecked"))
    options.encoding = "UTF-8"
}}
tasks.test {
    useJUnit()
    testLogging {
        events("passed", "failed", "skipped", "standardOut", "standardError")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showCauses = true
        showExceptions = true
        showStackTraces = true
    }}
tasks.withType<JavaCompile>{
    options.compilerArgs.removeAll(listOf("--release", "8"))
    options.encoding = "UTF-8"
}
tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar"){
    archiveClassifier.set("")
    relocate("com.zaxxer.hikari.*", "com.avrgaming.shaded.hikari")
}
tasks.build {
    dependsOn(tasks.named("shadowJar"))
}
tasks.jar { enabled = false }