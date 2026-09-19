package com.ntd.spingddd.infrastructure.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ntd.spingddd.infrastructure.repository.inventory.InventoryDO;

public interface InventoryJpa extends JpaRepository<InventoryDO, Long> {

  Optional<InventoryDO> findByProductId(Long productId);

  @Modifying
  @Query("UPDATE inventory i SET i.availableQuantity = i.availableQuantity - :quantity, i.version = i.version + 1 WHERE i.productId = :productId AND i.availableQuantity >= :quantity")
  int updateAvalibleQuality(@Param("quantity") Integer quantity, @Param("productId") Long productId);
}
