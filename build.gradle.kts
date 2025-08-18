val exposed_version: String by project
val h2_version: String by project
val kotlin_version: String by project
val ktor_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "2.1.10"
    id("io.ktor.plugin") version "3.1.2"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.10"
}

group = "com.example.crm-backend"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    // Koin DI
    implementation("io.insert-koin:koin-core:4.1.0")
    implementation("io.insert-koin:koin-ktor:4.1.0")
    implementation("io.insert-koin:koin-logger-slf4j:4.1.0")

    // Ktor server
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-auth:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jwt:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-server-cors:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor:ktor-server-config-yaml:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")

    // Password hashing
    implementation("org.mindrot:jbcrypt:0.4")

    // Database
    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-json:$exposed_version")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("org.postgresql:postgresql:42.5.4")
    implementation("com.h2database:h2:$h2_version")

    // Logging
    implementation("ch.qos.logback:logback-classic:$logback_version")

    // Testing - Use consistent versions
    testImplementation("io.ktor:ktor-server-test-host:$ktor_version")
    testImplementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    testImplementation("io.ktor:ktor-client-auth:$ktor_version")

    // JUnit 5 (choose one test framework)
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")

    // Kotlin test
    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlin_version")

    // Mockk for mocking
    testImplementation("io.mockk:mockk:1.13.4")

    // H2 for testing
    testImplementation("com.h2database:h2:$h2_version")

    // Koin testing
    testImplementation("io.insert-koin:koin-test:4.1.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}