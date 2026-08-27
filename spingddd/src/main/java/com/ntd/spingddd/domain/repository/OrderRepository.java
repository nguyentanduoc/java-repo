package com.ntd.spingddd.domain.repository;

import com.ntd.spingddd.domain.model.Order;

public interface OrderRepository {
  Order save(Order order);
}
