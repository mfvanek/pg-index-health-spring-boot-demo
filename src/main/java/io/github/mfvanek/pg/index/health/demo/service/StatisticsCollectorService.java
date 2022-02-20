/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health-spring-boot-demo
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.index.health.demo.service;

import io.github.mfvanek.pg.common.management.DatabaseManagement;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class StatisticsCollectorService {

    private final JdbcTemplate jdbcTemplate;
    private final DatabaseManagement databaseManagement;

    @SneakyThrows
    private void waitForStatisticsCollector() {
        jdbcTemplate.execute("vacuum analyze;");
        TimeUnit.MILLISECONDS.sleep(1_000L);
    }

    @Nonnull
    public OffsetDateTime getLastStatsResetTimestamp() {
        return databaseManagement.getLastStatsResetTimestamp().orElse(OffsetDateTime.MIN);
    }

    public void resetStatisticsNoWait() {
        databaseManagement.resetStatistics();
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public OffsetDateTime resetStatistics() {
        databaseManagement.resetStatistics();
        waitForStatisticsCollector();
        return getLastStatsResetTimestamp();
    }
}
