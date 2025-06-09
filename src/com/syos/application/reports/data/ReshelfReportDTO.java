package com.syos.application.reports.data;

public class ReshelfReportDTO {
    private String itemCode;
    private String itemName;
    private int shelfCapacity;
    private int currentQuantity;
    private int expiredQuantity;
    private int soldQuantity;
    private int toReshelfQuantity;
    private String lastMovedDate;

    public ReshelfReportDTO(String itemCode, String itemName,
                            int shelfCapacity, int currentQuantity, int expiredQuantity,
                            int soldQuantity, int toReshelfQuantity, String lastMovedDate) {
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.shelfCapacity = shelfCapacity;
        this.currentQuantity = currentQuantity;
        this.expiredQuantity = expiredQuantity;
        this.soldQuantity = soldQuantity;
        this.toReshelfQuantity = toReshelfQuantity;
        this.lastMovedDate = lastMovedDate;
    }

    public String getItemCode() { return itemCode; }
    public String getItemName() { return itemName; }
    public int getShelfCapacity() { return shelfCapacity; }
    public int getCurrentQuantity() { return currentQuantity; }
    public int getExpiredQuantity() { return expiredQuantity; }
    public int getSoldQuantity() { return soldQuantity; }
    public int getToReshelfQuantity() { return toReshelfQuantity; }
    public String getLastMovedDate() { return lastMovedDate; }
}