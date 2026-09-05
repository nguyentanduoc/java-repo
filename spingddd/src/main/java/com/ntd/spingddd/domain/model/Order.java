package com.ntd.spingddd.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
  private Long id;
  private Long productId;
  private Integer quantity;
  private BigDecimal price;
  private BigDecimal amount;
  private Long userId;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private Integer status;
  private String token;

  // Factory method
  public static Order create(Long productId, Integer quantity, BigDecimal price, Long userId, String token) {
    Order order = new Order();
    order.productId = productId;
    order.quantity = quantity;
    order.price = price;
    order.userId = userId;
    // createdAt and updatedAt will be automatically handled by Hibernate
    // timestamps,
    // but you can initialize them here for immediate use before saving.
    order.status = 0;
    order.createdAt = LocalDateTime.now();
    order.updatedAt = LocalDateTime.now();
    order.token = token;
    order.calculateAmount();
    return order;
  }

  public void calculateAmount() {
    if (this.price != null && this.quantity != null) {
      this.amount = this.price.multiply(BigDecimal.valueOf(this.quantity));
    }
  }
}
