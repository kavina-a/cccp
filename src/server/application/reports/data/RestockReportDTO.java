package server.application.reports.data;


public class RestockReportDTO {
    private String itemCode;
    private String itemName;
    private Integer currentStock;
    private Integer reorderLevel;
    private Integer reorderDeficit;
    private Integer lastOrderedQuantity;
    private Integer ToRestockQuantity;

    public RestockReportDTO(String itemCode, String itemName,
                            Integer currentStock, Integer reorderLevel,
                            Integer reorderDeficit, Integer lastOrderedQuantity, Integer ToRestockQuantity) {
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.currentStock = currentStock;
        this.reorderLevel = reorderLevel;
        this.reorderDeficit = reorderDeficit;
        this.lastOrderedQuantity = lastOrderedQuantity;
        this.ToRestockQuantity = ToRestockQuantity;
    }

    public RestockReportDTO() {
    }

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

    public Integer getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
    }

    public Integer getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(Integer reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    public Integer getReorderDeficit() {
        return reorderDeficit;
    }

    public void setReorderDeficit(Integer reorderDeficit) {
        this.reorderDeficit = reorderDeficit;
    }

    public Integer getLastOrderedQuantity() {
        return lastOrderedQuantity;
    }

    public void setLastOrderedQuantity(Integer lastOrderedQuantity) {
        this.lastOrderedQuantity = lastOrderedQuantity;
    }

    public Integer getToRestockQuantity() {
        return ToRestockQuantity;
    }

    public void setToRestockQuantity(Integer toRestockQuantity) {
        ToRestockQuantity = toRestockQuantity;
    }
}