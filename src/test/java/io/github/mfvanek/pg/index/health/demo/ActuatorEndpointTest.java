package io.github.mfvanek.pg.index.health.demo;

import io.github.mfvanek.pg.index.health.demo.utils.BasePgIndexHealthDemoSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ActuatorEndpointTest extends BasePgIndexHealthDemoSpringBootTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String ACTUATOR_URL_TEMPLATE = "http://localhost:%s/api/actuator/%s";

    @Test
    void prometheusEndpointShouldReturnMetrics() {
        final String url = String.format(ACTUATOR_URL_TEMPLATE, port, "prometheus");
        final ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        assertNotNull(response.getBody());
        assertThat(response.getBody()).contains("jvm_threads_live_threads");
    }

    @Test
    void healthEndpointShouldReturnStatusUp() {
        final String url = String.format(ACTUATOR_URL_TEMPLATE, port, "health");
        final ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        assertNotNull(response.getBody());
        assertThat(response.getBody()).contains("{\"status\":\"UP\"}");
    }

    @Test
    void liquibaseEndpointShouldReturnOk() {
        final String url = String.format(ACTUATOR_URL_TEMPLATE, port, "liquibase");
        final ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        assertNotNull(response.getBody());
        assertThat(response.getBody()).contains(
                "{\"contexts\":{\"pg-index-health-spring-boot-demo\":{\"liquibaseBeans\":{\"liquibase\":{\"changeSets\"");
    }
}
