package com.ntd.spingddd.e2e;

import com.ntd.spingddd.application.service.order.OrderConsummer;
import com.ntd.spingddd.domain.model.Order;
import com.ntd.spingddd.domain.repository.OrderRepository;
import com.ntd.spingddd.infrastructure.mq.PlaceOrderMQMessage;
import com.ntd.spingddd.infrastructure.repository.inventory.InventoryDO;
import com.ntd.spingddd.infrastructure.jpa.InventoryJpa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderConsumerIdempotencyTest extends AbstractE2ETest {

    @Autowired private OrderConsummer orderConsummer;
    @Autowired private OrderRepository orderRepository;
    @Autowired private InventoryJpa inventoryJpa;
    @Autowired private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE orders, inventory RESTART IDENTITY");
        inventoryJpa.save(InventoryDO.builder().productId(1L).availableQuantity(10).build());
    }


    @Test
    @DisplayName("Idempotency: Khi consumer nhận trùng message, chỉ trừ tồn kho 1 lần")
    void processOrder_idempotent() {
        String token = "TOKEN-123";
        Order order = Order.create(1L, 2, BigDecimal.TEN, 1L, token);
        order.setStatus(0);
        orderRepository.save(order);

        PlaceOrderMQMessage msg = new PlaceOrderMQMessage(token, 1L, 2, 1L, BigDecimal.TEN, System.currentTimeMillis());

        // Gửi lần 1
        orderConsummer.processOrder(msg);
        // Gửi lần 2 (trùng)
        orderConsummer.processOrder(msg);

        Integer finalQty = inventoryJpa.findByProductId(1L).get().getAvailableQuantity();
        assertThat(finalQty).isEqualTo(8); // Phải là 8, không phải 6
        
        Order savedOrder = orderRepository.findByToken(token);
        assertThat(savedOrder.getStatus()).isEqualTo(1);
    }
}
