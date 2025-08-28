package com.syos.service;

import server.application.dto.StoreStockDTO;
import server.application.dto.request.CreateStockRequest;
import server.data.dao.ProductDaoImpl;
import server.data.dao.StoreDaoImpl;
import server.domain.entity.Product;
import server.domain.entity.StoreStock;
import server.domain.service.StoreService;
import com.syos.service.testutils.TestData;
import server.utils.SessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StoreServiceTest {

    private StoreDaoImpl storeDao;
    private ProductDaoImpl productDao;
    private StoreService storeService;

    @BeforeEach
    void setUp() throws Exception {
        storeDao = mock(StoreDaoImpl.class);
        productDao = mock(ProductDaoImpl.class);
        storeService = new StoreService(productDao, storeDao);

        SessionManager mockSession = mock(SessionManager.class);
        when(mockSession.getLoggedInEmployee()).thenReturn(TestData.employee());

        injectSessionManager(mockSession);
    }

    //Utility method to inject the mock SessionManager into the static SessionManager class
    private static void injectSessionManager(SessionManager mockInstance) throws Exception {
        Field instanceField = SessionManager.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, mockInstance);
    }

    @Test
    void shouldThrowWhenItemCodeIsNullOrBlank() {
        CreateStockRequest request = new CreateStockRequest(null, "B123", 10, LocalDateTime.now().plusDays(5));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> storeService.insertStoreStock(request));
        assertEquals("Item Code is required.", ex.getMessage());
    }

    @Test
    void shouldThrowWhenBatchCodeIsNullOrBlank() {
        CreateStockRequest request = new CreateStockRequest("ITEM1", "", 10, LocalDateTime.now().plusDays(5));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> storeService.insertStoreStock(request));
        assertEquals("Batch Code is required.", ex.getMessage());
    }

    @Test
    void shouldThrowWhenBatchQuantityIsZeroOrNegative() {
        CreateStockRequest request = new CreateStockRequest("ITEM1", "B123", 0, LocalDateTime.now().plusDays(5));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> storeService.insertStoreStock(request));
        assertEquals("Quantity must be greater than zero.", ex.getMessage());
    }

    @Test
    void shouldThrowWhenExpiryDateIsNull() {
        CreateStockRequest request = new CreateStockRequest("ITEM1", "B123", 10, null);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> storeService.insertStoreStock(request));
        assertEquals("Expiry date must be a future date.", ex.getMessage());
    }

    @Test
    void shouldThrowWhenExpiryDateIsInPast() {
        CreateStockRequest request = new CreateStockRequest("ITEM1", "B123", 10, LocalDateTime.now().minusDays(1));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> storeService.insertStoreStock(request));
        assertEquals("Expiry date must be a future date.", ex.getMessage());
    }

    @Test
    void shouldThrowIfProductDoesNotExist() throws SQLException{
        CreateStockRequest request = new CreateStockRequest("ITEM404", "B123", 10, LocalDateTime.now().plusDays(5));
        when(productDao.findByItemCode(any(), eq("ITEM404"))).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> storeService.insertStoreStock(request));
        assertEquals("Product not found: ITEM404", ex.getMessage());
    }

    @Test
    void shouldThrowIfBatchCodeAlreadyExists() throws SQLException{
        CreateStockRequest request = new CreateStockRequest("ITEM1", "B123", 10, LocalDateTime.now().plusDays(5));
        when(productDao.findByItemCode(any(), eq("ITEM1"))).thenReturn(Optional.of(new Product()));
        when(storeDao.findByItemCodeAndBatchCode(any(), eq("ITEM1"), eq("B123"))).thenReturn(Optional.of(new StoreStock()));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> storeService.insertStoreStock(request));
        assertEquals("Batch already exists: B123", ex.getMessage());
    }

    @Test
    void shouldInsertStoreStockSuccessfully() throws SQLException {
        CreateStockRequest request = new CreateStockRequest("ITEM1", "B123", 10, LocalDateTime.now().plusDays(5));
        when(productDao.findByItemCode(any(), eq("ITEM1"))).thenReturn(Optional.of(new Product()));
        when(storeDao.findByItemCodeAndBatchCode(any(), eq("ITEM1"), eq("B123"))).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> storeService.insertStoreStock(request));
        verify(storeDao).save(any(), any(StoreStock.class));
    }

    @Test
    void shouldReturnEmptyListIfNoStockFound() throws SQLException {
        when(storeDao.findByItemCode(any(), eq("ITEMX"))).thenReturn(Collections.emptyList());
        List<StoreStockDTO> result = storeService.getStoreStockDetails("ITEMX");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnCorrectStockDetailsWhenStockExists() throws SQLException {
        StoreStock stock = new StoreStock();
        stock.setItemCode("ITEM1");
        stock.setBatchCode("B123");
        stock.setInitialStock(100);
        stock.setCurrentStock(80);
        stock.setExpiryDate(LocalDateTime.now().plusMonths(3));
        stock.setReceivedDate(LocalDateTime.now().minusDays(2));

        when(storeDao.findByItemCode(any(), eq("ITEM1"))).thenReturn(List.of(stock));
        List<StoreStockDTO> result = storeService.getStoreStockDetails("ITEM1");

        assertEquals(1, result.size());
        StoreStockDTO dto = result.get(0);
        assertEquals("ITEM1", dto.getItemCode());
        assertEquals("B123", dto.getBatchCode());
        assertEquals(100, dto.getInitialStock());
        assertEquals(80, dto.getCurrentStock());
    }


}
