package com.ntd.spingddd.domain.repository;

import com.ntd.spingddd.domain.model.Product;

public interface ProductRepository {

  Product findById(Long productId);
}
