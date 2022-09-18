/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health-spring-boot-demo
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.index.health.demo.controller;

import io.github.mfvanek.pg.index.health.demo.utils.BasePgIndexHealthDemoSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultControllerTest extends BasePgIndexHealthDemoSpringBootTest {

    @Test
    void rootPageShouldRedirectToSwaggerUi() {
        final var result = webTestClient.get()
                .uri("/")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isFound()
                .expectHeader().location(String.format("http://localhost:%d/actuator/swaggerui", port))
                .expectBody()
                .returnResult()
                .getResponseBody();
        assertThat(result)
                .isNull();
    }
}
