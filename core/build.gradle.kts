plugins {
    kotlin("kapt")
}

allOpen {
    annotation("javax.persistence.Entity")
}

noArg {
    annotation("javax.persistence.Entity")
}

dependencies {
    implementation("com.querydsl:querydsl-jpa")

    implementation("org.flywaydb:flyway-core:8.5.12")
    implementation("org.flywaydb:flyway-mysql:8.5.12")
    implementation("software.amazon.awssdk:secretsmanager:2.17.276")
    implementation("software.amazon.awssdk:sts:2.17.276")

    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    runtimeOnly("mysql:mysql-connector-java")
    kapt("com.querydsl:querydsl-apt::jpa")
}

sourceSets["main"].withConvention(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet::class) {
    kotlin.srcDir("$buildDir/generated/source/kapt/main")
}
