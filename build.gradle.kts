import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.springframework.boot.gradle.tasks.bundling.BootJar
import java.io.ByteArrayOutputStream

plugins {
    id("org.springframework.boot") version "3.2.4" apply false
    id("io.spring.dependency-management") version "1.1.3"
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22"
    kotlin("plugin.allopen") version "1.9.22"
    kotlin("plugin.noarg") version "1.9.22"
    id("org.jlleitschuh.gradle.ktlint") version "11.3.2"
}

group = "com.wafflestudio"
version = "1.0.0"
java.sourceCompatibility = JavaVersion.VERSION_17

allprojects {
    repositories {
        mavenCentral()
        mavenCodeArtifact()
        mavenLocal()
    }
}

subprojects {
    apply {
        plugin("kotlin")
        plugin("org.springframework.boot")
        plugin("org.jetbrains.kotlin.jvm")
        plugin("org.jetbrains.kotlin.plugin.jpa")
        plugin("org.jetbrains.kotlin.plugin.spring")
        plugin("kotlin-spring")
        plugin("io.spring.dependency-management")
        apply(plugin = "org.jlleitschuh.gradle.ktlint")
    }

    dependencies {
        implementation("org.springframework.boot:spring-boot-starter-web")
        implementation("org.springframework.boot:spring-boot-starter-validation")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

        implementation("org.springframework.boot:spring-boot-starter-data-jpa")
        implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

        implementation("com.wafflestudio.truffle.sdk:truffle-spring-boot-starter:1.1.4")
        implementation("com.wafflestudio.truffle.sdk:truffle-logback:1.1.4")

        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("com.h2database:h2")
        testImplementation("org.junit.jupiter:junit-jupiter-api")
        testImplementation("io.mockk:mockk:1.13.5")
        testImplementation("io.kotest:kotest-runner-junit5:5.6.2")
        testImplementation("io.kotest:kotest-assertions-core:5.6.2")
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "17"
        }
    }

    tasks.withType<Test> {
        systemProperty("spring.profiles.active", "test")
        useJUnitPlatform()
    }

    configure<KtlintExtension> {
        debug.set(true)
    }
}

project(":api") {
    val bootJar: BootJar by tasks

    bootJar.archiveFileName.set("snuttev-api.jar")
}

project(":batch") {
    val bootJar: BootJar by tasks

    bootJar.archiveFileName.set("snuttev-batch.jar")
}

project(":core") {
    val jar: Jar by tasks
    val bootJar: BootJar by tasks

    jar.enabled = true
    bootJar.enabled = false
}

fun RepositoryHandler.mavenCodeArtifact() {
    maven {
        val authToken = properties["codeArtifactAuthToken"] as String? ?: ByteArrayOutputStream().use {
            runCatching {
                exec {
                    commandLine = (
                        "aws codeartifact get-authorization-token " +
                            "--domain wafflestudio --domain-owner 405906814034 " +
                            "--query authorizationToken --region ap-northeast-1 --output text"
                        ).split(" ")
                    standardOutput = it
                }
            }
            it.toString()
        }
        url = uri("https://wafflestudio-405906814034.d.codeartifact.ap-northeast-1.amazonaws.com/maven/truffle-kotlin/")
        credentials {
            username = "aws"
            password = authToken
        }
    }
}
