plugins { id("java") }


allprojects {
    group = providers.gradleProperty("group").get()
    version = providers.gradleProperty("version").get()
    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://jitpack.io")
    }
}
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}
subprojects {
    apply(plugin = "java")
    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(providers.gradleProperty("javaVersion").get()))
    }

    // Exclude legacy NMS-heavy listener from civcraft module to allow 1.21 build
    if (project.name == "civcraft") {
        the<SourceSetContainer>().named("main") {
            java.srcDir("src/main/java")
            java.setSrcDirs(java.srcDirs)
            // No exclusions; ensure all sources are compiled for 1.21
            // java.exclude("com/avrgaming/civcraft/listener/BlockListener.java")
        }
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