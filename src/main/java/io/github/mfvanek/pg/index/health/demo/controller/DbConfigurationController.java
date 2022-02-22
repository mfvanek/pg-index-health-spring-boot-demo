/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health-spring-boot-demo
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.index.health.demo.controller;

import io.github.mfvanek.pg.common.management.DatabaseManagement;
import io.github.mfvanek.pg.model.MemoryUnit;
import io.github.mfvanek.pg.settings.PgParam;
import io.github.mfvanek.pg.settings.ServerSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/db/configuration")
public class DbConfigurationController {

    private final DatabaseManagement databaseManagement;

    @GetMapping
    public ResponseEntity<Set<PgParam>> getParamsWithDefaultValues() {
        final ServerSpecification serverSpecification = ServerSpecification.builder()
                .withCpuCores(Runtime.getRuntime().availableProcessors())
                .withMemoryAmount(8, MemoryUnit.GB)
                .withSSD()
                .build();
        return ResponseEntity.ok(databaseManagement.getParamsWithDefaultValues(serverSpecification));
    }
}
