package com.syos.domain.command;

import server.application.dto.AllocatedRestockDTO;
import server.application.dto.request.AllocationRequest;
import server.data.dao.interfaces.ShelfDao;
import server.data.dao.interfaces.StoreDao;
import server.domain.command.allocation.AllocateToShelfCommand;
import server.domain.entity.AllocationTarget;
import server.domain.entity.StoreStock;
import server.domain.strategy.stockselection.interfaces.StockSelectionStrategy;
import com.syos.service.testutils.TestData;
import server.utils.SessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.Mockito.*;

public class AllocateToShelfCommandTest {

    private AllocationRequest request;
    private StockSelectionStrategy strategy;
    private StoreDao storeDao;
    private ShelfDao shelfDao;
    private Connection conn;

    private AllocateToShelfCommand command;

    @BeforeEach
    void setUp() {
        request = new AllocationRequest("ITEM1", 15, AllocationTarget.SHELF);
        strategy = mock(StockSelectionStrategy.class);
        storeDao = mock(StoreDao.class);
        shelfDao = mock(ShelfDao.class);
        conn = mock(Connection.class);

        // Mock logged in employee
        SessionManager sessionManager = SessionManager.getInstance();
        sessionManager.setLoggedInEmployee(TestData.employee());

        command = new AllocateToShelfCommand(request, strategy, storeDao, shelfDao);
    }

    @Test
    void shouldAllocateStockToShelfSuccessfully() throws Exception {
        List<StoreStock> mockStocks = List.of(TestData.storeStock("ITEM1", "B1", 10), TestData.storeStock("ITEM1", "B2", 10));
        List<AllocatedRestockDTO> expected = List.of(
                new AllocatedRestockDTO("ITEM1", "B1", 10),
                new AllocatedRestockDTO("ITEM1", "B2", 5)
        );

        when(storeDao.findByItemCode(conn, "ITEM1")).thenReturn(mockStocks);
        when(strategy.allocateStock(mockStocks, 15)).thenReturn(expected);

        List<AllocatedRestockDTO> result = command.execute(conn);

        assertEquals(2, result.size());
        verify(storeDao, times(2)).updateStockAfterAllocation(eq(conn), anyString(), anyString(), anyInt());
        verify(shelfDao, times(2)).logShelfRestockTransaction(eq(conn), any(), any(), anyInt(), eq("SHELF-01"), any());
    }

    @Test
    void shouldThrowIfStrategyThrowsDueToInsufficientStock() throws Exception {
        List<StoreStock> mockStocks = List.of(TestData.storeStock("ITEM1", "B1", 2));
        when(storeDao.findByItemCode(conn, "ITEM1")).thenReturn(mockStocks);
        when(strategy.allocateStock(mockStocks, 15)).thenThrow(new IllegalArgumentException("Insufficient stock"));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> command.execute(conn));
        assertEquals("Insufficient stock", ex.getMessage());
    }

    @Test
    void shouldSkipRestockLogIfShelfIdNotPresent() throws Exception {
        AllocationRequest noShelfRequest = new AllocationRequest("ITEM1", 10, null);
        command = new AllocateToShelfCommand(noShelfRequest, strategy, storeDao, shelfDao);

        List<StoreStock> mockStocks = List.of(TestData.storeStock("ITEM1", "B1", 10));
        List<AllocatedRestockDTO> expected = List.of(new AllocatedRestockDTO("ITEM1", "B1", 10));

        when(storeDao.findByItemCode(conn, "ITEM1")).thenReturn(mockStocks);
        when(strategy.allocateStock(mockStocks, 10)).thenReturn(expected);

        List<AllocatedRestockDTO> result = command.execute(conn);

        verify(storeDao).updateStockAfterAllocation(eq(conn), anyString(), anyString(), anyInt());
        verify(shelfDao, never()).logShelfRestockTransaction(any(), any(), any(), anyInt(), any(), any());
    }

    @Test
    void shouldReturnEmptyListIfStrategyAllocatesNothing() throws Exception {
        when(storeDao.findByItemCode(conn, "ITEM1")).thenReturn(List.of());
        when(strategy.allocateStock(any(), anyInt())).thenReturn(List.of());

        List<AllocatedRestockDTO> result = command.execute(conn);

        assertTrue(result.isEmpty());
        verify(storeDao, never()).updateStockAfterAllocation(any(), any(), any(), anyInt());
    }
}