plugins {
    kotlin("kapt")
    id("org.hibernate.orm") apply false
}

if (gradle.startParameter.taskNames.none { it.contains("ktlint", ignoreCase = true) }) {
    apply(plugin = "org.hibernate.orm")
    configure<org.hibernate.orm.tooling.gradle.HibernateOrmSpec> {
        // 빈 블록이지만 bytecode enhancement의 opt-in 트리거이므로 제거하면 안 됨.
        // (enableLazyInitialization 등 세부 옵션은 기본값 true라 명시 불필요)
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
