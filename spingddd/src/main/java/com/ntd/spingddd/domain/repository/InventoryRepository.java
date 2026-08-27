package com.ntd.spingddd.domain.repository;

import com.ntd.spingddd.domain.model.Inventory;

public interface InventoryRepository {
  Inventory getAvalibleQuality(Long productId);

  int updateAvalibleQuality(Inventory inventory);

  int updateAvalibleQuality(Long productId, Integer quality);
}
