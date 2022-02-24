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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DbStatisticsControllerTest extends BasePgIndexHealthDemoSpringBootTest {

    @BeforeEach
    void setUp() {
        setUpBasicAuth();
    }

    @Test
    void getLastResetDateShouldNotReturnNull() {
        final OffsetDateTime startTestTimestamp = OffsetDateTime.now();
        final String url = String.format("http://localhost:%s/db/statistics/reset", port);
        final ResponseEntity<OffsetDateTime> response = restTemplate.getForEntity(url, OffsetDateTime.class);
        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        final OffsetDateTime resetTimestamp = response.getBody();
        assertThat(resetTimestamp).isNotNull();
        assertThat(resetTimestamp).isBefore(startTestTimestamp);
    }

    @Test
    void doResetWithoutWaitShouldReturnAccepted() {
        final long startTime = System.nanoTime();
        final String url = String.format("http://localhost:%s/db/statistics/reset", port);
        final ResponseEntity<OffsetDateTime> response = restTemplate.postForEntity(url, false, OffsetDateTime.class);
        final long executionTime = System.nanoTime() - startTime;
        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.ACCEPTED);
        final OffsetDateTime resetTimestamp = response.getBody();
        assertThat(resetTimestamp).isNotNull();
        assertThat(executionTime / 1000_000L).isLessThan(1_000L); // less than 1000ms
    }

    @Test
    void doResetWithWaitShouldReturnOk() {
        final long startTime = System.nanoTime();
        final String url = String.format("http://localhost:%s/db/statistics/reset", port);
        final ResponseEntity<OffsetDateTime> response = restTemplate.postForEntity(url, true, OffsetDateTime.class);
        final long executionTime = System.nanoTime() - startTime;
        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        final OffsetDateTime resetTimestamp = response.getBody();
        assertThat(resetTimestamp).isNotNull();
        assertThat(executionTime / 1000_000L).isGreaterThanOrEqualTo(1_000L); // >= 1000ms
    }
}
