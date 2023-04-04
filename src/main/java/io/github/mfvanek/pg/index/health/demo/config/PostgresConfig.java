/*
 * Copyright (c) 2019-2023. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health-spring-boot-demo
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.index.health.demo.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.Map;
import javax.annotation.Nonnull;
import javax.sql.DataSource;

@Configuration(proxyBeanMethods = false)
@Profile("postgres")
public class PostgresConfig {

    @Value("${postgres.username}")
    String username;
    @Value("${postgres.password}")
    String password;
    @Value("${postgres.database}")
    String database;

    @SuppressWarnings({"java:S2095", "java:S1452"})
    @Bean(initMethod = "start", destroyMethod = "stop")
    public JdbcDatabaseContainer<?> jdbcDatabaseContainer() {
        return new PostgreSQLContainer<>("postgres:13.2")
                .withDatabaseName("demo_for_pg_index_health")
                .withUsername("demo_user")
                .withPassword("myUniquePassword")
                .waitingFor(Wait.forListeningPort());
    }

    @Bean
    public DataSource dataSource(@Nonnull final JdbcDatabaseContainer<?> jdbcDatabaseContainer,
                                 @Nonnull final Environment environment) {
        if (environment instanceof ConfigurableEnvironment configurableEnvironment) {
            final MutablePropertySources mps = configurableEnvironment.getPropertySources();
            mps.addFirst(new MapPropertySource("connectionString",
                    Map.ofEntries(Map.entry("spring.datasource.url", jdbcDatabaseContainer.getJdbcUrl()))));
        }
        final HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(jdbcDatabaseContainer.getJdbcUrl());
        hikariConfig.setUsername(jdbcDatabaseContainer.getUsername());
        hikariConfig.setPassword(jdbcDatabaseContainer.getPassword());
        return new HikariDataSource(hikariConfig);
    }
}
