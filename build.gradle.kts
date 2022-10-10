import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot") version "2.7.2"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
    kotlin("plugin.allopen") version "1.6.21"
    kotlin("plugin.noarg") version "1.6.21"
    id("org.jlleitschuh.gradle.ktlint") version "10.3.0"
}

allprojects {
    repositories {
        mavenCentral()
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

    group = "com.wafflestudio.snuttev"
    version = "1.0.0"
    java.sourceCompatibility = JavaVersion.VERSION_17

    dependencies {
        implementation("org.springframework.boot:spring-boot-starter-web")
        implementation("org.springframework.boot:spring-boot-starter-validation")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        implementation("org.springframework.boot:spring-boot-starter-data-jpa")

        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("com.h2database:h2")
        testImplementation("org.junit.jupiter:junit-jupiter-api")
        testImplementation("io.mockk:mockk:1.12.4")
        testImplementation("io.kotest:kotest-runner-junit5:5.3.0")
        testImplementation("io.kotest:kotest-assertions-core:5.3.0")
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
    tasks.bootJar {
        archiveFileName.set("snuttev-api.jar")
    }
}

project(":batch") {
    tasks.bootJar {
        archiveFileName.set("snuttev-batch.jar")
    }
}

project(":core") {
    val jar: Jar by tasks
    val bootJar: BootJar by tasks

    jar.enabled = true
    bootJar.enabled = false
}

val jar: Jar by tasks
val bootJar: BootJar by tasks

jar.enabled = true
bootJar.enabled = false
