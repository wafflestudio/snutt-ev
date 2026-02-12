import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot") version "3.5.4" apply false
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.spring") version "2.2.0"
    kotlin("plugin.allopen") version "2.2.0"
    kotlin("plugin.noarg") version "2.2.0"
    id("org.jlleitschuh.gradle.ktlint") version "13.0.0"
}

group = "com.wafflestudio"
version = "1.0.0"
java.sourceCompatibility = JavaVersion.VERSION_21

allprojects {
    repositories {
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.github.com/wafflestudio/spring-waffle")
            credentials {
                username = "wafflestudio"
                password = findProperty("gpr.key") as String?
                    ?: System.getenv("GITHUB_TOKEN")
                    ?: runCatching {
                        ProcessBuilder("gh", "auth", "token")
                            .start()
                            .inputStream
                            .bufferedReader()
                            .readText()
                            .trim()
                    }.getOrDefault("")
            }
        }
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
        api(platform(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES))

        implementation("org.springframework.boot:spring-boot-starter-web")
        implementation("org.springframework.boot:spring-boot-starter-validation")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

        implementation("org.springframework.boot:spring-boot-starter-data-jpa")
        implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

        implementation("com.wafflestudio.spring:spring-boot-starter-waffle-oci-vault:2.1.0-SNAPSHOT")
        implementation("com.wafflestudio.spring.truffle:spring-boot-starter-truffle:2.1.0-SNAPSHOT")

        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("com.h2database:h2")
        testImplementation("org.junit.jupiter:junit-jupiter-api")
        testImplementation("io.mockk:mockk:1.14.5")
        testImplementation("io.kotest:kotest-runner-junit5:6.0.3")
        testImplementation("io.kotest:kotest-assertions-core:6.0.3")
    }

    tasks.withType<KotlinCompile> {
        compilerOptions {
            freeCompilerArgs.add("-Xjsr305=strict")
            jvmTarget.set(JvmTarget.JVM_21)
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

