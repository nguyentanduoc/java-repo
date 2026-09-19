package com.ntd.spingddd.unit.repository;

import com.ntd.spingddd.domain.model.OutboxEvent;
import com.ntd.spingddd.infrastructure.repository.outboxEvent.OutboxEventConverter;
import com.ntd.spingddd.infrastructure.repository.outboxEvent.OutboxEventDO;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class OutboxEventConverterTest {

    @Test
        void toOutboxEventDO_nullInput_returnsNull() {
        assertThat(OutboxEventConverter.toOutboxEventDO(null)).isNull();
    }

    @Test
    void toOutboxEventDO_validInput_returnsDo() {
        OutboxEvent event = OutboxEvent.builder()
                .id(1L)
                .aggregateId("100")
                .eventType("ORDER_CREATED")
                .payload("{\"key\":\"value\"}")
                .status(1)
                .createdAt(LocalDateTime.now())
                .publishedAt(null)
                .build();

        OutboxEventDO result = OutboxEventConverter.toOutboxEventDO(event);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getAggregateId()).isEqualTo("100");
    }

    @Test
    void toOutboxEvent_nullInput_returnsNull() {
        assertThat(OutboxEventConverter.toOutboxEvent(null)).isNull();
    }

    @Test
    void toOutboxEvent_validInput_returnsDomain() {
        OutboxEventDO doObj = OutboxEventDO.builder()
                .id(1L)
                .aggregateId("100")
                .eventType("ORDER_CREATED")
                .payload("{\"key\":\"value\"}")
                .status(1)
                .createdAt(LocalDateTime.now())
                .publishedAt(null)
                .build();

        OutboxEvent result = OutboxEventConverter.toOutboxEvent(doObj);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getAggregateId()).isEqualTo("100");
    }
}

