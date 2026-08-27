package com.ntd.spingddd.infrastructure.repository.inventory;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.ntd.spingddd.domain.model.Inventory;
import com.ntd.spingddd.domain.repository.InventoryRepository;
import com.ntd.spingddd.infrastructure.jpa.InventoryJpa;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InventoryRepositotyImpl implements InventoryRepository {

  private final InventoryJpa inventoryJpa;

  @Override
  public Inventory getAvalibleQuality(Long productId) {
    Optional<InventoryDO> inventory = inventoryJpa.findByProductId(productId);
    return inventory.map(InventoryConverter::toInventory).orElse(null);
  }

  @Override
  public int updateAvalibleQuality(Inventory inventory) {
    return inventoryJpa.updateAvalibleQuality(inventory.getAvailableQuantity(), inventory.getProductId());
  }

  @Override
  public int updateAvalibleQuality(Long productId, Integer quality) {
    return inventoryJpa.updateAvalibleQuality(quality, productId);
  }

}
