/*
 * Copyright (c) 2019-2023. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health-spring-boot-demo
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.index.health.demo.config;

import io.github.mfvanek.pg.connection.ConnectionCredentials;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.JdbcDatabaseContainer;

import javax.annotation.Nonnull;

@Configuration
@Profile("postgres")
public class PostgresCredentials {
    @Bean
    public ConnectionCredentials connectionCredentials(@Nonnull final JdbcDatabaseContainer<?> jdbcDatabaseContainer) {
        return ConnectionCredentials.ofUrl(jdbcDatabaseContainer.getJdbcUrl(),
                jdbcDatabaseContainer.getUsername(), jdbcDatabaseContainer.getPassword());
    }
}
