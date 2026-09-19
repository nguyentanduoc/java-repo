package com.ntd.spingddd.unit.model;

import com.ntd.spingddd.domain.model.Order;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTest {

    @Test
    void create_andCalculateAmount() {
        Order order = Order.create(1L, 2, new BigDecimal("100.0"), 100L, "token");
        assertThat(order.getToken()).isEqualTo("token");
        assertThat(order.getProductId()).isEqualTo(1L);
        assertThat(order.getAmount()).isEqualTo(new BigDecimal("200.0"));
        assertThat(order.getStatus()).isEqualTo(0);
    }

    @Test
    void calculateAmount_withNullPrice() {
        Order order = Order.builder().quantity(2).price(null).build();
        order.calculateAmount();
        assertThat(order.getAmount()).isNull();
    }
}