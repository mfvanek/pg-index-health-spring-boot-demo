/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health-spring-boot-demo
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.index.health.demo.controller;

import io.github.mfvanek.pg.common.management.DatabaseManagement;
import io.github.mfvanek.pg.index.health.demo.utils.BasePgIndexHealthDemoSpringBootTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class DbStatisticsControllerMockTest extends BasePgIndexHealthDemoSpringBootTest {

    @MockBean
    private DatabaseManagement databaseManagement;

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void shouldReturnErrorWhenResetStatisticsUnsuccessful(final boolean wait) {
        Mockito.when(databaseManagement.resetStatistics()).thenReturn(false);
        final String url = String.format("http://localhost:%s/db/statistics/reset", port);
        final ResponseEntity<Object> response = restTemplate.postForEntity(url, wait, Object.class);
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody())
                .isNotNull()
                .satisfies(b -> assertThat(b.toString()).contains("Internal Server Error"));
    }
}
