package com.ntd.spingddd.infrastructure.repository.inventory;

import com.ntd.spingddd.domain.model.Inventory;

public final class InventoryConverter {

  private InventoryConverter() {
  }

  public static InventoryDO toProductDO(Inventory inventory) {
    return InventoryDO.builder()
        .id(inventory.getId())
        .productId(inventory.getProductId())
        .availableQuantity(inventory.getAvailableQuantity())
        .build();
  }

  public static Inventory toInventory(InventoryDO inventoryDO) {
    return Inventory.builder()
        .id(inventoryDO.getId())
        .productId(inventoryDO.getProductId())
        .availableQuantity(inventoryDO.getAvailableQuantity())
        .build();
  }
}
