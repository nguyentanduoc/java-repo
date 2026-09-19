package com.ntd.spingddd.unit.repository;

import com.ntd.spingddd.domain.model.Order;
import com.ntd.spingddd.infrastructure.jpa.OrderJpa;
import com.ntd.spingddd.infrastructure.repository.order.OrderConverter;
import com.ntd.spingddd.infrastructure.repository.order.OrderDO;
import com.ntd.spingddd.infrastructure.repository.order.OrderRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderRepositoryImplTest {

    @Mock
    private OrderJpa orderJpa;

    @InjectMocks
    private OrderRepositoryImpl orderRepository;

    @Test
    void save_success() {
        Order order = Order.builder()
                .id(1L)
                .token("tok")
                .productId(2L)
                .quantity(3)
                .price(BigDecimal.TEN)
                .amount(BigDecimal.valueOf(30))
                .userId(4L)
                .status(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        OrderDO orderDO = OrderConverter.toOrderDO(order);

        when(orderJpa.save(any())).thenReturn(orderDO);

        Order saved = orderRepository.save(order);
        assertNotNull(saved);
        assertEquals(order.getId(), saved.getId());
    }

    @Test
    void findByToken_found() {
        OrderDO orderDO = OrderDO.builder().id(1L).token("tok").build();
        when(orderJpa.findByToken("tok")).thenReturn(orderDO);

        Order found = orderRepository.findByToken("tok");
        assertNotNull(found);
        assertEquals("tok", found.getToken());
    }

    @Test
    void findByToken_notFound() {
        when(orderJpa.findByToken("tok")).thenReturn(null);

        Order found = orderRepository.findByToken("tok");
        assertNull(found);
    }
}
