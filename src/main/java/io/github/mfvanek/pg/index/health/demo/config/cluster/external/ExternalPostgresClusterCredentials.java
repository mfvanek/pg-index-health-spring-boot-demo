/*
 * Copyright (c) 2019-2023. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health-spring-boot-demo
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.index.health.demo.config.cluster.external;

import io.github.mfvanek.pg.connection.ConnectionCredentials;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Configuration
@Profile("external-postgres-cluster")
public class ExternalPostgresClusterCredentials {

    private final ExternalPostgresClusterProperties clusterProperties;

    public ExternalPostgresClusterCredentials(final ExternalPostgresClusterProperties clusterProperties) {
        this.clusterProperties = clusterProperties;
    }

    @Bean
    public ConnectionCredentials clusterCredentials() {
        return ConnectionCredentials.of(
                List.of(
                        clusterProperties.primary().connectionString(),
                        clusterProperties.secondary().connectionString()
                ),
                clusterProperties.primary().username(),
                clusterProperties.primary().password()
        );
    }
}
