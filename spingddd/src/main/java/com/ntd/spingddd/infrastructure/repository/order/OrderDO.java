package com.ntd.spingddd.infrastructure.repository.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "orders")
public class OrderDO {

  @Id
  @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
  private Long id;

  @Column
  private Long productId;

  @Column
  private Integer quantity;

  @Column
  private BigDecimal price;

  @Column
  private BigDecimal amount;

  @Column
  private Long userId;

  @CreatedBy
  @Column(updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(updatable = false)
  private LocalDateTime updatedAt;
}
