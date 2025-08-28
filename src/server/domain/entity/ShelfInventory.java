package server.domain.entity;

import java.time.LocalDateTime;

public class ShelfInventory {

    private Long id;
    private String itemCode;
    private String batchCode;
    private String shelfId;
    private int quantityTransferred;
    private int quantityRemaining;
    private LocalDateTime movedDate;
    private String updatedBy;
    private StockStatus status = StockStatus.ACTIVE;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getShelfId() {
        return shelfId;
    }

    public void setShelfId(String shelfId) {
        this.shelfId = shelfId;
    }

    public int getQuantityTransferred() {
        return quantityTransferred;
    }

    public void setQuantityTransferred(int quantityTransferred) {
        this.quantityTransferred = quantityTransferred;
    }

    public int getQuantityRemaining() {
        return quantityRemaining;
    }

    public void setQuantityRemaining(int quantityRemaining) {
        this.quantityRemaining = quantityRemaining;
    }

    public LocalDateTime getMovedDate() {
        return movedDate;
    }

    public void setMovedDate(LocalDateTime movedDate) {
        this.movedDate = movedDate;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public StockStatus getStatus() {
        return status;
    }

    public void setStatus(StockStatus status) {
        this.status = status;
    }
}
