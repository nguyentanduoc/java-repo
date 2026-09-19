package com.ntd.spingddd.e2e;

import com.ntd.spingddd.infrastructure.mq.PlaceOrderMQMessage;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@TestConfiguration
public class TestKafkaConfig {

    @Bean
    public org.springframework.kafka.core.ProducerFactory<String, Object> producerFactory() {
        return new org.springframework.kafka.core.DefaultKafkaProducerFactory<>(
            org.springframework.kafka.test.utils.KafkaTestUtils.producerProps(
                "dummy-bootstrap-server"
            )
        );
    }

    @Bean
    public KafkaTemplate<String, PlaceOrderMQMessage> kafkaTemplate(
            org.springframework.kafka.core.ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate(producerFactory);
    }
}
