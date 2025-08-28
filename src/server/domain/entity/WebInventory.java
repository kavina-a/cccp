package server.domain.entity;

import java.time.LocalDateTime;

public class WebInventory {

    private String itemCode;
    private String batchCode;
    private String itemName;
    private int quantityRemaining;
    private int quantityTransferred;
    private LocalDateTime transferredDate;
    private StockStatus status;
    private LocalDateTime updatedDateTime;
    private Integer lastUpdatedBy;

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getQuantityRemaining() {
        return quantityRemaining;
    }

    public void setQuantityRemaining(int quantityRemaining) {
        this.quantityRemaining = quantityRemaining;
    }


    public int getQuantityTransferred() {
        return quantityTransferred;
    }

    public void setQuantityTransferred(int quantityTransferred) {
        this.quantityTransferred = quantityTransferred;
    }

    public LocalDateTime getTransferredDate() {
        return transferredDate;
    }

    public void setTransferredDate(LocalDateTime transferredDate) {
        this.transferredDate = transferredDate;
    }

    public StockStatus getStatus() {
        return status;
    }

    public void setStatus(StockStatus status) {
        this.status = status;
    }

    public LocalDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }
    public void setUpdatedDateTime(LocalDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    public Integer getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(Integer lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }
}
