package com.syos.application;

import com.syos.application.postprocessors.StockDeductionProcessor;
import com.syos.domain.entity.*;
import com.syos.domain.service.ShelfService;
import com.syos.domain.service.WebInventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class StockDeductionProcessorTest {

    private WebInventoryService webInventoryService;
    private ShelfService shelfService;
    private StockDeductionProcessor processor;
    private Connection mockConnection;

    @BeforeEach
    void setUp() {
        webInventoryService = mock(WebInventoryService.class);
        shelfService = mock(ShelfService.class);
        mockConnection = mock(Connection.class);
        processor = new StockDeductionProcessor(webInventoryService, shelfService);
    }

    @Test
    void process_shouldDeductStockFromWebInventory_whenWebBill() throws SQLException {
        Bill bill = new Bill();
        bill.setBillType(BillType.WEB_BILL);
        bill.setItems(List.of(createBillItem("ITEM1", 3)));

        assertDoesNotThrow(() -> processor.process(bill, mockConnection));
        verify(webInventoryService).deductStock(mockConnection, "ITEM1", 3);
        verifyNoInteractions(shelfService);
    }

    @Test
    void process_shouldDeductStockFromShelf_whenOtcBill() throws SQLException {
        Bill bill = new Bill();
        bill.setBillType(BillType.OTC_BILL);
        bill.setItems(List.of(createBillItem("ITEM2", 5)));

        assertDoesNotThrow(() -> processor.process(bill, mockConnection));
        verify(shelfService).deductStock(mockConnection, "ITEM2", 5);
        verifyNoInteractions(webInventoryService);
    }

    @Test
    void process_shouldThrowException_whenUnsupportedBillType() {
        Bill bill = new Bill();
        bill.setBillType(null); // Explicitly invalid
        bill.setItems(List.of(createBillItem("ITEMX", 2)));

        assertThrows(NullPointerException.class, () -> processor.process(bill, mockConnection));
    }

    @Test
    void process_shouldSkipWhenNoItemsPresent() {
        Bill bill = new Bill();
        bill.setBillType(BillType.WEB_BILL);
        bill.setItems(List.of());

        assertDoesNotThrow(() -> processor.process(bill, mockConnection));
        verifyNoInteractions(webInventoryService, shelfService);
    }

    @Test
    void process_shouldCallWebInventoryService_whenWebBill() throws SQLException {
        Bill bill = buildBill(BillType.WEB_BILL, "ITEM1", 3);
        processor.process(bill, mockConnection);
        verify(webInventoryService).deductStock(mockConnection, "ITEM1", 3);
        verify(shelfService, never()).deductStock(any(), any(), anyInt());
    }

    @Test
    void process_shouldCallShelfService_whenOTCBill() throws SQLException {
        Bill bill = buildBill(BillType.OTC_BILL, "ITEM2", 5);
        processor.process(bill, mockConnection);
        verify(shelfService).deductStock(mockConnection, "ITEM2", 5);
        verify(webInventoryService, never()).deductStock(any(), any(), anyInt());
    }

    @Test
    void process_shouldThrowException_whenBillTypeIsNull() {
        Bill bill = mock(Bill.class);
        BillItem item = mock(BillItem.class);
        when(bill.getItems()).thenReturn(List.of(item));
        when(item.getProduct()).thenReturn(mock(Product.class));
        when(item.getProduct().getItemCode()).thenReturn("ITEM123");
        when(item.getQuantity()).thenReturn(1);
        when(bill.getBillType()).thenReturn(null); // 👈 triggers NPE

        assertThrows(NullPointerException.class, () ->
                processor.process(bill, mock(Connection.class))
        );
    }

    @Test
    void process_shouldHandleMultipleItems() throws SQLException {
        Bill bill = new Bill();
        bill.setBillType(BillType.OTC_BILL);

        BillItem item1 = new BillItem();
        Product prod1 = new Product();
        prod1.setItemCode("ITEM1");
        item1.setProduct(prod1);
        item1.setQuantity(2);

        BillItem item2 = new BillItem();
        Product prod2 = new Product();
        prod2.setItemCode("ITEM2");
        item2.setProduct(prod2);
        item2.setQuantity(4);

        bill.setItems(List.of(item1, item2));

        processor.process(bill, mockConnection);

        verify(shelfService).deductStock(mockConnection, "ITEM1", 2);
        verify(shelfService).deductStock(mockConnection, "ITEM2", 4);
    }

    private BillItem createBillItem(String itemCode, int qty) {
        Product product = new Product();
        product.setItemCode(itemCode);

        BillItem item = new BillItem();
        item.setProduct(product);
        item.setQuantity(qty);
        return item;
    }

    private Bill buildBill(BillType type, String itemCode, int quantity) {
        Product product = new Product();
        product.setItemCode(itemCode);

        BillItem item = new BillItem();
        item.setProduct(product);
        item.setQuantity(quantity);

        Bill bill = new Bill();
        bill.setBillType(type);
        bill.setItems(List.of(item));
        return bill;
    }
}

