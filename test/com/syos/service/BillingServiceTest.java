package com.syos.service;

import server.domain.entity.Bill;
import server.domain.entity.BillItem;
import server.application.dto.request.BillItemRequest;
import server.application.dto.request.CreateBillRequest;
import server.application.postprocessors.interfaces.BillPostProcessor;
import server.domain.entity.Customer;
import server.domain.service.BillItemService;
import server.data.dao.interfaces.BillDao;
import server.data.dao.interfaces.BillItemDao;
import server.data.dao.interfaces.CustomerDao;
import server.domain.entity.TransactionType;

import server.domain.service.BillingService;
import server.domain.strategy.billing.interfaces.BillingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillingServiceTest {

    @Mock
    private BillDao billDao;
    @Mock
    private BillItemDao billItemDao;
    @Mock
    private CustomerDao customerDao;
    @Mock
    private BillItemService billItemService;
    @Mock
    private BillingStrategy billingStrategy;
    @Mock
    private BillPostProcessor postProcessor1;
    @Mock
    private BillPostProcessor postProcessor2;

    private BillingService billingService;

    private Connection connection; // Can be mocked/null, since passed through

    @BeforeEach
    void setUp() {
        Map<TransactionType, BillingStrategy> strategyMap = new HashMap<>();
        strategyMap.put(TransactionType.OTC, billingStrategy);
//        strategyMap.put(TransactionType.WEB, new WebBillingStrategy());

        List<BillPostProcessor> postProcessors = List.of(postProcessor1, postProcessor2);

        billingService = new BillingService(
                billDao, billItemDao, billItemService, customerDao, strategyMap, postProcessors
        );
    }

//    	1.	Your service method correctly fails when a DAO method internally fails (throws SQLException).
//            2.	You propagate or handle the failure properly.

    // 1. Customer is null
    @Test
    void shouldThrowWhenCustomerIsNull() {
        CreateBillRequest request = new CreateBillRequest();
        request.setCustomer(null); // ← test subject
        request.setItems(List.of(new BillItemRequest("P001", 1, BigDecimal.valueOf(100.0))));
        request.setTransactionType(TransactionType.OTC); // ← prevent strategy error

        assertThrows(IllegalArgumentException.class, () ->
                billingService.createBill(request));
    }

    // 2. Items list is empty
    @Test
    void shouldThrowWhenItemsListIsEmpty() {
        CreateBillRequest request = new CreateBillRequest();
        request.setCustomer(new Customer());
        request.setItems(List.of());
        request.setTransactionType(TransactionType.OTC);

        when(billItemService.createBillItems(any(), eq(List.of())))
                .thenReturn(List.of());

        assertThrows(IllegalArgumentException.class, () ->
                billingService.createBill(request));
    }

    // 3. Strategy not found for transaction type
    @Test
    void shouldThrowWhenStrategyIsNotFound() {
        CreateBillRequest request = new CreateBillRequest();
        request.setCustomer(new Customer());
        request.setItems(List.of(new BillItemRequest("P001", 1, BigDecimal.valueOf(100.0))));
        request.setTransactionType(TransactionType.WEB); // Not in map

        assertThrows(UnsupportedOperationException.class, () ->
                billingService.createBill(request));
    }

    // 4. Use correct strategy to generate bill
    @Test
    void shouldUseCorrectStrategyToGenerateBill() {
        CreateBillRequest request = new CreateBillRequest();
        request.setCustomer(new Customer());
        request.setItems(List.of(new BillItemRequest("P001", 1, BigDecimal.valueOf(100.0))));
        request.setTransactionType(TransactionType.OTC);

        List<BillItem> mockItems = List.of(new BillItem());
        when(billItemService.createBillItems(any(), any())).thenReturn(mockItems);
        try {
            when(billDao.getNextSerialNumber(any(), any())).thenReturn(123);
        } catch (java.sql.SQLException throwables) {
            throw new RuntimeException(throwables);
        }
        Bill generatedBill = new Bill();
        when(billingStrategy.generateBill(any(), any(), any(), any(), anyInt())).thenReturn(generatedBill);

        Bill result = billingService.createBill(request);

        assertSame(generatedBill, result);
        verify(billingStrategy).generateBill(eq(request), any(), eq(mockItems), eq(request.getCustomer()), eq(123));
    }

    //
    // 5. Bill should have items set
    @Test
    void shouldSetBillItemsOnGeneratedBill() {
        CreateBillRequest request = new CreateBillRequest();
        request.setCustomer(new Customer());
        request.setItems(List.of(new BillItemRequest("P001", 1, BigDecimal.valueOf(100.0))));
        request.setTransactionType(TransactionType.OTC);

        List<BillItem> mockItems = List.of(new BillItem());
        Bill generatedBill = new Bill();

        when(billItemService.createBillItems(any(), any())).thenReturn(mockItems);
        try {
            when(billDao.getNextSerialNumber(any(), any())).thenReturn(100);
        } catch (java.sql.SQLException throwables) {
            throw new RuntimeException(throwables);
        }
        when(billingStrategy.generateBill(any(), any(), any(), any(), anyInt())).thenReturn(generatedBill);

        Bill bill = billingService.createBill(request);

        assertEquals(mockItems, bill.getItems());
    }

    // 6. Bill should be saved using DAO
    @Test
    void shouldPersistBillUsingDao() {
        CreateBillRequest request = new CreateBillRequest();
        request.setCustomer(new Customer());
        request.setItems(List.of(new BillItemRequest("P001", 1, BigDecimal.valueOf(100.0))));
        request.setTransactionType(TransactionType.OTC);

        List<BillItem> mockItems = List.of(new BillItem());
        Bill generatedBill = new Bill();

        when(billItemService.createBillItems(any(), any())).thenReturn(mockItems);
        try {
            when(billDao.getNextSerialNumber(any(), any())).thenReturn(100);
        } catch (java.sql.SQLException throwables) {
            throw new RuntimeException(throwables);
        }
        when(billingStrategy.generateBill(any(), any(), any(), any(), anyInt())).thenReturn(generatedBill);

        billingService.createBill(request);

        try {
            verify(billDao).save(any(), eq(generatedBill));
        } catch (java.sql.SQLException throwables) {
            throw new RuntimeException(throwables);
        }
    }

    // 7. All bill items should be saved with bill reference
    @Test
    void shouldPersistEachBillItemWithBillReference() throws Exception {
        CreateBillRequest request = new CreateBillRequest();
        request.setCustomer(new Customer());
        request.setItems(List.of(new BillItemRequest("P001", 1, BigDecimal.valueOf(100.0))));
        request.setTransactionType(TransactionType.OTC);

        BillItem item1 = new BillItem();
        BillItem item2 = new BillItem();
        List<BillItem> items = List.of(item1, item2);
        Bill generatedBill = new Bill();

        when(billItemService.createBillItems(any(), any())).thenReturn(items);
        when(billDao.getNextSerialNumber(any(), any())).thenReturn(100);
        when(billingStrategy.generateBill(any(), any(), any(), any(), anyInt())).thenReturn(generatedBill);

        billingService.createBill(request);

        for (BillItem item : items) {
            assertEquals(generatedBill, item.getBill());
            verify(billItemDao).save(any(), eq(item));
        }
    }
//
    // 8. All post-processors should be called
    @Test
    void shouldInvokeAllPostProcessors() throws Exception {
        CreateBillRequest request = new CreateBillRequest();
        request.setCustomer(new Customer());
        request.setItems(List.of(new BillItemRequest("P001", 2, BigDecimal.valueOf(100.0))));
        request.setTransactionType(TransactionType.OTC);

        BillItem item = new BillItem();
        List<BillItem> items = List.of(item);
        Bill bill = new Bill();

        when(billItemService.createBillItems(any(), any())).thenReturn(items);
        try {
            when(billDao.getNextSerialNumber(any(), any())).thenReturn(1);
        } catch (java.sql.SQLException throwables) {
            throw new RuntimeException(throwables);
        }        when(billingStrategy.generateBill(any(), any(), any(), any(), anyInt())).thenReturn(bill);

        billingService.createBill(request);

        verify(postProcessor1).process(eq(bill), any());
        verify(postProcessor2).process(eq(bill), any());
    }

    // 9. Strategy throws an exception
    @Test
    void shouldPropagateExceptionFromStrategy() {
        CreateBillRequest request = new CreateBillRequest();
        request.setCustomer(new Customer());
        request.setItems(List.of(new BillItemRequest("P001", 1, BigDecimal.valueOf(100.0))));
        request.setTransactionType(TransactionType.OTC);

        when(billItemService.createBillItems(any(), any())).thenReturn(List.of(new BillItem()));
        try {
            when(billDao.getNextSerialNumber(any(), any())).thenReturn(999);
        } catch (java.sql.SQLException throwables) {
            throw new RuntimeException(throwables);
        }        when(billingStrategy.generateBill(any(), any(), any(), any(), anyInt()))
                .thenThrow(new RuntimeException("Strategy failed"));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                billingService.createBill(request)
        );

        assertEquals("Strategy failed", ex.getMessage());
    }

    // 10. One post-processor throws, others still invoked
    @Test
    void shouldHandlePostProcessorFailureGracefully() throws Exception {
        CreateBillRequest request = new CreateBillRequest();
        request.setCustomer(new Customer());
        request.setItems(List.of(new BillItemRequest("P001", 1, BigDecimal.valueOf(100.0))));
        request.setTransactionType(TransactionType.OTC);

        List<BillItem> items = List.of(new BillItem());
        Bill generatedBill = new Bill();

        when(billItemService.createBillItems(any(), any())).thenReturn(items);
        try {
            when(billDao.getNextSerialNumber(any(), any())).thenReturn(101);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        when(billingStrategy.generateBill(any(), any(), any(), any(), anyInt())).thenReturn(generatedBill);
        doThrow(new RuntimeException("PostProcessor1 failed")).when(postProcessor1).process(any(), any());

        assertDoesNotThrow(() -> billingService.createBill(request));
    }

    // 11. Serial number fails to generate
    @Test
    void shouldWrapSQLExceptionWhenSerialNumberGenerationFails() throws SQLException {
        CreateBillRequest request = validRequest(TransactionType.OTC);

        when(billItemService.createBillItems(any(), any()))
                .thenReturn(List.of(new BillItem()));
        when(billDao.getNextSerialNumber(any(), any()))
                .thenThrow(new SQLException("DB broken"));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                billingService.createBill(request)
        );

        assertTrue(ex.getMessage().contains("Failed to retrieve next serial number for billing"));
        assertTrue(ex.getCause() instanceof SQLException);
        assertEquals("DB broken", ex.getCause().getMessage());
    }

    // 12. Bill save fails (e.g., SQL error)
    @Test
    void shouldWrapSQLExceptionWhenBillSaveFails() throws SQLException {
        CreateBillRequest request = validRequest(TransactionType.OTC);

        List<BillItem> items = List.of(new BillItem());
        Bill bill = new Bill();

        when(billItemService.createBillItems(any(), any())).thenReturn(items);
        when(billDao.getNextSerialNumber(any(), any())).thenReturn(10);
        when(billingStrategy.generateBill(any(), any(), any(), any(), anyInt())).thenReturn(bill);
        doThrow(new SQLException("Simulated bill save failure")).when(billDao).save(any(), eq(bill));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                billingService.createBill(request));

        assertTrue(ex.getMessage().contains("Transaction failed"));
        assertTrue(ex.getCause() instanceof SQLException);
        assertEquals("Simulated bill save failure", ex.getCause().getMessage());
    }

    // 13. Bill item save fails
    @Test
    void shouldWrapSQLExceptionWhenBillItemSaveFails() throws SQLException {
        CreateBillRequest request = validRequest(TransactionType.OTC);

        BillItem item = new BillItem();
        Bill bill = new Bill();

        when(billItemService.createBillItems(any(), any())).thenReturn(List.of(item));
        when(billDao.getNextSerialNumber(any(), any())).thenReturn(101); // ← this throws checked SQLException
        when(billingStrategy.generateBill(any(), any(), any(), any(), anyInt())).thenReturn(bill);
        doNothing().when(billDao).save(any(), eq(bill));
        doThrow(new SQLException("item save failed")).when(billItemDao).save(any(), eq(item)); // simulate SQL error

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                billingService.createBill(request));

        assertTrue(ex.getMessage().contains("Transaction failed")); // Wrapped message
        assertTrue(ex.getCause() instanceof SQLException);
        assertEquals("item save failed", ex.getCause().getMessage());
    }

