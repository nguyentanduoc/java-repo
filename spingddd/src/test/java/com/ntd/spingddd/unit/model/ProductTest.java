package com.ntd.spingddd.unit.model;

import com.ntd.spingddd.domain.model.Product;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ProductTest {

    @Test
    void product_settersGetters() {
        Product p = new Product();
        p.setId(1L);
        p.setName("name");
        p.setPrice(new BigDecimal("100"));
        assertThat(p.getId()).isEqualTo(1L);
        assertThat(p.getName()).isEqualTo("name");
        assertThat(p.getPrice()).isEqualTo(new BigDecimal("100"));
    }
}