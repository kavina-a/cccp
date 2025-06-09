package com.syos.service;

import com.syos.application.dto.AllocatedRestockDTO;
import com.syos.application.dto.request.AllocationRequest;
import com.syos.data.dao.interfaces.ShelfDao;
import com.syos.data.dao.interfaces.StoreDao;
import com.syos.data.dao.interfaces.WebInventoryDao;
import com.syos.domain.entity.AllocationTarget;
import com.syos.domain.entity.Employee;
import com.syos.domain.entity.StoreStock;
import com.syos.domain.service.AllocationServiceImpl;
import com.syos.domain.strategy.stockselection.interfaces.StockSelectionStrategy;
import com.syos.service.testutils.TestData;
import com.syos.utils.SessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AllocationServiceTest {

    private StockSelectionStrategy strategy;
    private StoreDao storeDao;
    private ShelfDao shelfDao;
    private WebInventoryDao webInventoryDao;
    private AllocationServiceImpl service;

    @BeforeEach
    void setUp() throws Exception {
        strategy = mock(StockSelectionStrategy.class);
        storeDao = mock(StoreDao.class);
        shelfDao = mock(ShelfDao.class);
        webInventoryDao = mock(WebInventoryDao.class);
        service = new AllocationServiceImpl(strategy, storeDao, shelfDao, webInventoryDao);

        SessionManager mockSession = mock(SessionManager.class);
        when(mockSession.getLoggedInEmployee()).thenReturn(TestData.employee());

        injectSessionManager(mockSession);

//        SessionManager mockSession = mock(SessionManager.class);
//        when(mockSession.getLoggedInEmployee()).thenReturn(new Employee("E001"));
//        SessionManager.setInstance(mockSession);
    }

    private static void injectSessionManager(SessionManager mockInstance) throws Exception {
        Field instanceField = SessionManager.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, mockInstance);
    }

    @Test
    void shouldAllocateToShelfSuccessfully() throws SQLException {
        AllocationRequest request = new AllocationRequest("ITEM1", 5, AllocationTarget.SHELF);

        StoreStock stock = TestData.storeStock("ITEM1", "B123", 10);
        AllocatedRestockDTO dto = new AllocatedRestockDTO("ITEM1", "B123", 5);

        when(storeDao.findByItemCode(any(), eq("ITEM1"))).thenReturn(List.of(stock));
        when(strategy.allocateStock(any(), eq(5))).thenReturn(List.of(dto));

        List<AllocatedRestockDTO> result = service.allocateToShelf(request);

        assertEquals(1, result.size());
        assertEquals("ITEM1", result.get(0).getItemCode());

        verify(storeDao).updateStockAfterAllocation(any(), eq("ITEM1"), eq("B123"), eq(5));
        verify(shelfDao).logShelfRestockTransaction(any(), eq("ITEM1"), eq("B123"), eq(5), eq("S001"), eq("E001"));
    }

    @Test
    void shouldThrowIfInsufficientStockShelf() throws SQLException {
        AllocationRequest request = new AllocationRequest("ITEM1", 100, AllocationTarget.SHELF);
        StoreStock stock = TestData.storeStock("ITEM1", "B123", 10);

        when(storeDao.findByItemCode(any(), eq("ITEM1"))).thenReturn(List.of(stock));
        when(strategy.allocateStock(any(), eq(100))).thenThrow(new IllegalArgumentException("Insufficient stock"));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.allocateToShelf(request));
        assertEquals("Insufficient stock", ex.getMessage());
    }

    @Test
    void shouldAllocateToWebSuccessfully() throws SQLException {
        AllocationRequest request = new AllocationRequest("ITEM1", 10, null);
        StoreStock stock = TestData.storeStock("ITEM1", "B123", 10);
        AllocatedRestockDTO dto = new AllocatedRestockDTO("ITEM1", "B124", 10);

        when(storeDao.findByItemCode(any(), eq("ITEM1"))).thenReturn(List.of(stock));
        when(strategy.allocateStock(any(), eq(10))).thenReturn(List.of(dto));

        List<AllocatedRestockDTO> result = service.allocateToWeb(request);
        assertEquals(1, result.size());
        assertEquals("B124", result.get(0).getBatchCode());

        verify(storeDao).updateStockAfterAllocation(any(), eq("ITEM1"), eq("B124"), eq(10));
        verify(webInventoryDao).logWebRestockTransaction(any(), eq("ITEM1"), eq("B124"), eq(10));
    }

    @Test
    void shouldThrowIfInsufficientStockWeb() throws SQLException {
        AllocationRequest request = new AllocationRequest("ITEM1", 50, null);
        StoreStock stock = TestData.storeStock("ITEM1", "B123", 10);

        when(storeDao.findByItemCode(any(), eq("ITEM1"))).thenReturn(List.of(stock));
        when(strategy.allocateStock(any(), eq(50))).thenThrow(new IllegalArgumentException("Insufficient stock"));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.allocateToWeb(request));
        assertEquals("Insufficient stock", ex.getMessage());
    }
}
