/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health-spring-boot-demo
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.index.health.demo;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ApplicationStartTest {

    @Test
    void applicationShouldStart() {
        Assertions.assertThatCode(() -> PgIndexHealthSpringBootDemoApplication.main(new String[]{}))
                .doesNotThrowAnyException();
    }
}
