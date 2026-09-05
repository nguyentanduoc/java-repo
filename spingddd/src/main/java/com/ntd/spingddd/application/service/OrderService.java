package com.ntd.spingddd.application.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;
import com.alibaba.fastjson2.JSON;

import com.ntd.spingddd.application.command.order.CreateOrderCommand;
import com.ntd.spingddd.application.exception.NotfoundException;
import com.ntd.spingddd.domain.model.Inventory;
import com.ntd.spingddd.domain.model.Order;
import com.ntd.spingddd.domain.model.OutboxEvent;
import com.ntd.spingddd.domain.model.Product;
import com.ntd.spingddd.domain.repository.InventoryRepository;
import com.ntd.spingddd.domain.repository.OrderRepository;
import com.ntd.spingddd.domain.repository.OutboxEventRepository;
import com.ntd.spingddd.domain.repository.ProductRepository;
import com.ntd.spingddd.infrastructure.mq.PlaceOrderMQMessage;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

  private final ProductRepository productRepository;
  private final InventoryRepository inventoryRepository;
  private final OrderRepository orderRepository;
  private final StringRedisTemplate redisTemplate;
  private final DefaultRedisScript<Long> inventoryScript;
  private final OutboxEventRepository outboxEventRepository;

  private static final String INVENTORY_KEY_PREFIX = "inventory:product:";

  public boolean deductInventory(CreateOrderCommand createOrderCommand) {
    Long productId = createOrderCommand.productId();
    int quantityToDeduct = createOrderCommand.quantity();
    String key = INVENTORY_KEY_PREFIX + productId;

    // Thực thi Lua script
    Long result = redisTemplate.execute(
        inventoryScript,
        Collections.singletonList(key),
        String.valueOf(quantityToDeduct));

    if (result != null) {
      if (result == 1L) {
        // Trừ thành công trong cache.
        // Lưu ý: Cần gửi message vào Kafka/RabbitMQ ở đây để đồng bộ DB bất đồng bộ.
        return true;
      } else if (result == 0L) {
        // Không đủ hàng
        return false;
      } else if (result == -1L) {
        // Cache miss: Load từ PostgreSQL lên Redis
        return handleCacheMissAndDeduct(productId, quantityToDeduct, key);
      }
    }
    return false;
  }

  @Transactional
  public void createOrder(CreateOrderCommand createOrderCommand) {
    log.info("Create order with productId {} and quantity {}", createOrderCommand.productId(),
        createOrderCommand.quantity());
    // 1.find product by productId
    Product product = productRepository.findById(createOrderCommand.productId());
    if (Objects.isNull(product)) {
      log.warn("Product id {} not found", createOrderCommand.productId());
      throw new NotfoundException("Product id " + createOrderCommand.productId() + " not found");
    }

    // // 2.check product quantity
    // Inventory inventory =
    // inventoryRepository.getAvalibleQuality(createOrderCommand.productId());
    // if (Objects.isNull(inventory)) {
    // log.warn("Inventoty product id {} not found",
    // createOrderCommand.productId());
    // throw new NotfoundException("Inventoty product id " +
    // createOrderCommand.productId() + " not found");
    // }

    // // 3. Decrease inventory
    // inventory.deductStock(createOrderCommand.quantity());
    // inventoryRepository.updateAvalibleQuality(inventory);

    // 2.check product quantity + 3. Decrease inventory
    if (!deductInventory(createOrderCommand)) {
      log.warn("Inventoty product id {} not found",
          createOrderCommand.productId());
    }
    // // 3. Update inventory
    // inventoryRepository.updateAvalibleQuality(createOrderCommand.productId(),
    // createOrderCommand.quantity());
    // // 4.Create order
    String token = "MQ-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);

    Order order = Order.create(createOrderCommand.productId(), createOrderCommand.quantity(), product.getPrice(),
        createOrderCommand.userId(), token);
    orderRepository.save(order);

    PlaceOrderMQMessage mqMessage = new PlaceOrderMQMessage(token, createOrderCommand.productId(),
        createOrderCommand.quantity(), createOrderCommand.userId(), product.getPrice(), System.currentTimeMillis());
    OutboxEvent outboxEvent = new OutboxEvent();
    outboxEvent.setAggregateId(token);
    outboxEvent.setEventType("ORDER_PLACED");
    outboxEvent.setPayload(JSON.toJSONString(mqMessage));
    outboxEvent.setStatus(0);
    outboxEvent.setCreatedAt(LocalDateTime.now());
    outboxEventRepository.save(outboxEvent);

  }

  // @Transactional
  protected boolean handleCacheMissAndDeduct(Long productId, int quantityToDeduct, String key) {
    // Lấy thông tin từ Database
    Inventory inventory = inventoryRepository.getAvalibleQuality(productId);
    if (Objects.isNull(inventory)) {
      log.warn("Inventoty product id {} not found", productId);
      throw new NotfoundException("Inventoty product id " + productId + " not found");
    }

    // Nạp lên Redis (Set giá trị từ DB lên cache)
    redisTemplate.opsForValue().set(key, String.valueOf(inventory.getAvailableQuantity()));

    // Gọi lại Lua script để trừ
    Long retryResult = redisTemplate.execute(
        inventoryScript,
        Collections.singletonList(key),
        String.valueOf(quantityToDeduct));

    return retryResult != null && retryResult == 1L;
  }
}
