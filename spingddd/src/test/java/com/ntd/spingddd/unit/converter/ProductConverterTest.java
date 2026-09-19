package com.ntd.spingddd.unit.converter;

import com.ntd.spingddd.domain.model.Product;
import com.ntd.spingddd.infrastructure.repository.product.ProductConverter;
import com.ntd.spingddd.infrastructure.repository.product.ProductDO;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ProductConverterTest {

    @Test
    void toProduct_and_toProductDO() {
        ProductDO productDO = new ProductDO();
        productDO.setId(1L);
        productDO.setName("name");
        productDO.setPrice(new BigDecimal("100"));

        Product product = ProductConverter.toProduct(productDO);
        assertThat(product.getName()).isEqualTo("name");

        ProductDO convertedDO = ProductConverter.toProductDO(product);
        assertThat(convertedDO.getName()).isEqualTo("name");
    }
}