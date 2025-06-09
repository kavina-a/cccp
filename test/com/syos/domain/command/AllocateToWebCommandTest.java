//package com.syos.domain.command;
//
//import com.syos.application.dto.AllocatedRestockDTO;
//import com.syos.application.dto.request.AllocationRequest;
//import com.syos.data.dao.interfaces.StoreDao;
//import com.syos.data.dao.interfaces.WebInventoryDao;
//import com.syos.domain.command.allocation.AllocateToWebCommand;
//import com.syos.domain.entity.StoreStock;
//import com.syos.domain.strategy.stockselection.interfaces.StockSelectionStrategy;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.sql.Connection;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class AllocateToWebCommandTest {
//
//    private StoreDao storeDao;
//    private WebInventoryDao webInventoryDao;
//    private StockSelectionStrategy strategy;
//    private AllocationRequest request;
//    private Connection conn;
//
//    @BeforeEach
//    void setUp() {
//        storeDao = mock(StoreDao.class);
//        webInventoryDao = mock(WebInventoryDao.class);
//        strategy = mock(StockSelectionStrategy.class);
//        request = new AllocationRequest("ITEM1", 15, null); // shelfId is irrelevant for web
//        conn = mock(Connection.class);
//    }
//
//    @Test
//    void shouldAllocateStockToWebSuccessfully() throws Exception {
//        StoreStock batch1 = new StoreStock("ITEM1", "B1", 10, LocalDate.now().plusDays(10));
//        StoreStock batch2 = new StoreStock("ITEM1", "B2", 10, LocalDate.now().plusDays(20));
//        List<StoreStock> availableBatches = Arrays.asList(batch1, batch2);
//
//        List<AllocatedRestockDTO> allocations = Arrays.asList(
//                new AllocatedRestockDTO("ITEM1", "B1", 10),
//                new AllocatedRestockDTO("ITEM1", "B2", 5)
//        );
//
//        when(storeDao.findByItemCode(conn, "ITEM1")).thenReturn(availableBatches);
//        when(strategy.allocateStock(availableBatches, 15)).thenReturn(allocations);
//
//        AllocateToWebCommand command = new AllocateToWebCommand(request, strategy, storeDao, webInventoryDao);
//
//        List<AllocatedRestockDTO> result = command.execute(conn);
//
//        assertEquals(2, result.size());
//        verify(storeDao, times(2)).updateStockAfterAllocation(any(), anyString(), anyString(), anyInt());
//        verify(webInventoryDao, times(2)).logWebRestockTransaction(any(), anyString(), anyString(), anyInt());
//    }
//
//    @Test
//    void shouldThrowIfStockIsInsufficient() throws Exception {
//        StoreStock batch = new StoreStock("ITEM1", "B1", 5, LocalDate.now().plusDays(10));
//        List<StoreStock> availableBatches = Collections.singletonList(batch);
//
//        when(storeDao.findByItemCode(conn, "ITEM1")).thenReturn(availableBatches);
//        when(strategy.allocateStock(availableBatches, 15))
//                .thenThrow(new IllegalArgumentException("Insufficient stock to fulfill request of 15"));
//
//        AllocateToWebCommand command = new AllocateToWebCommand(request, strategy, storeDao, webInventoryDao);
//
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> command.execute(conn));
//        assertTrue(exception.getMessage().contains("Insufficient stock"));
//    }
//
//    @Test
//    void shouldThrowIfAllBatchesHaveZeroStock() throws Exception {
//        StoreStock batch1 = new StoreStock("ITEM1", "B1", 0, LocalDateTime.now().plusDays(10));
//        StoreStock batch2 = new StoreStock("ITEM1", "B2", 0, LocalDateTime.now().plusDays(5));
//        List<StoreStock> zeroStockBatches = Arrays.asList(batch1, batch2);
//
//        when(storeDao.findByItemCode(conn, "ITEM1")).thenReturn(zeroStockBatches);
//        when(strategy.allocateStock(zeroStockBatches, 10))
//                .thenThrow(new IllegalArgumentException("Insufficient stock to fulfill request of 10"));
//
//        AllocateToWebCommand command = new AllocateToWebCommand(request, strategy, storeDao, webInventoryDao);
//
//        assertThrows(IllegalArgumentException.class, () -> command.execute(conn));
//    }
//
//    @Test
//    void shouldUseMultipleBatchesToFulfillRequest() throws Exception {
//        StoreStock batch1 = new StoreStock("ITEM1", "B1", 7, LocalDateTime.now().plusDays(3));
//        StoreStock batch2 = new StoreStock("ITEM1", "B2", 8, LocalDateTime.now().plusDays(6));
//        List<StoreStock> batches = Arrays.asList(batch1, batch2);
//
//        List<AllocatedRestockDTO> allocations = Arrays.asList(
//                new AllocatedRestockDTO("ITEM1", "B1", 7),
//                new AllocatedRestockDTO("ITEM1", "B2", 3)
//        );
//
//        AllocationRequest partialRequest = new AllocationRequest("ITEM1", 10, null);
//        AllocateToWebCommand command = new AllocateToWebCommand(partialRequest, strategy, storeDao, webInventoryDao);
//
//        List<AllocatedRestockDTO> result = command.execute(conn);
//
//        assertEquals(2, result.size());
//        assertEquals(7, result.get(0).getAllocatedQuantity());
//        assertEquals(3, result.get(1).getAllocatedQuantity());
//    }
//}