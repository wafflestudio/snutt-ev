plugins {
    kotlin("kapt")
    id("org.hibernate.orm") apply false
}

if (gradle.startParameter.taskNames.none { it.contains("ktlint", ignoreCase = true) }) {
    apply(plugin = "org.hibernate.orm")
    configure<org.hibernate.orm.tooling.gradle.HibernateOrmSpec> {
        enhancement { }
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
