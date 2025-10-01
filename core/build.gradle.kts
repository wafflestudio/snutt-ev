plugins {
    kotlin("kapt")
}

allOpen {
    annotation("jakarta.persistence.Entity")
}

noArg {
    annotation("jakarta.persistence.Entity")
}

dependencies {
    implementation("com.querydsl:querydsl-jpa::jakarta")

    implementation("org.flywaydb:flyway-core:11.13.2")
    implementation("org.flywaydb:flyway-mysql:11.13.2")
    implementation("software.amazon.awssdk:secretsmanager:2.34.6")
    implementation("software.amazon.awssdk:sts:2.34.6")

    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    runtimeOnly("com.mysql:mysql-connector-j")
    kapt("com.querydsl:querydsl-apt::jakarta")
}
