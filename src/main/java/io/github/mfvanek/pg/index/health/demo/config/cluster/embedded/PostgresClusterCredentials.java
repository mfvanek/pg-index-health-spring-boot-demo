/*
 * Copyright (c) 2019-2023. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health-spring-boot-demo
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.index.health.demo.config.cluster.embedded;

import io.github.mfvanek.pg.connection.ConnectionCredentials;
import io.github.mfvanek.pg.testing.PostgreSqlClusterWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Configuration
@Profile("postgres-cluster")
public class PostgresClusterCredentials {

    private final PostgreSqlClusterWrapper clusterWrapper;

    public PostgresClusterCredentials(final PostgreSqlClusterWrapper clusterWrapper) {
        this.clusterWrapper = clusterWrapper;
    }

    @Bean
    public ConnectionCredentials clusterCredentials() {
        return ConnectionCredentials.of(
                List.of(
                        clusterWrapper.getFirstContainerJdbcUrl(),
                        clusterWrapper.getSecondContainerJdbcUrl()
                ),
                clusterWrapper.getUsername(),
                clusterWrapper.getPassword()
        );
    }
}