//    // 14. One post processor fails but others still invoked
//    @Test
//    void shouldContinueInvokingOtherPostProcessorsIfOneFails() {
//        CreateBillRequest request = validRequest(TransactionType.OTC);
//
//        Bill bill = new Bill();
//        BillItem item = new BillItem();
//
//        when(billItemService.createBillItems(any(), any())).thenReturn(List.of(item));
//        when(billDao.getNextSerialNumber(any(), any())).thenReturn(200);
//        when(billingStrategy.generateBill(any(), any(), any(), any(), anyInt())).thenReturn(bill);
//        doNothing().when(billDao).save(any(), eq(bill));
//        doNothing().when(billItemDao).save(any(), eq(item));
//        doThrow(new RuntimeException("Processor 1 failed")).when(postProcessor1).process(eq(bill), any());
//
//        assertDoesNotThrow(() -> billingService.createBill(request));
//
//        verify(postProcessor1).process(eq(bill), any());
//        verify(postProcessor2).process(eq(bill), any());
//    }
//
////    // 15. PostProcessors list is empty
////    @Test
////    void shouldHandleEmptyPostProcessorsWithoutError() {
////        CreateBillRequest request = validRequest(TransactionType.OTC);
////
////        BillItem item = new BillItem();
////        Bill bill = new Bill();
////
////        billingService = new BillingService(
////                billDao, billItemDao, billItemService, customerDao,
////                Map.of(TransactionType.OTC, billingStrategy),
////                List.of() // empty processors
////        );
////
////        when(billItemService.createBillItems(any(), any())).thenReturn(List.of(item));
////        when(billDao.getNextSerialNumber(any(), any())).thenReturn(12);
////        when(billingStrategy.generateBill(any(), any(), any(), any(), anyInt())).thenReturn(bill);
////
////        assertDoesNotThrow(() -> billingService.createBill(request));
////    }
//
    // 16. Item generation fails in BillItemService
    @Test
    void shouldThrowIfBillItemServiceFails() {
        CreateBillRequest request = validRequest(TransactionType.OTC);

        when(billItemService.createBillItems(any(), any()))
                .thenThrow(new RuntimeException("Item creation failed"));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                billingService.createBill(request));

        assertEquals("Item creation failed", ex.getMessage());
    }

    // 17. Strategy fails and DAO methods must not be called
    @Test
    void shouldNotCallDaosWhenStrategyFails() throws SQLException {
        CreateBillRequest request = validRequest(TransactionType.OTC);
        List<BillItem> items = List.of(new BillItem());

        when(billItemService.createBillItems(any(), any())).thenReturn(items);
        when(billDao.getNextSerialNumber(any(), any())).thenReturn(5);
        when(billingStrategy.generateBill(any(), any(), any(), any(), anyInt()))
                .thenThrow(new RuntimeException("strategy error"));

        assertThrows(RuntimeException.class, () ->
                billingService.createBill(request));

        verify(billDao, never()).save(any(), any());
        verify(billItemDao, never()).save(any(), any());
    }

    // 18. Null transaction type
    @Test
    void shouldThrowWhenTransactionTypeIsNull() {
        CreateBillRequest request = validRequest(null);
        assertThrows(UnsupportedOperationException.class, () ->
                billingService.createBill(request));
    }

    // 19. Zero quantity item
    @Test
    void shouldThrowIfItemQuantityIsZero() {
        CreateBillRequest request = new CreateBillRequest();
        request.setCustomer(new Customer());
        request.setTransactionType(TransactionType.OTC);
        request.setItems(List.of(new BillItemRequest("P001", 0, BigDecimal.valueOf(100.0))));

        when(billItemService.createBillItems(any(), any()))
                .thenThrow(new IllegalArgumentException("Quantity must be positive"));

        assertThrows(IllegalArgumentException.class, () ->
                billingService.createBill(request));
    }

    // 20. Strategy returns null bill
    @Test
    void shouldThrowIfStrategyReturnsNullBill() throws SQLException {
        CreateBillRequest request = validRequest(TransactionType.OTC);

        when(billItemService.createBillItems(any(), any())).thenReturn(List.of(new BillItem()));
        when(billDao.getNextSerialNumber(any(), any())).thenReturn(77);
        when(billingStrategy.generateBill(any(), any(), any(), any(), anyInt()))
                .thenReturn(null);

        assertThrows(NullPointerException.class, () ->
                billingService.createBill(request));
    }

    @Test
    void shouldThrowWhenTransactionTypeIsMissing() {
        CreateBillRequest request = validRequest(null);
        assertThrows(UnsupportedOperationException.class, () -> billingService.createBill(request));
    }

    @Test
    void shouldThrowIfItemQuantityIsNegative() {
        CreateBillRequest request = new CreateBillRequest();
        request.setCustomer(new Customer());
        request.setTransactionType(TransactionType.OTC);
        request.setItems(List.of(new BillItemRequest("P001", -5, BigDecimal.valueOf(50.0))));

        when(billItemService.createBillItems(any(), any()))
                .thenThrow(new IllegalArgumentException("Quantity must be positive"));

        assertThrows(IllegalArgumentException.class, () -> billingService.createBill(request));
    }

    @Test
    void shouldReturnCreatedBillOnSuccess() throws SQLException {
        CreateBillRequest request = validRequest(TransactionType.OTC);
        Bill bill = new Bill();
        BillItem item = new BillItem();
        bill.setItems(List.of(item));
        item.setBill(bill);

        when(billItemService.createBillItems(any(), any())).thenReturn(List.of(item));
        when(billDao.getNextSerialNumber(any(), any())).thenReturn(100);
        when(billingStrategy.generateBill(any(), any(), any(), any(), anyInt())).thenReturn(bill);
        doNothing().when(billDao).save(any(), eq(bill));
        doNothing().when(billItemDao).save(any(), eq(item));
        doNothing().when(postProcessor1).process(eq(bill), any());
        doNothing().when(postProcessor2).process(eq(bill), any());

        Bill result = billingService.createBill(request);

        assertNotNull(result);
        assertEquals(bill, result);
    }

    @Test
    void shouldSaveAllBillItems() throws SQLException {
        CreateBillRequest request = validRequest(TransactionType.OTC);
        Bill bill = new Bill();
        BillItem item1 = new BillItem();
        BillItem item2 = new BillItem();
        bill.setItems(List.of(item1, item2));

        when(billItemService.createBillItems(any(), any())).thenReturn(List.of(item1, item2));
        when(billDao.getNextSerialNumber(any(), any())).thenReturn(111);
        when(billingStrategy.generateBill(any(), any(), any(), any(), anyInt())).thenReturn(bill);
        doNothing().when(billDao).save(any(), eq(bill));
        doNothing().when(billItemDao).save(any(), eq(item1));
        doNothing().when(billItemDao).save(any(), eq(item2));

        assertDoesNotThrow(() -> billingService.createBill(request));
        verify(billItemDao).save(any(), eq(item1));
        verify(billItemDao).save(any(), eq(item2));
    }

