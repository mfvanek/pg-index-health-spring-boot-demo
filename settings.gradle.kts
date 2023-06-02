rootProject.name = "pg-index-health-spring-boot-demo"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            library("postgresql", "org.postgresql:postgresql:42.6.0")
            version("checkstyle", "10.12.0")
            version("pmd", "6.55.0")
            version("jacoco", "0.8.10")
            library("pitest-dashboard-reporter", "it.mulders.stryker:pit-dashboard-reporter:0.2.1")
            version("pitest-junit5Plugin", "1.2.0")
            version("pitest-core", "1.14.1")
            val pgIndexHealth = version("pg-index-health", "0.9.3")
            library("pgIndexHealth-core", "io.github.mfvanek", "pg-index-health")
                    .versionRef(pgIndexHealth)
            library("pgIndexHealth-testStarter", "io.github.mfvanek", "pg-index-health-test-starter")
                    .versionRef(pgIndexHealth)
            val springdoc = version("springdoc", "1.7.0")
            library("springdoc-openapi-ui", "org.springdoc", "springdoc-openapi-ui")
                    .versionRef(springdoc)
            library("springdoc-openapi-security", "org.springdoc", "springdoc-openapi-security")
                    .versionRef(springdoc)
        }
    }
}
