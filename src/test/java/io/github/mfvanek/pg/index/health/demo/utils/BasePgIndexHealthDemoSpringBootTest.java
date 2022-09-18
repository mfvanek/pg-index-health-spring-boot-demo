/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health-spring-boot-demo
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.index.health.demo.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalManagementPort;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Clock;
import java.util.List;
import javax.annotation.Nonnull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class BasePgIndexHealthDemoSpringBootTest {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @LocalServerPort
    protected int port;

    @LocalManagementPort
    protected int actuatorPort;

    @Autowired
    protected Clock clock;

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    protected WebTestClient webTestClient;

    @Autowired
    private SecurityProperties securityProperties;

    protected void setUpBasicAuth() {
        final List<ClientHttpRequestInterceptor> interceptors = restTemplate.getRestTemplate().getInterceptors();
        interceptors.add(new BasicAuthenticationInterceptor(
                securityProperties.getUser().getName(), securityProperties.getUser().getPassword()));
    }

    protected final void setUpBasicAuth(@Nonnull final HttpHeaders httpHeaders) {
        httpHeaders.setBasicAuth(securityProperties.getUser().getName(), securityProperties.getUser().getPassword());
    }
}
