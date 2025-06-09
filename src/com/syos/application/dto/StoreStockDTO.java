package com.syos.application.dto;

import java.time.LocalDateTime;

public class StoreStockDTO {
    private String itemCode;
    private String batchCode;
    private int initialStock;
    private int currentStock;
    private LocalDateTime expiryDate;
    private LocalDateTime receivedDate;

    public StoreStockDTO(String itemCode, String batchCode, int initialStock, int currentStock,
                         LocalDateTime expiryDate, LocalDateTime receivedDate) {
        this.itemCode = itemCode;
        this.batchCode = batchCode;
        this.initialStock = initialStock;
        this.currentStock = currentStock;
        this.expiryDate = expiryDate;
        this.receivedDate = receivedDate;
    }

    public String getItemCode() {
        return itemCode;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public int getInitialStock() {
        return initialStock;
    }

    public int getCurrentStock() {
        return currentStock;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public LocalDateTime getReceivedDate() {
        return receivedDate;
    }

    @Override
    public String toString() {
        return "ItemCode: " + itemCode +
                ", BatchCode: " + batchCode +
                ", InitialStock: " + initialStock +
                ", CurrentStock: " + currentStock +
                ", ExpiryDate: " + expiryDate +
                ", ReceivedDate: " + receivedDate;
    }
}