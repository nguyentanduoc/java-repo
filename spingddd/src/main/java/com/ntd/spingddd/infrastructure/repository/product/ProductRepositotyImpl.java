package com.ntd.spingddd.infrastructure.repository.product;

import java.util.Optional;

import com.ntd.spingddd.domain.model.Product;
import com.ntd.spingddd.domain.repository.ProductRepository;
import com.ntd.spingddd.infrastructure.jpa.ProductJpa;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductRepositotyImpl implements ProductRepository {

  private final ProductJpa productJpa;

  @Override
  @Cacheable(value = "products", key = "#p0")
  public Product findById(Long productId) {
    Optional<ProductDO> productDO = productJpa.findById(productId);
    return productDO.map(ProductConverter::toProduct).orElse(null);
  }

}
