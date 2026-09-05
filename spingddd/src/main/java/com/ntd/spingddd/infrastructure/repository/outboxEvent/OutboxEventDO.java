package com.ntd.spingddd.infrastructure.repository.outboxEvent;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "outbox_event")
public class OutboxEventDO {
  @Id
  @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
  private Long id;

  @Column
  private String aggregateId;

  @Column
  private String eventType;

  @Column
  private String payload;

  @Column
  private Integer status;

  @CreatedBy
  @Column(updatable = false)
  private LocalDateTime createdAt;

  @Column
  private LocalDateTime publishedAt;
}
