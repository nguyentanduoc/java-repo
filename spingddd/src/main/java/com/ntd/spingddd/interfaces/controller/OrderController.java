package com.ntd.spingddd.interfaces.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ntd.spingddd.application.command.order.CreateOrderCommand;
import com.ntd.spingddd.application.service.OrderService;
import com.ntd.spingddd.interfaces.ov.order.OrderRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;

  @PostMapping("/createOrder")
  public String createOrder(@RequestBody OrderRequest orderRequest) {
    // Fake userId
    Long userId = 1L;
    CreateOrderCommand createOrderCommand = CreateOrderCommand.builder()
        .productId(orderRequest.getProductId())
        .quantity(orderRequest.getQuantity())
        .userId(userId)
        .build();
    orderService.createOrder(createOrderCommand);
    return "Order created successfully";
  }

}
