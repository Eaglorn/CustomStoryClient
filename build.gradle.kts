plugins {
    id("java")
    id("application")
    id("org.jetbrains.kotlin.jvm") version "2.1.10"
    id("io.ktor.plugin") version "3.0.3"
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("org.beryx.runtime") version "1.13.1"
    id("com.google.protobuf") version "0.9.5"
}

group = "ru.eaglorn.csc"
version = "0.0.1"

repositories {
    mavenCentral()
}

tasks.named<JavaExec>("runShadow") {
    standardInput = System.`in`
}

application {
    mainClass = "ClientApplicationKt"
    applicationName = "CustomStoryClient"
}

kotlin {
    jvmToolchain(23)
}

val ktorVersion = "3.0.3"
val kotlinxCoroutinesVersion = "1.10.1"
val logbackVersion = "1.5.16"
val kotlinVersion = "2.1.10"
val protobufVersion = "4.30.2"
val zstdVersion = "1.5.7-2"
val controlsfxVersion = "11.2.1"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
    implementation("io.ktor:ktor-network:$ktorVersion")
    implementation("io.ktor:ktor-network-tls:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("org.controlsfx:controlsfx:$controlsfxVersion")
    implementation("com.google.protobuf:protobuf-java:$protobufVersion")
    implementation("com.github.luben:zstd-jni:$zstdVersion")
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

sourceSets {
    main {
        proto {
            srcDir("src/main/proto")
        }
        java {
            srcDir("build/generated/source/proto/main/java")
        }
    }
}