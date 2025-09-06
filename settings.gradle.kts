pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
        mavenCentral()
    }
}
rootProject.name = "civcraft-monorepo"
include("civcraft", "civcraft_dynmap")
// settings.gradle.kts
//include(":civcraft", ":civcraft-common", ":civcraft-threading") // voorbeeld