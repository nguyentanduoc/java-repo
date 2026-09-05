package com.ntd.spingddd.infrastructure.mq;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class PlaceOrderMQMessage {
  private String token;
  private Long productId;
  private Integer quantity;
  private Long userId;
  private BigDecimal price;
  private long timestamp;
}
