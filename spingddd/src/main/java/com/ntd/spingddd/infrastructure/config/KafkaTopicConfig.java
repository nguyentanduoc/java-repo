package com.ntd.spingddd.infrastructure.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

  public static final String ORDER_PLACE_TOPIC = "order-place-topic";

  @Bean
  NewTopic orderCreatedTopic() {
    return TopicBuilder.name("order-created-topic")
        .partitions(3) // Chia làm 3 phần vùng để xử lý song song tốt hơn
        .replicas(1)
        .build();
  }

}