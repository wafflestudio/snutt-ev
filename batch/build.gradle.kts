dependencies {
    implementation(project(":core"))
    implementation(project(":data"))
    implementation(project(":service"))

    implementation("org.springframework.boot:spring-boot-starter-batch")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("com.amazonaws:aws-java-sdk-secretsmanager:1.11.965")
    runtimeOnly("org.postgresql:postgresql")
}
