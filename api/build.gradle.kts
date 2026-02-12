dependencies {
    implementation(project(":core"))

    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("tools.jackson.module:jackson-module-kotlin")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.0")
}

graalvmNative {
    binaries {
        named("main") {
            buildArgs.add("--gc=G1")
            buildArgs.add("-R:MaxRAMPercentage=60.0")
        }
    }
}
