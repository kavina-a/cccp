package com.syos.application.dto.builder;

import com.syos.domain.entity.StoreStock;

import java.time.LocalDateTime;

public class StoreStockBuilder {

    private String itemCode;
    private String batchCode;
    private int initialStock;
    private int currentStock;
    private LocalDateTime expiryDate;
    private String updatedBy;
    private LocalDateTime updatedDateTime;
    private LocalDateTime receivedDate;

    public StoreStockBuilder withItemCode(String itemCode) {
        this.itemCode = itemCode;
        return this;
    }

    public StoreStockBuilder withBatchCode(String batchCode) {
        this.batchCode = batchCode;
        return this;
    }

    public StoreStockBuilder withInitialStock(int initialStock) {
        this.initialStock = initialStock;
        return this;
    }

    public StoreStockBuilder withCurrentStock(int currentStock) {
        this.currentStock = currentStock;
        return this;
    }

    public StoreStockBuilder withExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
        return this;
    }

    public StoreStockBuilder withReceivedDate(LocalDateTime receivedDate) {
        this.receivedDate = receivedDate;
        return this;
    }

    public StoreStockBuilder withUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
        return this;
    }

    public StoreStockBuilder withUpdatedDateTime(LocalDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
        return this;
    }

    public StoreStock build() {
        StoreStock stock = new StoreStock();
        stock.setItemCode(itemCode);
        stock.setBatchCode(batchCode);
        stock.setInitialStock(initialStock);
        stock.setCurrentStock(currentStock);
        stock.setExpiryDate(expiryDate);
        stock.setReceivedDate(receivedDate);
        stock.setUpdatedBy(updatedBy);
        stock.setUpdatedDateTime(updatedDateTime != null ? updatedDateTime : LocalDateTime.now());
        return stock;
    }
}