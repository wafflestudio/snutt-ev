dependencies {
    implementation(project(":core"))

    implementation("org.springframework.boot:spring-boot-starter-batch")
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("com.h2database:h2")
}

graalvmNative {
    binaries {
        named("main") {
            buildArgs.add("--gc=G1")
            buildArgs.add("-R:MaxRAMPercentage=60.0")
        }
    }
}
