package com.ntd.spingddd.unit.service.order;

import com.ntd.spingddd.application.service.order.OrderConsummer;
import com.ntd.spingddd.domain.model.Order;
import com.ntd.spingddd.domain.repository.InventoryRepository;
import com.ntd.spingddd.domain.repository.OrderRepository;
import com.ntd.spingddd.infrastructure.mq.PlaceOrderMQMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderConsummerTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private OrderConsummer orderConsummer;

    @Test
    void processOrder_orderNotFound() {
        PlaceOrderMQMessage msg = new PlaceOrderMQMessage("token", 1L, 1, 1L, null, System.currentTimeMillis());
        when(orderRepository.findByToken("token")).thenReturn(null);

        orderConsummer.processOrder(msg);

        verify(orderRepository).findByToken("token");
        verifyNoInteractions(inventoryRepository);
    }

    @Test
    void processOrder_alreadyProcessed() {
        PlaceOrderMQMessage msg = new PlaceOrderMQMessage("token", 1L, 1, 1L, null, System.currentTimeMillis());
        Order order = Order.builder().status(1).build();
        when(orderRepository.findByToken("token")).thenReturn(order);

        orderConsummer.processOrder(msg);

        verify(orderRepository).findByToken("token");
        verifyNoInteractions(inventoryRepository);
    }

    @Test
    void processOrder_failToDecreaseInventory() {
        PlaceOrderMQMessage msg = new PlaceOrderMQMessage("token", 1L, 1, 1L, null, System.currentTimeMillis());
        Order order = Order.builder().status(0).build();
        when(orderRepository.findByToken("token")).thenReturn(order);
        when(inventoryRepository.updateAvalibleQuality(1L, 1)).thenReturn(0);

        orderConsummer.processOrder(msg);

        verify(inventoryRepository).updateAvalibleQuality(1L, 1);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void processOrder_statusNull_success() {
        PlaceOrderMQMessage msg = new PlaceOrderMQMessage("token", 1L, 1, 1L, null, System.currentTimeMillis());
        Order order = Order.builder().status(null).build();
        when(orderRepository.findByToken("token")).thenReturn(order);
        when(inventoryRepository.updateAvalibleQuality(1L, 1)).thenReturn(1);

        orderConsummer.processOrder(msg);

        verify(orderRepository).save(order);
        verify(inventoryRepository).updateAvalibleQuality(1L, 1);
    }

    @Test
    void processOrder_success() {
        PlaceOrderMQMessage msg = new PlaceOrderMQMessage("token", 1L, 1, 1L, null, System.currentTimeMillis());
        Order order = Order.builder().status(0).build();
        when(orderRepository.findByToken("token")).thenReturn(order);
        when(inventoryRepository.updateAvalibleQuality(1L, 1)).thenReturn(1);

        orderConsummer.processOrder(msg);

        verify(orderRepository).save(order);
        verify(inventoryRepository).updateAvalibleQuality(1L, 1);
    }
}
