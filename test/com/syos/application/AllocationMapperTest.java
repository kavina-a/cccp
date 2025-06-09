package com.syos.application;

import com.syos.application.dto.AllocatedRestockDTO;
import com.syos.application.mapper.AllocationMapper;
import com.syos.domain.entity.StoreStock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AllocationMapperTest {

    @Test
    void shouldMapStoreStockToDTOCorrectly() {
        StoreStock stock = new StoreStock();
        stock.setItemCode("ITEM123");
        stock.setBatchCode("BATCH01");

        AllocatedRestockDTO dto = AllocationMapper.toDTO(stock, 50);

        assertEquals("ITEM123", dto.getItemCode());
        assertEquals("BATCH01", dto.getBatchCode());
        assertEquals(50, dto.getAllocatedQuantity());
    }

    @Test
    void shouldHandleZeroQuantity() {
        StoreStock stock = new StoreStock();
        stock.setItemCode("ITEM123");
        stock.setBatchCode("BATCH01");

        AllocatedRestockDTO dto = AllocationMapper.toDTO(stock, 0);

        assertEquals(0, dto.getAllocatedQuantity());
    }

    @Test
    void shouldHandleNullFields() {
        StoreStock stock = new StoreStock();
        // Leave itemCode and batchCode null

        AllocatedRestockDTO dto = AllocationMapper.toDTO(stock, 10);

        assertNull(dto.getItemCode());
        assertNull(dto.getBatchCode());
        assertEquals(10, dto.getAllocatedQuantity());
    }
}