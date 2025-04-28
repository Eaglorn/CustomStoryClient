plugins {
    id("java")
    id("application")
    id("org.jetbrains.kotlin.jvm") version "2.1.20"
    id("io.ktor.plugin") version "3.0.3"
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("org.beryx.runtime") version "1.13.1"
    id("com.google.protobuf") version "0.9.5"
}

group = "ru.eaglorn.cs"
version = "0.0.1"

repositories {
    mavenCentral()
}

tasks.named<JavaExec>("runShadow") {
    standardInput = System.`in`
}

application {
    mainClass = "ru.eaglorn.cs.ClientApplicationKt"
    applicationName = "CustomStoryClient"
}

kotlin {
    jvmToolchain(23)
}

val kotlinVersion = "2.1.20"
val kotlinxCoroutinesVersion = "1.10.2"
val ktorVersion = "3.1.2"
val log4jVersion = "2.24.3"
val protobufVersion = "4.30.2"
val zstdVersion = "1.5.7-2"
val springVersion = "3.4.4"
val eclipseCollections = "11.1.0"
val controlsfxVersion = "11.2.1"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
    implementation("io.ktor:ktor-network:$ktorVersion")
    implementation("io.ktor:ktor-network-tls:$ktorVersion")
    implementation("org.controlsfx:controlsfx:$controlsfxVersion")
    implementation("com.google.protobuf:protobuf-java:$protobufVersion")
    implementation("com.github.luben:zstd-jni:$zstdVersion")
    implementation("org.apache.logging.log4j:log4j-api:${log4jVersion}")
    implementation("org.apache.logging.log4j:log4j-core:${log4jVersion}")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:${log4jVersion}")
    implementation("org.springframework.boot:spring-boot-starter:$springVersion") {
        exclude("org.springframework.boot","spring-boot-starter-logging")
    }
    implementation("org.eclipse.collections:eclipse-collections-api:$eclipseCollections")
    implementation("org.eclipse.collections:eclipse-collections:$eclipseCollections")
}

javafx {
    version = "24.0.1"
    modules("javafx.controls", "javafx.fxml", "javafx.web", "javafx.swing")
}

runtime {
    options.add("--strip-debug")
    options.add("--compress")
    options.add("2")
    options.add("--no-header-files")
    options.add("--no-man-pages")

    targetPlatform("win") {
        jdkHome = jdkDownload("https://github.com/AdoptOpenJDK/semeru23-binaries/releases/download/jdk-23.0.1%2B11_openj9-0.48.0/ibm-semeru-open-jdk_x64_windows_23.0.1_11_openj9-0.48.0.zip")
    }
    launcher {
        noConsole = true
    }
    jpackage {
        val imgType = "png"
        imageOptions.add("--icon")
        imageOptions.add("src/main/resources/hellofx.$imgType")
        installerOptions.add("--resource-dir")
        installerOptions.add("src/main/resources")
        installerOptions.add("--vendor")
        installerOptions.add("Acme Corporation")
        installerOptions.add("--win-per-user-install")
        installerOptions.add("--win-dir-chooser")
        installerOptions.add("--win-menu")
        installerOptions.add("--win-shortcut")
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:$protobufVersion"
    }
}
