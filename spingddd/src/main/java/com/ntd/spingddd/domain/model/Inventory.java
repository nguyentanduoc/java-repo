package com.ntd.spingddd.domain.model;

import com.ntd.spingddd.application.exception.BadRequestException;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Inventory {
  private Long id;
  private Long productId;
  private Integer availableQuantity;

  public void deductStock(Integer quantity) {
    if (quantity <= 0) {
      throw new BadRequestException("Số lượng trừ phải lớn hơn 0");
    }
    if (this.availableQuantity < quantity) {
      throw new BadRequestException("Không đủ số lượng tồn kho cho sản phẩm: " + productId);
    }
    this.availableQuantity -= quantity;
  }

  // Nghiệp vụ: Thêm tồn kho
  public void addStock(Integer quantity) {
    if (quantity <= 0) {
      throw new IllegalArgumentException("Số lượng thêm phải lớn hơn 0");
    }
    this.availableQuantity += quantity;
  }

  public boolean isNotAvalibleStock(Integer quantity) {
    return this.availableQuantity < quantity;
  }
}
