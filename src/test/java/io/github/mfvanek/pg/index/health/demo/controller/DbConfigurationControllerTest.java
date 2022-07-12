/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health-spring-boot-demo
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.index.health.demo.controller;

import io.github.mfvanek.pg.index.health.demo.utils.BasePgIndexHealthDemoSpringBootTest;
import io.github.mfvanek.pg.settings.PgParam;
import io.github.mfvanek.pg.settings.PgParamImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class DbConfigurationControllerTest extends BasePgIndexHealthDemoSpringBootTest {

    @BeforeEach
    void setUp() {
        setUpBasicAuth();
    }

    @Test
    void getParamsWithDefaultValuesShouldReturnOk() {
        final String url = String.format("http://localhost:%s/db/configuration", port);
        final ResponseEntity<PgParam[]> response = restTemplate.getForEntity(url, PgParam[].class);

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        final PgParam[] responseBody = response.getBody();
        assertThat(responseBody)
                .containsExactly(
                        PgParamImpl.of("maintenance_work_mem", "64MB"),
                        PgParamImpl.of("random_page_cost", "4"),
                        PgParamImpl.of("shared_buffers", "128MB"),
                        PgParamImpl.of("lock_timeout", "0"),
                        PgParamImpl.of("effective_cache_size", "4GB"),
                        PgParamImpl.of("temp_file_limit", "-1"),
                        PgParamImpl.of("statement_timeout", "0"),
                        PgParamImpl.of("log_min_duration_statement", "-1"),
                        PgParamImpl.of("work_mem", "4MB"),
                        PgParamImpl.of("idle_in_transaction_session_timeout", "0"));
    }
}
