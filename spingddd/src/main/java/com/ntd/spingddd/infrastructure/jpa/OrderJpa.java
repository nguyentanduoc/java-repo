package com.ntd.spingddd.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ntd.spingddd.infrastructure.repository.order.OrderDO;

public interface OrderJpa extends JpaRepository<OrderDO, Long> {

  @Query("SELECT o FROM orders o WHERE o.token = :token")
  OrderDO findByToken(String token);

}
