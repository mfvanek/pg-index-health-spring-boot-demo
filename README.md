# pg-index-health-spring-boot-demo
Demo app for [pg-index-health library](https://github.com/mfvanek/pg-index-health) (with Spring and Spring Boot).  
See also [pg-index-health-test-starter](https://github.com/mfvanek/pg-index-health-test-starter) project.

[![Java CI](https://github.com/mfvanek/pg-index-health-spring-boot-demo/workflows/Java%20CI/badge.svg)](https://github.com/mfvanek/pg-index-health-spring-boot-demo/actions "Java CI")
[![License: Apache 2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/mfvanek/pg-index-health-spring-boot-demo/blob/master/LICENSE "Apache License 2.0")
[![codecov](https://codecov.io/gh/mfvanek/pg-index-health-spring-boot-demo/branch/master/graph/badge.svg?token=NEFMS9CA2N)](https://codecov.io/gh/mfvanek/pg-index-health-spring-boot-demo)

[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=mfvanek_pg-index-health-spring-boot-demo&metric=bugs)](https://sonarcloud.io/summary/new_code?id=mfvanek_pg-index-health-spring-boot-demo)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=mfvanek_pg-index-health-spring-boot-demo&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=mfvanek_pg-index-health-spring-boot-demo)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=mfvanek_pg-index-health-spring-boot-demo&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=mfvanek_pg-index-health-spring-boot-demo)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=mfvanek_pg-index-health-spring-boot-demo&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=mfvanek_pg-index-health-spring-boot-demo)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=mfvanek_pg-index-health-spring-boot-demo&metric=coverage)](https://sonarcloud.io/summary/new_code?id=mfvanek_pg-index-health-spring-boot-demo)

[![Mutation score](https://img.shields.io/badge/Mutation%20score-100%25-green)](https://pitest.org/ "Mutation score 100%")

### Endpoints
Use `demouser/testpwd123` in order to access Actuator endpoints.

#### Swagger UI
http://localhost:8081/actuator/swagger-ui

#### Health
http://localhost:8081/actuator/health

#### Info
http://localhost:8081/actuator/info

#### Metrics
http://localhost:8081/actuator/prometheus

#### Liquibase
http://localhost:8081/actuator/liquibase
