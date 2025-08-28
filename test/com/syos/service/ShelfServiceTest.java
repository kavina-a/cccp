package com.syos.service;

import server.application.dto.LowStockEventDTO;
import server.application.dto.ShelfDTO;
import server.application.dto.request.CreateShelfRequest;
import server.data.dao.interfaces.ShelfDao;
import server.data.dao.interfaces.StoreDao;
import server.domain.entity.ShelfInventory;
import server.domain.entity.StockStatus;
import server.domain.notifications.StockEventPublisher;
import server.domain.service.ShelfService;
import com.syos.service.testutils.TestData;
import server.utils.SessionManager;
import org.hibernate.annotations.processing.SQL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ShelfServiceTest {

    private ShelfDao shelfDao;
    private StoreDao storeDao;
    private StockEventPublisher eventPublisher;
    private ShelfService shelfService;
    private Connection conn;

    @BeforeEach
    void setUp() throws Exception {
        shelfDao = mock(ShelfDao.class);
        storeDao = mock(StoreDao.class);
        eventPublisher = mock(StockEventPublisher.class);
        shelfService = new ShelfService(shelfDao, storeDao, eventPublisher);
        conn = mock(Connection.class);

        SessionManager mockSession = mock(SessionManager.class);
        when(mockSession.getLoggedInEmployee()).thenReturn(TestData.employee());

        injectSessionManager(mockSession);
    }

    private static void injectSessionManager(SessionManager mockInstance) throws Exception {
        Field instanceField = SessionManager.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, mockInstance);
    }

    @Test
    void shouldCreateShelfSuccessfully() throws SQLException {
        CreateShelfRequest request = new CreateShelfRequest("SHELF-01", "ITEM1", 5, "Aisle 1");
        when(shelfDao.exists(any(), eq("SHELF-01"))).thenReturn(false);

        ShelfDTO result = shelfService.createShelf(request);
        assertTrue(result.isSuccess());
        assertEquals("SHELF-01", result.getShelfId());
    }

    @Test
    void shouldRejectDuplicateShelfId() throws SQLException {
        CreateShelfRequest request = new CreateShelfRequest("S1", "ITEM1", 10, "Aisle 1");

        when(shelfDao.exists(any(), eq("Aisle 1"))).thenReturn(true); // Mock as duplicate

        ShelfDTO response = shelfService.createShelf(request);

        assertFalse(response.isSuccess());
        assertEquals("Shelf ID already exists.", response.getMessage());
    }

    @Test
    void shouldDeductShelfStockSuccessfully() throws SQLException {
        Connection mockConnection = mock(Connection.class);

        ShelfInventory batch1 = TestData.shelfInventory("ITEM1", "B1", 5, StockStatus.ACTIVE);
        ShelfInventory batch2 = TestData.shelfInventory("ITEM1", "B2", 10, StockStatus.ACTIVE);

        when(shelfDao.findActiveBatches(mockConnection, "ITEM1")).thenReturn(List.of(batch1, batch2));

        shelfService.deductStock(mockConnection, "ITEM1", 12); // Deducting 12

        verify(shelfDao, times(2)).update(eq(mockConnection), any(ShelfInventory.class));
    }

    @Test
    void shouldThrowIfNotEnoughShelfStock() throws SQLException {
        Connection mockConnection = mock(Connection.class);

        // Only 5 items available
        ShelfInventory batch = TestData.shelfInventory("ITEM1", "B1", 5, StockStatus.ACTIVE);
        when(shelfDao.findActiveBatches(mockConnection, "ITEM1")).thenReturn(List.of(batch));

        // Requesting 10 items (more than available)
        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                shelfService.deductStock(mockConnection, "ITEM1", 10)
        );

        assertEquals("Insufficient shelf stock for product: ITEM1", ex.getMessage());
    }

    @Test
    void shouldTriggerLowStockNotification() throws SQLException {
        LowStockEventDTO dto = new LowStockEventDTO("ITEM1", "ITEM_NAME", 5);
        when(shelfDao.getTriggeredLowStockEvent(conn, "ITEM1")).thenReturn(Optional.of(dto));

        shelfService.evaluateLowStock(conn, "ITEM1");

        verify(eventPublisher).notifyLowStock(Optional.of(dto));
    }

    @Test
    void shouldNotTriggerNotificationIfStockIsSufficient() throws SQLException {
        when(shelfDao.getTriggeredLowStockEvent(conn, "ITEM1")).thenReturn(Optional.empty());

        shelfService.evaluateLowStock(conn, "ITEM1");

        verify(eventPublisher, never()).notifyLowStock(any());
    }
}