dependencies {
    implementation(project(":core"))

    implementation("org.springframework.boot:spring-boot-starter-batch")
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("com.h2database:h2")
}
