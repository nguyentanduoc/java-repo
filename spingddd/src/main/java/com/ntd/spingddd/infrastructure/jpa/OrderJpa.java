package com.ntd.spingddd.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ntd.spingddd.infrastructure.repository.order.OrderDO;

public interface OrderJpa extends JpaRepository<OrderDO, Long> {

}
