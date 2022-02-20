/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health-spring-boot-demo
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.index.health.demo.config;

import io.github.mfvanek.pg.common.health.DatabaseHealthFactory;
import io.github.mfvanek.pg.common.health.DatabaseHealthFactoryImpl;
import io.github.mfvanek.pg.common.health.logger.HealthLogger;
import io.github.mfvanek.pg.common.maintenance.MaintenanceFactoryImpl;
import io.github.mfvanek.pg.common.management.DatabaseManagement;
import io.github.mfvanek.pg.common.management.DatabaseManagementImpl;
import io.github.mfvanek.pg.connection.ConnectionCredentials;
import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;
import io.github.mfvanek.pg.connection.HighAvailabilityPgConnectionFactory;
import io.github.mfvanek.pg.connection.HighAvailabilityPgConnectionFactoryImpl;
import io.github.mfvanek.pg.connection.PgConnectionFactoryImpl;
import io.github.mfvanek.pg.connection.PrimaryHostDeterminerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.JdbcDatabaseContainer;

import javax.annotation.Nonnull;

@Configuration(proxyBeanMethods = false)
public class DatabaseStructureHealthConfig {

    @Bean
    public ConnectionCredentials connectionCredentials(@Nonnull JdbcDatabaseContainer<?> jdbcDatabaseContainer) {
        return ConnectionCredentials.ofUrl(jdbcDatabaseContainer.getJdbcUrl(),
                jdbcDatabaseContainer.getUsername(), jdbcDatabaseContainer.getPassword());
    }

    @Bean
    public HighAvailabilityPgConnectionFactory highAvailabilityPgConnectionFactory() {
        return new HighAvailabilityPgConnectionFactoryImpl(new PgConnectionFactoryImpl(), new PrimaryHostDeterminerImpl());
    }

    @Bean
    public DatabaseHealthFactory databaseHealthFactory() {
        return new DatabaseHealthFactoryImpl(new MaintenanceFactoryImpl());
    }

    @Bean
    public HealthLogger healthLogger(@Nonnull final ConnectionCredentials connectionCredentials,
                                     @Nonnull final HighAvailabilityPgConnectionFactory highAvailabilityPgConnectionFactory,
                                     @Nonnull final DatabaseHealthFactory databaseHealthFactory) {
        return new CustomHealthLogger(connectionCredentials, highAvailabilityPgConnectionFactory, databaseHealthFactory);
    }

    @Bean
    public HighAvailabilityPgConnection highAvailabilityPgConnection(
            @Nonnull final ConnectionCredentials connectionCredentials,
            @Nonnull final HighAvailabilityPgConnectionFactory highAvailabilityPgConnectionFactory) {
        return highAvailabilityPgConnectionFactory.of(connectionCredentials);
    }

    @Bean
    public DatabaseManagement databaseManagement(@Nonnull final HighAvailabilityPgConnection highAvailabilityPgConnection) {
        return new DatabaseManagementImpl(highAvailabilityPgConnection, new MaintenanceFactoryImpl());
    }
}
