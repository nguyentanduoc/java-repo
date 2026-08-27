package com.ntd.spingddd.application.command.order;

import lombok.Builder;

@Builder
public record CreateOrderCommand(Long productId, Integer quantity, Long userId) {
}
