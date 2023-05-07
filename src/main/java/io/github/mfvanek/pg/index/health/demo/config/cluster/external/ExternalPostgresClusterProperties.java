/*
 * Copyright (c) 2019-2023. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health-spring-boot-demo
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.index.health.demo.config.cluster.external;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;

@ConfigurationProperties(prefix = "cluster")
@Profile("external-postgres-cluster")
public record ExternalPostgresClusterProperties(
        ClusterProperties primary,
        ClusterProperties secondary
) {

    record ClusterProperties(
            String username,
            String password,
            String connectionString
    ) {

    }

}
