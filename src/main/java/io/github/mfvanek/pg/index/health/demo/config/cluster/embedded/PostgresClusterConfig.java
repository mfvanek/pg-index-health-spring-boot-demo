/*
 * Copyright (c) 2019-2023. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health-spring-boot-demo
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.index.health.demo.config.cluster.embedded;

import io.github.mfvanek.pg.testing.PostgreSqlClusterWrapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
@Profile("postgres-cluster")
public class PostgresClusterConfig {

    @Value("${postgresql.username}")
    private String username;
    @Value("${postgresql.password}")
    private String password;

    @Bean
    public PostgreSqlClusterWrapper postgresClusterWrapper() {
        return PostgreSqlClusterWrapper.builder()
                .withUsername(username)
                .withPassword(password)
                .build();
    }

    @Bean
    @Primary
    public DataSource primaryDatasource(final PostgreSqlClusterWrapper clusterWrapper) {
        return clusterWrapper.getDataSourceForPrimary();
    }

    @Bean
    public DataSource standByDatasource(final PostgreSqlClusterWrapper clusterWrapper) {
        return clusterWrapper.getDataSourceForStandBy();
    }
}
