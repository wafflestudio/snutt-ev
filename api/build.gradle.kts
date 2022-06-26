dependencies {
    implementation(project(":core"))
    implementation(project(":service"))
    implementation(project(":data"))

    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.amazonaws:aws-java-sdk-secretsmanager:1.11.965")
    implementation("org.springdoc:springdoc-openapi-ui:1.6.4")
}
