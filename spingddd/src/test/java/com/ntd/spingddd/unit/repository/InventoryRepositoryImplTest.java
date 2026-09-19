package com.ntd.spingddd.unit.repository;

import com.ntd.spingddd.domain.model.Inventory;
import com.ntd.spingddd.infrastructure.jpa.InventoryJpa;
import com.ntd.spingddd.infrastructure.repository.inventory.InventoryDO;
import com.ntd.spingddd.infrastructure.repository.inventory.InventoryRepositotyImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryRepositoryImplTest {

    @Mock
    private InventoryJpa inventoryJpa;

    @InjectMocks
    private InventoryRepositotyImpl inventoryRepository;

    @Test
    void getAvalibleQuality_found() {
        when(inventoryJpa.findByProductId(1L)).thenReturn(Optional.of(InventoryDO.builder().id(1L).productId(1L).availableQuantity(5).build()));
        inventoryRepository.getAvalibleQuality(1L);
        verify(inventoryJpa).findByProductId(1L);
    }

    @Test
    void updateAvalibleQuality_inventoryObj() {
        Inventory inv = Inventory.builder().productId(1L).availableQuantity(3).build();
        inventoryRepository.updateAvalibleQuality(inv);
        verify(inventoryJpa).updateAvalibleQuality(3, 1L);
    }

    @Test
    void updateAvalibleQuality_productIdAndQty() {
        inventoryRepository.updateAvalibleQuality(1L, 4);
        verify(inventoryJpa).updateAvalibleQuality(4, 1L);
    }
}