//    @Test
//    void shouldSkipNullPostProcessorInList() {
//        CreateBillRequest request = validRequest(TransactionType.OTC);
//        Bill bill = new Bill();
//        BillItem item = new BillItem();
//
//        billingService = new BillingService(
//                billDao, billItemDao, billItemService, customerDao,
//                Map.of(TransactionType.OTC, billingStrategy),
//                List.of(postProcessor1, null, postProcessor2)
//        );
//
//        when(billItemService.createBillItems(any(), any())).thenReturn(List.of(item));
//        when(billDao.getNextSerialNumber(any(), any())).thenReturn(120);
//        when(billingStrategy.generateBill(any(), any(), any(), any(), anyInt())).thenReturn(bill);
//
//        assertDoesNotThrow(() -> billingService.createBill(request));
//        verify(postProcessor1).process(eq(bill), any());
//        verify(postProcessor2).process(eq(bill), any());
//    }

    @Test
    void shouldThrowIfCustomerIsInvalid() {
        Customer invalidCustomer = new Customer();
        CreateBillRequest request = new CreateBillRequest();
        request.setCustomer(invalidCustomer);
        request.setTransactionType(TransactionType.OTC);
        request.setItems(List.of(new BillItemRequest("P001", 1, BigDecimal.valueOf(50.0))));

        when(billItemService.createBillItems(any(), any()))
                .thenThrow(new IllegalArgumentException("Invalid customer"));

        assertThrows(IllegalArgumentException.class, () -> billingService.createBill(request));
    }

    @Test
    void shouldPropagateStrategyUnsupportedOperationException() throws SQLException {
        CreateBillRequest request = validRequest(TransactionType.OTC);

        when(billItemService.createBillItems(any(), any())).thenReturn(List.of(new BillItem()));
        when(billDao.getNextSerialNumber(any(), any())).thenReturn(101);
        when(billingStrategy.generateBill(any(), any(), any(), any(), anyInt()))
                .thenThrow(new UnsupportedOperationException("Strategy not supported"));

        assertThrows(UnsupportedOperationException.class, () -> billingService.createBill(request));
    }

    @Test
    void shouldThrowWhenCustomerAndItemsAreMissing() {
        CreateBillRequest request = new CreateBillRequest();
        request.setCustomer(null);
        request.setItems(List.of());

        assertThrows(IllegalArgumentException.class, () -> billingService.createBill(request));
    }

//    @Test
//    void shouldCreateBillSuccessfullyForWebTransaction() throws SQLException {
//        CreateBillRequest request = validRequest(TransactionType.WEB);
//        Bill bill = new Bill();
//        BillItem item = new BillItem();
//
//        when(billItemService.createBillItems(any(), any())).thenReturn(List.of(item));
//        when(billDao.getNextSerialNumber(any(), any())).thenReturn(200);
//        when(billingStrategy.generateBill(any(), any(), any(), any(), anyInt())).thenReturn(bill);
//        doNothing().when(billDao).save(any(), eq(bill));
//        doNothing().when(billItemDao).save(any(), eq(item));
//
//        assertDoesNotThrow(() -> billingService.createBill(request));
//    }

    // Utility to create valid request
    private CreateBillRequest validRequest(TransactionType type) {
        CreateBillRequest request = new CreateBillRequest();
        request.setCustomer(new Customer());
        request.setTransactionType(type);
        request.setItems(List.of(new BillItemRequest("P001", 1, BigDecimal.valueOf(100.0))));
        return request;
    }
}