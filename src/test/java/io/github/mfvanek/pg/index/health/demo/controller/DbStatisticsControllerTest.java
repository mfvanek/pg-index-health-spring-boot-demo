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
import org.springframework.http.ResponseEntity;

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
        final OffsetDateTime startTestTimestamp = OffsetDateTime.now();
        final String url = String.format("http://localhost:%s/db/statistics/reset", port);
        final ResponseEntity<OffsetDateTime> response = restTemplate.getForEntity(url, OffsetDateTime.class);
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .isBefore(startTestTimestamp);
        assertThat(output.getOut())
                .as("waitForStatisticsCollector should not be called")
                .doesNotContain("vacuum analyze");
    }

    @Test
    void doResetWithoutWaitShouldReturnAccepted(@Nonnull final CapturedOutput output) {
        final long startTime = System.nanoTime();
        final String url = String.format("http://localhost:%s/db/statistics/reset", port);
        final ResponseEntity<OffsetDateTime> response = restTemplate.postForEntity(url, false, OffsetDateTime.class);
        final long executionTime = System.nanoTime() - startTime;
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody())
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
        final String url = String.format("http://localhost:%s/db/statistics/reset", port);
        final ResponseEntity<OffsetDateTime> response = restTemplate.postForEntity(url, true, OffsetDateTime.class);
        final long executionTime = System.nanoTime() - startTime;
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .isNotNull();
        assertThat(executionTime / 1_000_000L)
                .isGreaterThanOrEqualTo(1_000L); // >= 1000ms
        assertThat(output.getOut())
                .as("waitForStatisticsCollector should be called")
                .contains("vacuum analyze");
    }
}
