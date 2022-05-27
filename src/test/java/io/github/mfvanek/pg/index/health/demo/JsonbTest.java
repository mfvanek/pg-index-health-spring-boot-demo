/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health-spring-boot-demo
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.index.health.demo;

import io.github.mfvanek.pg.index.health.demo.utils.BasePgIndexHealthDemoSpringBootTest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.postgresql.util.PGobject;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class JsonbTest extends BasePgIndexHealthDemoSpringBootTest {

    @Test
    void readingAndWritingJsonb() {
        final List<Payment> payments = jdbcTemplate.query("select * from demo.payment order by id limit 10", (rs, rowNum) ->
                Payment.builder()
                        .id(rs.getLong("id"))
                        .orderId(rs.getLong("order_id"))
                        .status(rs.getInt("status"))
                        .createdAt(rs.getObject("created_at", LocalDateTime.class))
                        .paymentTotal(rs.getBigDecimal("payment_total"))
                        .info(rs.getString("info"))
                        .build());
        payments.forEach(p -> assertThat(p.getInfo())
                .isNotBlank()
                .isEqualTo("{\" payment\": {\"date\": \"2022-05-27T18:31:42\", \"result\": \"success\"}}"));
        payments.forEach(p -> {
            final String withoutWhitespaces = StringUtils.deleteWhitespace(p.getInfo());
            assertThat(withoutWhitespaces).isEqualTo("{\"payment\":{\"date\":\"2022-05-27T18:31:42\",\"result\":\"success\"}}");
            final PGobject fixedInfoObject = new PGobject();
            try {
                fixedInfoObject.setType("jsonb");
                fixedInfoObject.setValue(withoutWhitespaces);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            final int count = jdbcTemplate.update("update demo.payment set info = ?::jsonb where id = ?::bigint", fixedInfoObject, p.getId());
            assertThat(count).isEqualTo(1);
        });
    }

    @Getter
    @RequiredArgsConstructor
    @ToString
    @SuperBuilder
    static class Payment {
        private final long id;
        private final long orderId;
        private final int status;
        private final LocalDateTime createdAt;
        private final BigDecimal paymentTotal;
        private final String info;
    }
}
