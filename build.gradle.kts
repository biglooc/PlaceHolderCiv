plugins { id("java") }


allprojects {
    group = providers.gradleProperty("group").get()
    version = providers.gradleProperty("version").get()
    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}
tasks.withType<JavaCompile> {
    options.release.set(8)
}
subprojects {
    apply(plugin = "java")
    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(providers.gradleProperty("javaVersion").get()))
    }

    subprojects {
        tasks.withType<org.gradle.language.jvm.tasks.ProcessResources>().configureEach {
            val tokens = mapOf(
                "name" to project.name,
                "version" to project.version.toString()
            )
            inputs.properties(tokens)
            filesMatching("plugin.yml") {
                expand(tokens)
            }
        }
    }

}