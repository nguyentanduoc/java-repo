package com.ntd.spingddd.unit.service;

import com.ntd.spingddd.application.command.order.CreateOrderCommand;
import com.ntd.spingddd.application.exception.BadRequestException;
import com.ntd.spingddd.application.exception.NotfoundException;
import com.ntd.spingddd.application.service.OrderService;
import com.ntd.spingddd.domain.model.Inventory;
import com.ntd.spingddd.domain.model.Order;
import com.ntd.spingddd.domain.model.Product;
import com.ntd.spingddd.domain.repository.InventoryRepository;
import com.ntd.spingddd.domain.repository.OrderRepository;
import com.ntd.spingddd.domain.repository.ProductRepository;
import com.ntd.spingddd.domain.repository.OutboxEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.ValueOperations;


import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private OutboxEventRepository outboxEventRepository;
    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private StringRedisTemplate redisTemplate;
    @Mock
    private DefaultRedisScript<Long> inventoryScript;

    @InjectMocks
    private OrderService orderService;

    private CreateOrderCommand command;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @BeforeEach
    void setUp() {
        command = CreateOrderCommand.builder()
            .productId(1L)
            .quantity(1)
            .userId(1L)
            .build();
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void createOrder_productNotFound_throwsException() {
        when(productRepository.findById(1L)).thenReturn(null);
        assertThatThrownBy(() -> orderService.createOrder(command))
            .isInstanceOf(NotfoundException.class);
    }

    @Test
    void createOrder_insufficientStock_throwsException() {
        Product product = new Product();
        product.setId(1L);
        product.setPrice(new BigDecimal("100"));
        when(productRepository.findById(1L)).thenReturn(product);
        when(redisTemplate.execute(any(), any(), any())).thenReturn(0L);

        assertThatThrownBy(() -> orderService.createOrder(command))
            .isInstanceOf(BadRequestException.class);
    }

    @Test
    void createOrder_success() {
        Product product = new Product();
        product.setId(1L);
        product.setPrice(new BigDecimal("100"));
        when(productRepository.findById(1L)).thenReturn(product);
        when(redisTemplate.execute(any(), any(), any())).thenReturn(1L);

        orderService.createOrder(command);

        verify(orderRepository, times(1)).save(any(Order.class));
        verify(outboxEventRepository, times(1)).save(any());
    }

        @Test
    void deductInventory_cacheMiss_success() {
        command = CreateOrderCommand.builder()
            .productId(1L)
            .quantity(2)
            .userId(1L)
            .build();

        when(redisTemplate.execute(any(), any(), any()))
            .thenReturn(-1L, 1L);
        when(inventoryRepository.getAvalibleQuality(1L)).thenReturn(
            Inventory.builder()
                .productId(1L)
                .availableQuantity(10)
                .build());

        boolean result = orderService.deductInventory(command);
        assertThat(result).isTrue();
        verify(redisTemplate, times(2)).execute(any(), any(), any());
    }

    @Test
    void deductInventory_cacheMiss_inventoryNotFound_throwsException() {
        command = CreateOrderCommand.builder()
            .productId(1L)
            .quantity(2)
            .userId(1L)
            .build();

        when(redisTemplate.execute(any(), any(), any())).thenReturn(-1L);
        when(inventoryRepository.getAvalibleQuality(1L)).thenReturn(null);

        assertThatThrownBy(() -> orderService.deductInventory(command))
            .isInstanceOf(NotfoundException.class);
    }

    @Test
    void deductInventory_cacheMiss_retryFail_returnsFalse() {
        command = CreateOrderCommand.builder()
            .productId(1L)
            .quantity(2)
            .userId(1L)
            .build();

        when(redisTemplate.execute(any(), any(), any()))
            .thenReturn(-1L, 0L);
        when(inventoryRepository.getAvalibleQuality(1L)).thenReturn(
            Inventory.builder()
                .productId(1L)
                .availableQuantity(10)
                .build());

        boolean result = orderService.deductInventory(command);
        assertThat(result).isFalse();
    }

    @Test
    void deductInventory_cacheMiss_retryNull_returnsFalse() {
        command = CreateOrderCommand.builder()
            .productId(1L)
            .quantity(2)
            .userId(1L)
            .build();

        when(redisTemplate.execute(any(), any(), any()))
            .thenReturn(-1L, null);
        when(inventoryRepository.getAvalibleQuality(1L)).thenReturn(
            Inventory.builder()
                .productId(1L)
                .availableQuantity(10)
                .build());

        boolean result = orderService.deductInventory(command);
        assertThat(result).isFalse();
    }

    @Test
    void deductInventory_resultNull_returnsFalse() {
        when(redisTemplate.execute(any(), any(), any())).thenReturn(null);

        boolean result = orderService.deductInventory(command);
        assertThat(result).isFalse();
    }

}