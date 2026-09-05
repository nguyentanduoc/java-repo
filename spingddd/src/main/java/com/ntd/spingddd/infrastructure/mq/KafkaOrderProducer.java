package com.ntd.spingddd.infrastructure.mq;

import java.util.concurrent.TimeUnit;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.ntd.spingddd.infrastructure.config.KafkaTopicConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaOrderProducer {

  private final KafkaTemplate<String, PlaceOrderMQMessage> kafkaTemplate;

  public void publish(PlaceOrderMQMessage domainEvent) {
    // Gửi lên Kafka: Key là orderId giúp các message cùng đơn hàng luôn vào 1
    // Partition
    kafkaTemplate.send(KafkaTopicConfig.ORDER_PLACE_TOPIC, domainEvent.getToken(), domainEvent)
        .whenComplete((result, ex) -> {
          if (ex == null) {
            System.out.println("Gửi Kafka thành công Offset: " + result.getRecordMetadata().offset());
          } else {
            System.err.println("Gửi Kafka thất bại: " + ex.getMessage());
          }
        });
  }

  /**
   * Outbox Publisher — row-by-row mode.
   * Gửi blocking, chờ Broker ACK tối đa 5 giây.
   * Ném exception nếu Kafka fail → caller (OutboxPublisherJob) bắt và skip row
   * đó.
   */
  public void sendAndAwaitAck(PlaceOrderMQMessage message) throws Exception {
    kafkaTemplate.send(KafkaTopicConfig.ORDER_PLACE_TOPIC, message.getToken(), message)
        .get(5, TimeUnit.SECONDS); // chờ Broker ACK, timeout sau 5s
  }
}
