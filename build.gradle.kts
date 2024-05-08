val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val postgresVersion: String by project
val koinVersion: String by project
val coroutinesVersion: String by project
val h2Version: String by project

plugins {
    kotlin("jvm") version "1.9.10"
    id("io.ktor.plugin") version "2.3.4"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.10"
    id("org.jlleitschuh.gradle.ktlint") version "11.1.0"
    id("jacoco")
    id("org.jetbrains.dokka") version "1.9.20"
}

tasks.dokkaHtml {
    dokkaSourceSets.configureEach {
        includes.from("Packages.md")

        skipEmptyPackages.set(true)

        perPackageOption {
            matchingRegex.set(".*.(core|di).*")
            suppress.set(true)
        }
    }
}

jacoco {
    version = "0.8.7"
}

val minimumCoverage = "0.85".toBigDecimal()

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    finalizedBy(tasks.jacocoTestCoverageVerification)
    classDirectories.setFrom(
        fileTreeExclusions()
    )
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        classDirectories.setFrom(
            fileTreeExclusions()
        )
        rule {
            classDirectories.setFrom(tasks.jacocoTestReport.get().classDirectories)
            limit {
                minimum = minimumCoverage
            }
        }
    }
}

fun Build_gradle.fileTreeExclusions() =
    sourceSets.main.get().output.asFileTree.matching {
        exclude(
            "**/R.class",
            "**/BuildConfig.*",
            "**/session/**",
            "**/model/**",
            "**/plugins/**",
            "**/di/**",
            "**/security/**",
        )
    }

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

group = "io.lb"
version = "1.0.0"

application {
    mainClass.set("io.lb.warehouse.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("org.postgresql:postgresql:$postgresVersion")
    implementation("com.h2database:h2:$h2Version")
    implementation("io.ktor:ktor-server-websockets-jvm")
    implementation("io.ktor:ktor-serialization-gson-jvm")
    implementation("io.ktor:ktor-server-call-logging-jvm")
    implementation("io.ktor:ktor-server-auth-jvm")
    implementation("io.ktor:ktor-server-auth-jwt")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    implementation("io.insert-koin:koin-ktor:$koinVersion")
    implementation("io.insert-koin:koin-logger-slf4j:$koinVersion")
    implementation("io.ktor:ktor-server-sessions:$ktorVersion")
    implementation("org.mindrot:jbcrypt:0.4")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.zaxxer:HikariCP:4.0.3")

    testImplementation("io.mockk:mockk:1.13.4")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.3")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.3")
    testImplementation("com.willowtreeapps.assertk:assertk:0.27.0")
    testImplementation("app.cash.turbine:turbine:1.0.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")
    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
}
