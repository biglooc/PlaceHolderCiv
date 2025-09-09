plugins { java }

java { toolchain.languageVersion.set(JavaLanguageVersion.of(21)) }

repositories {
    mavenCentral()
    maven(url = "https://maven.elmakers.com/repository/")
    maven(url = "https://repo.minebench.de/")
    maven(url = "https://mvn.lumine.io/repository/maven-public/")
    maven(url = "https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
    compileOnly("org.dynmap:dynmap-api:3.6") // check de exacte versie die je dynmap plugin gebruikt
    compileOnly(project(":civcraft"))
}

