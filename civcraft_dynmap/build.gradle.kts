plugins { `java-library` }

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT")
    // Als je later de Dynmap API nodig hebt, voegen we die hier compileOnly toe.
}
dependencies {
    constraints {
        implementation("org.apache.commons:commons-lang3:3.18.0") {
            because("CVE-2025-48924")
        }
    }
}
dependencies {
    implementation("org.apache.commons:commons-lang3:3.18.0")
}



tasks {
    processResources {
        filesMatching("plugin.yml") {
            expand("name" to project.name, "version" to project.version)
        }
    }
    jar { archiveBaseName.set(project.name) }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    withSourcesJar()
}
