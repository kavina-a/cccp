package server.application.dto;

public class LowStockEventDTO {
    private final String itemCode;
    private final String itemName;
    private final int currentQuantity;

    public LowStockEventDTO(String itemCode, String itemName, int currentQuantity) {
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.currentQuantity = currentQuantity;
    }

    public String getItemCode() { return itemCode; }
    public String getItemName() { return itemName; }
    public int getCurrentQuantity() { return currentQuantity; }
}
