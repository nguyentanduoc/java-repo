package com.ntd.spingddd.domain.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutboxEvent {
  private Long id;
  private String aggregateId;
  private String eventType;
  private String payload;
  private Integer status;
  private LocalDateTime createdAt;
  private LocalDateTime publishedAt;
}
