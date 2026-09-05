package com.ntd.spingddd.infrastructure.repository.outboxEvent;

import com.ntd.spingddd.domain.model.OutboxEvent;

public final class OutboxEventConverter {

  private OutboxEventConverter() {
    // Private constructor to prevent instantiation
  }

  public static OutboxEventDO toOutboxEventDO(OutboxEvent outboxEvent) {
    if (outboxEvent == null) {
      return null;
    }
    return OutboxEventDO.builder()
        .id(outboxEvent.getId())
        .aggregateId(outboxEvent.getAggregateId())
        .eventType(outboxEvent.getEventType())
        .payload(outboxEvent.getPayload())
        .status(outboxEvent.getStatus())
        .createdAt(outboxEvent.getCreatedAt())
        .publishedAt(outboxEvent.getPublishedAt())
        .build();
  }

  public static OutboxEvent toOutboxEvent(OutboxEventDO outboxEventDO) {
    if (outboxEventDO == null) {
      return null;
    }
    return OutboxEvent.builder()
        .id(outboxEventDO.getId())
        .aggregateId(outboxEventDO.getAggregateId())
        .eventType(outboxEventDO.getEventType())
        .payload(outboxEventDO.getPayload())
        .status(outboxEventDO.getStatus())
        .createdAt(outboxEventDO.getCreatedAt())
        .publishedAt(outboxEventDO.getPublishedAt())
        .build();
  }
}
