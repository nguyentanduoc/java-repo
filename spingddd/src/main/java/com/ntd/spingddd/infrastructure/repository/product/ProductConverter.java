package com.ntd.spingddd.infrastructure.repository.product;

import com.ntd.spingddd.domain.model.Product;

public final class ProductConverter {

  private ProductConverter() {
  }

  public static ProductDO toProductDO(Product product) {
    return ProductDO.builder()
        .id(product.getId())
        .name(product.getName())
        .price(product.getPrice())
        .build();
  }

  public static Product toProduct(ProductDO productDO) {
    Product prod = new Product();
    prod.setId(productDO.getId());
    prod.setName(productDO.getName());
    prod.setPrice(productDO.getPrice());
    return prod;
  }
}
