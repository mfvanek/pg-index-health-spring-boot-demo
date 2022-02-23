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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class ActuatorEndpointTest extends BasePgIndexHealthDemoSpringBootTest {

    private static final String ACTUATOR_URL_TEMPLATE = "http://localhost:%s/actuator/%s";

    @BeforeEach
    void setUp() {
        setUpBasicAuth();
    }

    @Test
    void actuatorShouldBeRunOnSeparatePort() {
        assertThat(actuatorPort).isNotEqualTo(port);
    }

    @Test
    void prometheusEndpointShouldReturnMetrics() {
        final String url = String.format(ACTUATOR_URL_TEMPLATE, actuatorPort, "prometheus");
        final ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).contains("jvm_threads_live_threads");
    }

    @Test
    void healthEndpointShouldReturnStatusUp() {
        final String url = String.format(ACTUATOR_URL_TEMPLATE, actuatorPort, "health");
        final ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).contains("{\"status\":\"UP\",\"groups\":[\"liveness\",\"readiness\"]}");
    }

    @Test
    void healthLivenessEndpointShouldReturnStatusUp() {
        final String url = String.format(ACTUATOR_URL_TEMPLATE, actuatorPort, "health/liveness");
        final ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).contains("{\"status\":\"UP\"}");
    }

    @Test
    void healthReadinessEndpointShouldReturnStatusUp() {
        final String url = String.format(ACTUATOR_URL_TEMPLATE, actuatorPort, "health/readiness");
        final ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).contains("{\"status\":\"UP\"}");
    }

    @Test
    void liquibaseEndpointShouldReturnOk() {
        final String url = String.format(ACTUATOR_URL_TEMPLATE, actuatorPort, "liquibase");
        final ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).contains(
                "{\"contexts\":{\"pg-index-health-spring-boot-demo\":{\"liquibaseBeans\":{\"liquibase\":{\"changeSets\"");
    }

    @Test
    void infoEndpointShouldReturnStatusUp() {
        final String url = String.format(ACTUATOR_URL_TEMPLATE, actuatorPort, "info");
        final ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).contains("\"version\":");
    }
}
