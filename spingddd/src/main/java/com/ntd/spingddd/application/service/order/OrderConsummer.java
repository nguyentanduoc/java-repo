package com.ntd.spingddd.application.service.order;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.ntd.spingddd.domain.model.Inventory;
import com.ntd.spingddd.domain.model.Order;
import com.ntd.spingddd.domain.repository.InventoryRepository;
import com.ntd.spingddd.domain.repository.OrderRepository;
import com.ntd.spingddd.infrastructure.mq.PlaceOrderMQMessage;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderConsummer {

  private final OrderRepository orderRepository;
  private final InventoryRepository inventoryRepository;

  @KafkaListener(topics = "order-place-topic", groupId = "order-consumer-group", concurrency = "10")
  @Transactional(rollbackOn = Exception.class)
  public void processOrder(PlaceOrderMQMessage message) {
    log.info("Consumer received order message: {}", message);

    // 1. Idempotent check: Check if order already exists by token
    Order order = orderRepository.findByToken(message.getToken());
    if (order == null) {
      log.error("Order with token {} not found. This should not happen in the current flow.", message.getToken());
      return;
    }

    if (order.getStatus() != null && order.getStatus() == 1) {
      log.warn("Order with token {} is already processed (status=1), skipping.", message.getToken());
      return;
    }

    // 2. Decrease inventory in DB
    int updatedRows = inventoryRepository.updateAvalibleQuality(message.getProductId(), message.getQuantity());
    if (updatedRows == 0) {
      log.error("Failed to decrease inventory for product {}. Out of stock or product not found.", message.getProductId());
      // Cập nhật trạng thái đơn hàng thành thất bại (ví dụ: status = 2) nếu cần
      return;
    }

    // 3. Update order status to completed (1)
    order.setStatus(1);
    orderRepository.save(order);

    log.info("Successfully processed order for token: {}", message.getToken());
  }
}
