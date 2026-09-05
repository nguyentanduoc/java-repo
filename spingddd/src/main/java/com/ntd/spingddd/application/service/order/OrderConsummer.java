package com.ntd.spingddd.application.service.order;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.ntd.spingddd.infrastructure.mq.PlaceOrderMQMessage;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderConsummer {

  @KafkaListener(topics = "order-place-topic", groupId = "order-consumer-group", concurrency = "10")
  @Transactional(rollbackOn = Exception.class)
  public void processOrder(PlaceOrderMQMessage message) {
  }
}
