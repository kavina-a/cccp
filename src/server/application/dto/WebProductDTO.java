package server.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class WebProductDTO {
    private String itemCode;
    private String itemName;
    private BigDecimal price;
//    private int quantity;
    private LocalDateTime updatedDateTime;

    public WebProductDTO(String itemCode, String itemName, BigDecimal price, LocalDateTime updatedDateTime) {
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.price = price;
//        this.quantity = quantity;
        this.updatedDateTime = updatedDateTime;
    }

    public String getItemCode() {
        return itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public BigDecimal getPrice() {
        return price;
    }

//    public int getQuantity() {
//        return quantity;
//    }

    public LocalDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    @Override
    public String toString() {
        return "ItemCode: " + itemCode +
                ", Name: " + itemName +
                ", Price: " + price +
                ", Last Updated: " + updatedDateTime;
    }
}