package com.syos.service;

import server.application.dto.WebProductDTO;
import server.data.dao.interfaces.WebInventoryDao;
import server.domain.entity.ProductStatus;
import server.domain.entity.StockStatus;
import server.domain.entity.WebInventory;
import server.domain.entity.WebProduct;
import server.domain.service.WebInventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WebInventoryServiceTest {

    private WebInventoryDao webInventoryDao;
    private WebInventoryService service;
    private Connection mockConnection;

    @BeforeEach
    void setUp() {
        webInventoryDao = mock(WebInventoryDao.class);
        service = new WebInventoryService(webInventoryDao);
        mockConnection = mock(Connection.class);
    }

    @Test
    void shouldReturnAvailableProductWhenActive() throws SQLException {
        WebProduct product = new WebProduct();
        product.setItemCode("ITEM01");
        product.setItemName("Test Item");
        product.setPrice(BigDecimal.TEN);
        product.setLastUpdated(LocalDateTime.now());
        product.setStatus(ProductStatus.ACTIVE);

        when(webInventoryDao.findByItemCode(any(), eq("ITEM01"))).thenReturn(Optional.of(product));

        Optional<WebProductDTO> result = service.getAvailableProduct("ITEM01");
        assertTrue(result.isPresent());
        assertEquals("ITEM01", result.get().getItemCode());
    }

    @Test
    void shouldReturnEmptyIfProductInactive() throws SQLException {
        WebProduct product = new WebProduct();
        product.setStatus(ProductStatus.DELETED);
        when(webInventoryDao.findByItemCode(any(), eq("ITEM02"))).thenReturn(Optional.of(product));

        Optional<WebProductDTO> result = service.getAvailableProduct("ITEM02");
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnAllWebItems() throws SQLException {
        WebProduct product1 = new WebProduct("ITEM1", "Item A", BigDecimal.valueOf(100),ProductStatus.ACTIVE, LocalDateTime.now());
        WebProduct product2 = new WebProduct("ITEM2", "Item B", BigDecimal.valueOf(200),ProductStatus.ACTIVE, LocalDateTime.now());
        when(webInventoryDao.findAll(any())).thenReturn(Arrays.asList(product1, product2));

        List<WebProductDTO> result = service.getAllWebItems();
        assertEquals(2, result.size());
    }

    @Test
    void shouldReturnCorrectQuantity() throws SQLException {
        when(webInventoryDao.getAvailableQuantity(any(), eq("ITEMX"))).thenReturn(30);
        int qty = service.getAvailableQuantity("ITEMX");
        assertEquals(30, qty);
    }

    @Test
    void shouldDeductStockFromMultipleBatches() throws SQLException {
        WebInventory b1 = new WebInventory();
        b1.setItemCode("ITEMZ");
        b1.setBatchCode("B1");
        b1.setQuantityRemaining(5);
        b1.setStatus(StockStatus.ACTIVE);

        WebInventory b2 = new WebInventory();
        b2.setItemCode("ITEMZ");
        b2.setBatchCode("B2");
        b2.setQuantityRemaining(10);
        b2.setStatus(StockStatus.ACTIVE);

        when(webInventoryDao.findActiveBatches(any(), eq("ITEMZ"))).thenReturn(Arrays.asList(b1, b2));

        Connection conn = mock(Connection.class);
        service.deductStock(conn, "ITEMZ", 12);

        assertEquals(0, b1.getQuantityRemaining());
        assertEquals(3, b2.getQuantityRemaining());
        verify(webInventoryDao, times(2)).update(any(), any(WebInventory.class));
    }

    @Test
    void shouldHandleExactQuantityMatch() throws SQLException {
        WebInventory b1 = new WebInventory();
        b1.setItemCode("ITEMZ");
        b1.setBatchCode("B1");
        b1.setQuantityRemaining(7);

        when(webInventoryDao.findActiveBatches(any(), eq("ITEMZ"))).thenReturn(List.of(b1));

        Connection conn = mock(Connection.class);
        service.deductStock(conn, "ITEMZ", 7);

        assertEquals(0, b1.getQuantityRemaining());
        verify(webInventoryDao).update(any(), eq(b1));
    }

    @Test
    void shouldDoNothingIfQuantityIsZero() throws SQLException {
        WebInventory b1 = new WebInventory();
        b1.setItemCode("ITEMZ");
        b1.setBatchCode("B1");
        b1.setQuantityRemaining(7);

        when(webInventoryDao.findActiveBatches(any(), eq("ITEMZ"))).thenReturn(List.of(b1));

        Connection conn = mock(Connection.class);
        service.deductStock(conn, "ITEMZ", 0);

        assertEquals(7, b1.getQuantityRemaining());
        verify(webInventoryDao, never()).update(any(), any());
    }
}
