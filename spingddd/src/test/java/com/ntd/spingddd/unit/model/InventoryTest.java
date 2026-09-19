package com.ntd.spingddd.unit.model;

import com.ntd.spingddd.application.exception.BadRequestException;
import com.ntd.spingddd.domain.model.Inventory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InventoryTest {

    @Test
    void deductStock_success() {
        Inventory inventory = Inventory.builder().productId(1L).availableQuantity(10).build();
        inventory.deductStock(5);
        assertThat(inventory.getAvailableQuantity()).isEqualTo(5);
    }

    @Test
    void deductStock_throwsException_whenQuantityIsNonPositive() {
        Inventory inventory = Inventory.builder().productId(1L).availableQuantity(10).build();
        assertThatThrownBy(() -> inventory.deductStock(0)).isInstanceOf(BadRequestException.class);
        assertThatThrownBy(() -> inventory.deductStock(-1)).isInstanceOf(BadRequestException.class);
    }

    @Test
    void deductStock_throwsException_whenInsufficientStock() {
        Inventory inventory = Inventory.builder().productId(1L).availableQuantity(5).build();
        assertThatThrownBy(() -> inventory.deductStock(10)).isInstanceOf(BadRequestException.class);
    }

    @Test
    void addStock_success() {
        Inventory inventory = Inventory.builder().productId(1L).availableQuantity(10).build();
        inventory.addStock(5);
        assertThat(inventory.getAvailableQuantity()).isEqualTo(15);
    }

    @Test
    void addStock_throwsException_whenQuantityIsNonPositive() {
        Inventory inventory = Inventory.builder().productId(1L).availableQuantity(10).build();
        assertThatThrownBy(() -> inventory.addStock(0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> inventory.addStock(-1)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void isNotAvalibleStock_returnsCorrectValue() {
        Inventory inventory = Inventory.builder().productId(1L).availableQuantity(5).build();
        assertThat(inventory.isNotAvalibleStock(10)).isTrue();
        assertThat(inventory.isNotAvalibleStock(3)).isFalse();
    }
}