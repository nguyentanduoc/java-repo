package com.ntd.spingddd.unit.cronjob;

import com.ntd.spingddd.application.cronjob.OutboxPublisherJob;
import com.ntd.spingddd.domain.repository.OutboxEventRepository;
import com.ntd.spingddd.infrastructure.mq.KafkaOrderProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxPublisherJobTest {

    @Mock
    private OutboxEventRepository outboxEventRepository;
    @Mock
    private KafkaOrderProducer kafkaOrderProducer;

    @InjectMocks
    private OutboxPublisherJob outboxPublisherJob;

            @Test
    void run_noEvents() throws Exception {
        when(outboxEventRepository.findPending(500)).thenReturn(List.of());
        outboxPublisherJob.publish();
        verify(kafkaOrderProducer, never()).sendAndAwaitAck(any());
    }

    @Test
    void run_withEvents_success_thenMarkPublished() throws Exception {
        var event = new com.ntd.spingddd.infrastructure.repository.outboxEvent.OutboxEventDO();
        event.setId(1L);
        event.setPayload("{\"token\":\"t\",\"productId\":1,\"quantity\":1,\"userId\":1,\"price\":100,\"timestamp\":1}");
        when(outboxEventRepository.findPending(500)).thenReturn(List.of(event));

        outboxPublisherJob.publish();

        verify(kafkaOrderProducer, times(1)).sendAndAwaitAck(any());
        verify(outboxEventRepository, times(1)).markPublished(eq(1L), any());
    }

    @Test
    void run_withEvents_exceptionCaught() throws Exception {
        var event = new com.ntd.spingddd.infrastructure.repository.outboxEvent.OutboxEventDO();
        event.setId(2L);
        event.setPayload("{\"token\":\"t\",\"productId\":1,\"quantity\":1,\"userId\":1,\"price\":100,\"timestamp\":1}");
        when(outboxEventRepository.findPending(500)).thenReturn(List.of(event));
        doThrow(new RuntimeException("Kafka error")).when(kafkaOrderProducer).sendAndAwaitAck(any());

        outboxPublisherJob.publish();

        verify(outboxEventRepository, never()).markPublished(anyLong(), any());
    }
}