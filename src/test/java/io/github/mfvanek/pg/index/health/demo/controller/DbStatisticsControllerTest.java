/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health-spring-boot-demo
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.index.health.demo.controller;

import io.github.mfvanek.pg.index.health.demo.utils.BasePgIndexHealthDemoSpringBootTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.OffsetDateTime;
import javax.annotation.Nonnull;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(OutputCaptureExtension.class)
class DbStatisticsControllerTest extends BasePgIndexHealthDemoSpringBootTest {

    @BeforeEach
    void setUp() {
        setUpBasicAuth();
    }

    @Test
    void getLastResetDateShouldNotReturnNull(@Nonnull final CapturedOutput output) {
        final var startTestTimestamp = OffsetDateTime.now(clock);
        final var result = webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment("db", "statistics", "reset")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.OK)
                .expectBody(OffsetDateTime.class)
                .returnResult()
                .getResponseBody();
        assertThat(result)
                .isBefore(startTestTimestamp);
        assertThat(output.getOut())
                .as("waitForStatisticsCollector should not be called")
                .doesNotContain("vacuum analyze");
    }

    @Test
    void doResetWithoutWaitShouldReturnAccepted(@Nonnull final CapturedOutput output) {
        final long startTime = System.nanoTime();
        final var result = webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment("db", "statistics", "reset")
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(false)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.ACCEPTED)
                .expectBody(OffsetDateTime.class)
                .returnResult()
                .getResponseBody();
        final long executionTime = System.nanoTime() - startTime;
        assertThat(result)
                .isNotNull();
        assertThat(executionTime / 1_000_000L)
                .isLessThan(1_000L); // less than 1000ms
        assertThat(output.getOut())
                .as("waitForStatisticsCollector should not be called")
                .doesNotContain("vacuum analyze");
    }

    @Test
    void doResetWithWaitShouldReturnOk(@Nonnull final CapturedOutput output) {
        final long startTime = System.nanoTime();
        final var result = webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment("db", "statistics", "reset")
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(true)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.OK)
                .expectBody(OffsetDateTime.class)
                .returnResult()
                .getResponseBody();
        final long executionTime = System.nanoTime() - startTime;
        assertThat(result)
                .isNotNull();
        assertThat(executionTime / 1_000_000L)
                .isGreaterThanOrEqualTo(1_000L); // >= 1000ms
        assertThat(output.getOut())
                .as("waitForStatisticsCollector should be called")
                .contains("vacuum analyze");
    }
}
