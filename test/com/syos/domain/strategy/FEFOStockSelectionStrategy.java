package com.syos.domain.strategy;

import com.syos.application.dto.AllocatedRestockDTO;
import com.syos.domain.entity.StoreStock;
import com.syos.domain.strategy.stockselection.FEFOStockSelectionStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FEFOStockSelectionStrategyTest {

    private FEFOStockSelectionStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new FEFOStockSelectionStrategy();
    }

    @Test
    void shouldAllocateSingleBatchWhenSufficientStockExists() {
        StoreStock batch = new StoreStock();
        batch.setItemCode("ITEM1");
        batch.setBatchCode("BATCH1");
        batch.setCurrentStock(100);
        batch.setExpiryDate(LocalDateTime.now().plusDays(10));

        List<AllocatedRestockDTO> result = strategy.allocateStock(List.of(batch), 50);

        assertEquals(1, result.size());
        assertEquals(50, result.get(0).getAllocatedQuantity());
        assertEquals("BATCH1", result.get(0).getBatchCode());
    }

    @Test
    void shouldAllocateFromMultipleBatchesIfRequired() {
        StoreStock batch1 = new StoreStock();
        batch1.setItemCode("ITEM1");
        batch1.setBatchCode("B1");
        batch1.setCurrentStock(10);
        batch1.setExpiryDate(LocalDateTime.now().plusDays(1));

        StoreStock batch2 = new StoreStock();
        batch2.setItemCode("ITEM1");
        batch2.setBatchCode("B2");
        batch2.setCurrentStock(15);
        batch2.setExpiryDate(LocalDateTime.now().plusDays(3));

        List<AllocatedRestockDTO> result = strategy.allocateStock(List.of(batch2, batch1), 20);

        assertEquals(2, result.size());
        assertEquals("B1", result.get(0).getBatchCode());
        assertEquals(10, result.get(0).getAllocatedQuantity());
        assertEquals("B2", result.get(1).getBatchCode());
        assertEquals(10, result.get(1).getAllocatedQuantity());
    }

    @Test
    void shouldSkipZeroStockBatches() {
        StoreStock batch1 = new StoreStock();
        batch1.setItemCode("ITEM1");
        batch1.setBatchCode("B1");
        batch1.setCurrentStock(0);
        batch1.setExpiryDate(LocalDateTime.now().plusDays(1));

        StoreStock batch2 = new StoreStock();
        batch2.setItemCode("ITEM1");
        batch2.setBatchCode("B2");
        batch2.setCurrentStock(5);
        batch2.setExpiryDate(LocalDateTime.now().plusDays(3));

        List<AllocatedRestockDTO> result = strategy.allocateStock(List.of(batch1, batch2), 3);

        assertEquals(1, result.size());
        assertEquals("B2", result.get(0).getBatchCode());
        assertEquals(3, result.get(0).getAllocatedQuantity());
    }

    @Test
    void shouldThrowIfInsufficientStockAcrossAllBatches() {
        StoreStock batch1 = new StoreStock();
        batch1.setItemCode("ITEM1");
        batch1.setBatchCode("B1");
        batch1.setCurrentStock(2);
        batch1.setExpiryDate(LocalDateTime.now().plusDays(1));

        StoreStock batch2 = new StoreStock();
        batch2.setItemCode("ITEM1");
        batch2.setBatchCode("B2");
        batch2.setCurrentStock(1);
        batch2.setExpiryDate(LocalDateTime.now().plusDays(3));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> strategy.allocateStock(List.of(batch1, batch2), 10));

        assertTrue(ex.getMessage().contains("Insufficient stock"));
    }

    @Test
    void shouldReturnEmptyIfNoBatchesProvided() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> strategy.allocateStock(List.of(), 5));

        assertTrue(ex.getMessage().contains("Insufficient stock"));
    }
}
