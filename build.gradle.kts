import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.github.spotbugs.snom.Confidence
import com.github.spotbugs.snom.Effort
import com.github.spotbugs.snom.SpotBugsTask
import info.solidsoft.gradle.pitest.PitestTask
import net.ltgt.gradle.errorprone.errorprone

plugins {
    id("java")
    id("org.springframework.boot") version "2.7.18"
    id("io.spring.dependency-management") version "1.1.4"
    id("com.github.spotbugs") version "6.0.2"
    id("checkstyle")
    id("jacoco")
    id("pmd")
    id("org.sonarqube") version "4.4.1.3373"
    id("info.solidsoft.pitest") version "1.15.0"
    id("io.freefair.lombok") version "8.4"
    id("com.google.osdetector") version "1.7.3"
    id("net.ltgt.errorprone") version "3.1.0"
    id("org.gradle.test-retry") version "1.5.8"
    id("com.github.ben-manes.versions") version "0.50.0"
}

group = "io.github.mfvanek"
version = "0.10.2"

repositories {
    mavenLocal()
    mavenCentral()
}

configurations.configureEach {
    exclude("org.hamcrest")
}

ext["commons-lang3.version"] = "3.13.0"

dependencies {
    implementation("io.micrometer:micrometer-registry-prometheus:1.12.1")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation(libs.springdoc.openapi.ui)
    implementation(libs.springdoc.openapi.security)
    implementation("org.liquibase:liquibase-core:4.25.0")
    implementation(platform("org.testcontainers:testcontainers-bom:1.19.3"))
    implementation("org.testcontainers:testcontainers")
    implementation("org.testcontainers:postgresql")
    implementation(platform("io.github.mfvanek:pg-index-health-bom:0.10.2"))
    implementation("io.github.mfvanek:pg-index-health")
    implementation("com.google.code.findbugs:jsr305:3.0.2")
    implementation("com.github.blagerweij:liquibase-sessionlock:1.6.9")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    runtimeOnly(libs.postgresql)

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webflux")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("io.github.mfvanek:pg-index-health-test-starter")
    testImplementation("org.apache.httpcomponents.client5:httpclient5")
    testImplementation(libs.postgresql)

    // https://github.com/netty/netty/issues/11020
    if (osdetector.arch == "aarch_64") {
        testImplementation("io.netty:netty-all:4.1.101.Final")
    }

    pitest(libs.pitest.dashboard.reporter)
    checkstyle("com.thomasjensen.checkstyle.addons:checkstyle-addons:7.0.1")

    errorprone("com.google.errorprone:error_prone_core:2.23.0")
    errorprone("jp.skypencil.errorprone.slf4j:errorprone-slf4j:0.1.21")

    spotbugsSlf4j(rootProject.libs.slf4j.simple)
    spotbugsPlugins("jp.skypencil.findbugs.slf4j:bug-pattern:1.5.0")
    spotbugsPlugins("com.h3xstream.findsecbugs:findsecbugs-plugin:1.12.0")
    spotbugsPlugins("com.mebigfatguy.sb-contrib:sb-contrib:7.6.3")
}

dependencyManagement {
    imports {
        // Need use this instead of 'testImplementation(platform("org.junit:junit-bom:5.9.3"))'
        // to update junit at runtime as well
        mavenBom("org.junit:junit-bom:5.10.1")
        mavenBom("org.apache.httpcomponents.client5:httpclient5-parent:5.3")
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}
tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-parameters")
    options.errorprone {
        disableWarningsInGeneratedCode.set(true)
        disable("Slf4jLoggerShouldBeNonStatic")
    }
}

jacoco {
    toolVersion = libs.versions.jacoco.get()
}

tasks {
    wrapper {
        gradleVersion = "8.5"
    }

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
