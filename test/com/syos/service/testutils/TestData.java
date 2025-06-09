package com.syos.service.testutils;

import com.syos.domain.entity.*;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TestData {
    public static Employee employee() {
        Employee e = new Employee();
        e.setEmployeeID("EMP-001");
        e.setUsername("admin");
        e.setPassword("admin123");
        e.setEmail("admin@example.com");
        e.setRole(EmployeeRole.MANAGER);
        e.setStatus("ACTIVE");
        e.setCreatedAt(Date.valueOf(LocalDate.now()));
        return e;
    }

    public static StoreStock storeStock(String itemCode, String batchCode, int stockQty) {
        StoreStock stock = new StoreStock(itemCode, batchCode, stockQty);
        stock.setCurrentStock(stockQty);
        stock.setStatus(StockStatus.ACTIVE);
        stock.setExpiryDate(LocalDateTime.now().plusMonths(1));
        stock.setReceivedDate(LocalDateTime.now());
        return stock;
    }

    public static ShelfInventory shelfInventory(String itemCode, String batchCode, int quantity, StockStatus status) {
        ShelfInventory inv = new ShelfInventory();
        inv.setItemCode(itemCode);
        inv.setBatchCode(batchCode);
        inv.setQuantityRemaining(quantity);
        inv.setStatus(status);
        return inv;
    }


}
