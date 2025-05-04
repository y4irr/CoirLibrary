plugins {
    kotlin("jvm") version "1.9.24"
    id("com.gradleup.shadow") version "8.3.0"
}

group = "dev.y4irr"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()

    maven("https://maven.elmakers.com/repository/") {
        name = "elmakers-maven"
    }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation(kotlin("reflect"))
}

val targetJavaVersion = 17

kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks.build {
    dependsOn("shadowJar")
}
