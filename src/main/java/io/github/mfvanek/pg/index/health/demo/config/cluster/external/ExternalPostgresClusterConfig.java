/*
 * Copyright (c) 2019-2023. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health-spring-boot-demo
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.index.health.demo.config.cluster.external;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
@Profile("external-postgres-cluster")
public class ExternalPostgresClusterConfig {

    private final ExternalPostgresClusterProperties clusterProperties;

    public ExternalPostgresClusterConfig(final ExternalPostgresClusterProperties clusterProperties) {
        this.clusterProperties = clusterProperties;
    }

    @Bean
    @Primary
    public DataSource createPrimary() {
        final HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(clusterProperties.primary().connectionString());
        dataSource.setUsername(clusterProperties.primary().username());
        dataSource.setPassword(clusterProperties.primary().password());
        return dataSource;
    }

    @Bean
    public DataSource createSecondary() {
        final HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(clusterProperties.secondary().connectionString());
        dataSource.setUsername(clusterProperties.secondary().username());
        dataSource.setPassword(clusterProperties.secondary().password());
        return dataSource;
    }
}
