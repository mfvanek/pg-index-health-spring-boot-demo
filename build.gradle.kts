import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.github.spotbugs.snom.Confidence
import com.github.spotbugs.snom.Effort
import com.github.spotbugs.snom.SpotBugsTask
import info.solidsoft.gradle.pitest.PitestTask
import net.ltgt.gradle.errorprone.errorprone

plugins {
    id("java")
    id("org.springframework.boot") version "2.7.14"
    id("io.spring.dependency-management") version "1.1.2"
    id("com.github.spotbugs") version "5.1.1"
    id("checkstyle")
    id("jacoco")
    id("pmd")
    id("org.sonarqube") version "4.3.0.3225"
    id("info.solidsoft.pitest") version "1.9.11"
    id("io.freefair.lombok") version "8.1.0"
    id("com.google.osdetector") version "1.7.3"
    id("net.ltgt.errorprone") version "3.1.0"
    id("org.gradle.test-retry") version "1.5.4"
    id("com.github.ben-manes.versions") version "0.47.0"
}

group = "io.github.mfvanek"
version = "0.9.4"

repositories {
    mavenLocal()
    mavenCentral()
}

configurations.configureEach {
    exclude("org.hamcrest")
}

dependencies {
    implementation("io.micrometer:micrometer-registry-prometheus:1.11.2")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation(libs.springdoc.openapi.ui)
    implementation(libs.springdoc.openapi.security)
    implementation("org.liquibase:liquibase-core:4.23.0")
    runtimeOnly(libs.postgresql)
    implementation(platform("org.testcontainers:testcontainers-bom:1.18.3"))
    implementation("org.testcontainers:testcontainers")
    implementation("org.testcontainers:postgresql")
    implementation(libs.pg.index.health.core)
    implementation("com.google.code.findbugs:jsr305:3.0.2")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webflux")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation(libs.pg.index.health.testStarter)
    testImplementation("org.apache.httpcomponents.client5:httpclient5:5.2.1")
    testImplementation(libs.postgresql)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher") {
        because("required for pitest")
    }

    // https://github.com/netty/netty/issues/11020
    if (osdetector.arch == "aarch_64") {
        testImplementation("io.netty:netty-all:4.1.96.Final")
    }

    pitest(libs.pitest.dashboard.reporter)
    checkstyle("com.thomasjensen.checkstyle.addons:checkstyle-addons:7.0.1")
    errorprone("com.google.errorprone:error_prone_core:2.20.0")
}

dependencyManagement {
    imports {
        // Need use this instead of 'testImplementation(platform("org.junit:junit-bom:5.9.3"))'
        // to update junit at runtime as well
        mavenBom("org.junit:junit-bom:5.10.0")
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
tasks.withType<JavaCompile>().configureEach {
    options.errorprone {
        disableWarningsInGeneratedCode.set(true)
    }
}

jacoco {
    toolVersion = libs.versions.jacoco.get()
}

tasks {
    test {
        useJUnitPlatform()
        dependsOn(checkstyleMain, checkstyleTest, pmdMain, pmdTest, spotbugsMain, spotbugsTest)
        maxParallelForks = 1
        finalizedBy(jacocoTestReport, jacocoTestCoverageVerification)

        retry {
            maxRetries.set(1)
            maxFailures.set(3)
            failOnPassedAfterRetry.set(false)
        }
    }

    jacocoTestReport {
        dependsOn(test)
        reports {
            xml.required.set(true)
            html.required.set(true)
        }
    }
    withType<JacocoReport> {
        afterEvaluate {
            classDirectories.setFrom(files(classDirectories.files.map {
                fileTree(it) {
                    exclude("**/PgIndexHealthSpringBootDemoApplication.class")
                }
            }))
        }
    }
    jacocoTestCoverageVerification {
        dependsOn(jacocoTestReport)
        violationRules {
            classDirectories.setFrom(jacocoTestReport.get().classDirectories)
            rule {
                limit {
                    counter = "CLASS"
                    value = "MISSEDCOUNT"
                    maximum = "0.0".toBigDecimal()
                }
            }
            rule {
                limit {
                    counter = "METHOD"
                    value = "MISSEDCOUNT"
                    maximum = "0.0".toBigDecimal()
                }
            }
            rule {
                limit {
                    counter = "LINE"
                    value = "MISSEDCOUNT"
                    maximum = "1.0".toBigDecimal()
                }
            }
            rule {
                limit {
                    counter = "INSTRUCTION"
                    value = "COVEREDRATIO"
                    minimum = "0.97".toBigDecimal()
                }
            }
        }
    }

    check {
        dependsOn(jacocoTestCoverageVerification)
    }
}

springBoot {
    buildInfo()
}

checkstyle {
    toolVersion = libs.versions.checkstyle.get()
    configFile = file("config/checkstyle/checkstyle.xml")
    isIgnoreFailures = false
    maxWarnings = 0
    maxErrors = 0
}

spotbugs {
    showProgress.set(true)
    effort.set(Effort.MAX)
    reportLevel.set(Confidence.LOW)
    excludeFilter.set(file("config/spotbugs/exclude.xml"))
}
tasks.withType<SpotBugsTask>().configureEach {
    reports {
        create("xml") { enabled = true }
        create("html") { enabled = true }
    }
}

pmd {
    toolVersion = libs.versions.pmd.get()
    isConsoleOutput = true
    ruleSetFiles = files("config/pmd/pmd.xml")
    ruleSets = listOf()
}

sonar {
    properties {
        property("sonar.projectKey", "mfvanek_pg-index-health-spring-boot-demo")
        property("sonar.organization", "mfvanek")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}

pitest {
    verbosity.set("DEFAULT")
    junit5PluginVersion.set(libs.versions.pitest.junit5Plugin.get())
    pitestVersion.set(libs.versions.pitest.core.get())
    threads.set(4)
    if (System.getenv("STRYKER_DASHBOARD_API_KEY") != null) {
        outputFormats.set(setOf("stryker-dashboard"))
    } else {
        outputFormats.set(setOf("HTML"))
    }
    timestampedReports.set(false)
    mutationThreshold.set(100)
    excludedClasses.set(listOf("io.github.mfvanek.pg.index.health.demo.config.*",
        "io.github.mfvanek.pg.index.health.demo.PgIndexHealthSpringBootDemoApplication"))
    excludedTestClasses.set(listOf("io.github.mfvanek.pg.index.health.demo.ActuatorEndpointTest"))
}
tasks.withType<PitestTask>().configureEach {
    mustRunAfter(tasks.test)
}
tasks.build {
    dependsOn("pitest")
}

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}

tasks.named<DependencyUpdatesTask>("dependencyUpdates").configure {
    checkForGradleUpdate = true
    gradleReleaseChannel = "current"
    checkConstraints = true
    rejectVersionIf {
        isNonStable(candidate.version)
    }
}
