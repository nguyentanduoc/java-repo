package com.ntd.spingddd.unit.converter;

import com.ntd.spingddd.domain.model.Inventory;
import com.ntd.spingddd.infrastructure.repository.inventory.InventoryConverter;
import com.ntd.spingddd.infrastructure.repository.inventory.InventoryDO;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InventoryConverterTest {

    @Test
    void toInventory_and_toProductDO() {
        InventoryDO inventoryDO = new InventoryDO();
        inventoryDO.setProductId(1L);
        inventoryDO.setAvailableQuantity(10);
        inventoryDO.setId(1L);

        Inventory inventory = InventoryConverter.toInventory(inventoryDO);
        assertThat(inventory.getProductId()).isEqualTo(1L);

        InventoryDO convertedDO = InventoryConverter.toProductDO(inventory);
        assertThat(convertedDO.getProductId()).isEqualTo(1L);
    }
}