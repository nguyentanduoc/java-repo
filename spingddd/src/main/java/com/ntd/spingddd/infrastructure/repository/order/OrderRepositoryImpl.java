package com.ntd.spingddd.infrastructure.repository.order;

import org.springframework.stereotype.Component;

import com.ntd.spingddd.domain.model.Order;
import com.ntd.spingddd.domain.repository.OrderRepository;
import com.ntd.spingddd.infrastructure.jpa.OrderJpa;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

  private final OrderJpa orderJpa;

  @Override
  public Order save(Order order) {
    OrderDO orderDO = orderJpa.save(OrderConverter.toOrderDO(order));
    return OrderConverter.toOrder(orderDO);
  }

}
