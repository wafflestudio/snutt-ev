dependencies {
    implementation(project(":core"))

    implementation("org.springframework.boot:spring-boot-starter-batch")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("com.h2database:h2")
}
