# pg-index-health-spring-boot-demo
Demo app for [pg-index-health library](https://github.com/mfvanek/pg-index-health) (with Spring and Spring Boot).  
See also [pg-index-health-test-starter](https://github.com/mfvanek/pg-index-health-test-starter) project.

[![Java CI](https://github.com/mfvanek/pg-index-health-spring-boot-demo/workflows/Java%20CI/badge.svg)](https://github.com/mfvanek/pg-index-health-spring-boot-demo/actions "Java CI")
[![License: Apache 2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/mfvanek/pg-index-health-spring-boot-demo/blob/master/LICENSE "Apache License 2.0")

### Endpoints
Use `demouser/testpwd123` in order to access Actuator endpoints.

#### Swagger UI
http://localhost:8081/actuator/swaggerui

#### Health
http://localhost:8081/actuator/health

#### Info
http://localhost:8081/actuator/info

#### Metrics
http://localhost:8081/actuator/prometheus

#### Liquibase
http://localhost:8081/actuator/liquibase
