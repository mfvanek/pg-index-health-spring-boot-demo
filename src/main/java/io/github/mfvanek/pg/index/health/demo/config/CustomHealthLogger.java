/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health-spring-boot-demo
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.index.health.demo.config;

import io.github.mfvanek.pg.common.health.DatabaseHealthFactory;
import io.github.mfvanek.pg.common.health.logger.AbstractHealthLogger;
import io.github.mfvanek.pg.common.health.logger.LoggingKey;
import io.github.mfvanek.pg.connection.ConnectionCredentials;
import io.github.mfvanek.pg.connection.HighAvailabilityPgConnectionFactory;

import javax.annotation.Nonnull;

public class CustomHealthLogger extends AbstractHealthLogger {

    public CustomHealthLogger(@Nonnull final ConnectionCredentials credentials,
                              @Nonnull final HighAvailabilityPgConnectionFactory connectionFactory,
                              @Nonnull final DatabaseHealthFactory databaseHealthFactory) {
        super(credentials, connectionFactory, databaseHealthFactory);
    }

    @Override
    protected String writeToLog(@Nonnull final LoggingKey key, final int value) {
        return key.getSubKeyName() + ":" + value;
    }
}
