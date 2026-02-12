plugins {
    kotlin("kapt")
    id("org.hibernate.orm")
}

hibernate {
    enhancement {
        enableLazyInitialization = true
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
}

noArg {
    annotation("jakarta.persistence.Entity")
}

dependencies {
    implementation("com.querydsl:querydsl-jpa::jakarta")

    implementation("org.springframework.boot:spring-boot-starter-flyway")
    implementation("org.flywaydb:flyway-mysql")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    runtimeOnly("com.mysql:mysql-connector-j")
    kapt("com.querydsl:querydsl-apt::jakarta")
}
