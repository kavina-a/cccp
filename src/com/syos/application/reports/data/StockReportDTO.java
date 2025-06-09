package com.syos.application.reports.data;

import java.time.LocalDate;

public class StockReportDTO {
    private String itemCode;
    private String itemName;
    private String batchCode;
    private int initialStock;
    private int currentStock;
    private LocalDate receivedDate;
    private LocalDate expiryDate;
    private String status;

    public StockReportDTO(String itemCode, String itemName, String batchCode, int initialStock,
                          int currentStock, LocalDate receivedDate, LocalDate expiryDate, String status) {
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.batchCode = batchCode;
        this.initialStock = initialStock;
        this.currentStock = currentStock;
        this.receivedDate = receivedDate;
        this.expiryDate = expiryDate;
        this.status = status;
    }

    public String getItemCode() { return itemCode; }
    public String getItemName() { return itemName; }
    public String getBatchCode() { return batchCode; }
    public int getInitialStock() { return initialStock; }
    public int getCurrentStock() { return currentStock; }
    public LocalDate getReceivedDate() { return receivedDate; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public String getStatus() { return status; }
}