package com.syos.domain.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class StoreStock {

    private String itemCode;
    private String batchCode;
    private int initialStock;
    private int currentStock;
    private LocalDateTime expiryDate;
    private LocalDateTime receivedDate;
    private StockStatus status = StockStatus.ACTIVE;
    private String updatedBy;
    private LocalDateTime updatedDateTime;

    public StoreStock() {}

    public StoreStock(String itemCode, String batchCode, int initialStock) {
        this.itemCode = itemCode;
        this.batchCode = batchCode;
        this.initialStock = initialStock;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public int getInitialStock() {
        return initialStock;
    }

    public void setInitialStock(int initialStock) {
        this.initialStock = initialStock;
    }

    public int getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(int currentStock) {
        this.currentStock = currentStock;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public LocalDateTime getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(LocalDateTime receivedDate) {
        this.receivedDate = receivedDate;
    }

    public StockStatus getStatus() {
        return status;
    }

    public void setStatus(StockStatus status) {
        this.status = status;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    public void setUpdatedDateTime(LocalDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    public static class StoreStockId implements Serializable {
        private String itemCode;
        private String batchCode;

        public StoreStockId() {}

        public StoreStockId(String itemCode, String batchCode) {
            this.itemCode = itemCode;
            this.batchCode = batchCode;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof StoreStockId that)) return false;
            return Objects.equals(itemCode, that.itemCode) &&
                    Objects.equals(batchCode, that.batchCode);
        }

        @Override
        public int hashCode() {
            return Objects.hash(itemCode, batchCode);
        }
    }
}