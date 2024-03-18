rootProject.name = "pg-index-health-spring-boot-demo"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            library("postgresql", "org.postgresql:postgresql:42.7.3")
            version("checkstyle", "10.12.7")
            version("pmd", "6.55.0")
            version("jacoco", "0.8.11")
            library("pitest-dashboard-reporter", "it.mulders.stryker:pit-dashboard-reporter:0.2.1")
            version("pitest-junit5Plugin", "1.2.1")
            version("pitest-core", "1.15.3")
            val springdoc = version("springdoc", "1.7.0")
            library("springdoc-openapi-ui", "org.springdoc", "springdoc-openapi-ui")
                    .versionRef(springdoc)
            library("springdoc-openapi-security", "org.springdoc", "springdoc-openapi-security")
                    .versionRef(springdoc)
            library("slf4j-simple", "org.slf4j:slf4j-simple:1.7.36") // to be compatible with Spring Boot 2.7.X
        }
    }
}
