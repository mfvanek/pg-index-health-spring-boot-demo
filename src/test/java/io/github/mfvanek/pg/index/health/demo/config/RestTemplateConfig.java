/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health-spring-boot-demo
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.index.health.demo.config;


import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

@Configuration(proxyBeanMethods = false)
public class RestTemplateConfig {

//    @Bean
//    public RestTemplateBuilder restTemplateBuilder() {
//        final HttpClient httpClient = HttpClientBuilder.create().disableRedirectHandling().build();
//        final HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
//        return new RestTemplateBuilder((restTemplate -> restTemplate.setRequestFactory(requestFactory)));
//    }
}
