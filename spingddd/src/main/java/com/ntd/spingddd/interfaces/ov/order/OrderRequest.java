package com.ntd.spingddd.interfaces.ov.order;

import lombok.Data;

@Data
public class OrderRequest {
  private Long productId;
  private Integer quantity;
}
