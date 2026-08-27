package com.ntd.spingddd.infrastructure.jpa;

import java.util.Optional;

import com.ntd.spingddd.infrastructure.repository.product.ProductDO;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductJpa extends JpaRepository<ProductDO, Long> {

  Optional<ProductDO> findById(Long productId);
}
