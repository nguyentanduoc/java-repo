package com.ntd.spingddd.infrastructure.repository.order;

import com.ntd.spingddd.domain.model.Order;

public final class OrderConverter {
  private OrderConverter() {
  }

  public static OrderDO toOrderDO(Order order) {
    return OrderDO.builder()
        .id(order.getId())
        .productId(order.getProductId())
        .quantity(order.getQuantity())
        .amount(order.getAmount())
        .price(order.getPrice())
        .userId(order.getUserId())
        .createdAt(order.getCreatedAt())
        .updatedAt(order.getUpdatedAt())
        .build();
  }

  public static Order toOrder(OrderDO orderDO) {
    return Order.builder()
        .id(orderDO.getId())
        .productId(orderDO.getProductId())
        .quantity(orderDO.getQuantity())
        .amount(orderDO.getAmount())
        .price(orderDO.getPrice())
        .userId(orderDO.getUserId())
        .createdAt(orderDO.getCreatedAt())
        .updatedAt(orderDO.getUpdatedAt())
        .build();
  }
}
