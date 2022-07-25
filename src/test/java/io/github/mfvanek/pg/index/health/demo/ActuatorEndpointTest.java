/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health-spring-boot-demo
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.index.health.demo;

import io.github.mfvanek.pg.index.health.demo.utils.BasePgIndexHealthDemoSpringBootTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.annotation.Nonnull;

import static org.assertj.core.api.Assertions.assertThat;

class ActuatorEndpointTest extends BasePgIndexHealthDemoSpringBootTest {

    private static final String ACTUATOR_URL_TEMPLATE = "http://localhost:%s/actuator/%s";

    @BeforeEach
    void setUp() {
        setUpBasicAuth();
    }

    @Test
    void actuatorShouldBeRunOnSeparatePort() {
        assertThat(actuatorPort)
                .isNotEqualTo(port);
    }

    @ParameterizedTest
    @CsvSource(value = {
        "prometheus|jvm_threads_live_threads",
        "health|{\"status\":\"UP\",\"groups\":[\"liveness\",\"readiness\"]}",
        "health/liveness|{\"status\":\"UP\"}",
        "health/readiness|{\"status\":\"UP\"}",
        "liquibase|{\"contexts\":{\"pg-index-health-spring-boot-demo\":{\"liquibaseBeans\":{\"liquibase\":{\"changeSets\"",
        "info|\"version\":"}, delimiter = '|')
    void actuatorEndpointShouldReturnOk(@Nonnull final String endpointName, @Nonnull final String expectedSubstring) {
        final String url = String.format(ACTUATOR_URL_TEMPLATE, actuatorPort, endpointName);
        final ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .contains(expectedSubstring);
    }

    @Test
    void swaggerUiEndpointShouldReturnFound() {
        final String url = String.format(ACTUATOR_URL_TEMPLATE, actuatorPort, "swaggerui");
        final ResponseEntity<Void> response = restTemplate.getForEntity(url, Void.class);
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation())
                .hasToString("/actuator/swagger-ui/index.html");
    }
}
