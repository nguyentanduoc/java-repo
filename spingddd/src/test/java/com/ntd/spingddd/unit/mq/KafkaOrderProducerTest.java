package com.ntd.spingddd.unit.mq;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import com.ntd.spingddd.infrastructure.mq.KafkaOrderProducer;
import com.ntd.spingddd.infrastructure.mq.PlaceOrderMQMessage;
import com.ntd.spingddd.infrastructure.config.KafkaTopicConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import java.util.concurrent.CompletableFuture;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaOrderProducerTest {

    @Mock
    private KafkaTemplate<String, PlaceOrderMQMessage> kafkaTemplate;

    @InjectMocks
    private KafkaOrderProducer producer;

    @Test
    void publish_success() {
        PlaceOrderMQMessage msg = new PlaceOrderMQMessage("token", 1L, 1, 1L, null, System.currentTimeMillis());
        CompletableFuture<SendResult<String, PlaceOrderMQMessage>> future = new CompletableFuture<>();
        when(kafkaTemplate.send(eq(KafkaTopicConfig.ORDER_PLACE_TOPIC), anyString(), eq(msg))).thenReturn(future);

        producer.publish(msg);
        
        SendResult<String, PlaceOrderMQMessage> sendResult = mock(SendResult.class);
        RecordMetadata recordMetadata = new RecordMetadata(new TopicPartition("topic", 0), 0, 0, 0, 0, 0);
        when(sendResult.getRecordMetadata()).thenReturn(recordMetadata);
        
        future.complete(sendResult);
        verify(kafkaTemplate).send(eq(KafkaTopicConfig.ORDER_PLACE_TOPIC), anyString(), eq(msg));
    }

    @Test
    void publish_failure() throws Exception {
        PlaceOrderMQMessage msg = new PlaceOrderMQMessage("token", 1L, 1, 1L, null, System.currentTimeMillis());
        CompletableFuture<SendResult<String, PlaceOrderMQMessage>> future = new CompletableFuture<>();
        when(kafkaTemplate.send(eq(KafkaTopicConfig.ORDER_PLACE_TOPIC), anyString(), eq(msg))).thenReturn(future);

        producer.publish(msg);
        future.completeExceptionally(new RuntimeException("Kafka down"));
        verify(kafkaTemplate).send(eq(KafkaTopicConfig.ORDER_PLACE_TOPIC), anyString(), eq(msg));
    }

    @Test
    void sendAndAwaitAck_success() throws Exception {
        PlaceOrderMQMessage msg = new PlaceOrderMQMessage("token", 1L, 1, 1L, null, System.currentTimeMillis());
        CompletableFuture<SendResult<String, PlaceOrderMQMessage>> future = CompletableFuture.completedFuture(mock(SendResult.class));
        when(kafkaTemplate.send(eq(KafkaTopicConfig.ORDER_PLACE_TOPIC), anyString(), eq(msg))).thenReturn(future);

        producer.sendAndAwaitAck(msg);
        verify(kafkaTemplate).send(eq(KafkaTopicConfig.ORDER_PLACE_TOPIC), anyString(), eq(msg));
    }
}
