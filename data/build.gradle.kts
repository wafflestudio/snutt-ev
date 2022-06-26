plugins {
    kotlin("kapt")
}

allOpen {
    annotation("javax.persistence.Entity")
}

noArg {
    annotation("javax.persistence.Entity")
}

dependencies {
    implementation(project(":core"))
    implementation("com.querydsl:querydsl-jpa")

    runtimeOnly("mysql:mysql-connector-java")
    kapt("com.querydsl:querydsl-apt::jpa")
}

sourceSets["main"].withConvention(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet::class) {
    kotlin.srcDir("$buildDir/generated/source/kapt/main")
}
