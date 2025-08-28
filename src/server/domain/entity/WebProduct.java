package server.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class WebProduct {

    private String itemCode;
    private String itemName;
    private BigDecimal price;
//    private int availableQuantity;
    private ProductStatus status;
    private LocalDateTime lastUpdated;
//    private String lastUpdatedBy;

    public WebProduct() {}

    public WebProduct(String itemCode, String itemName, BigDecimal price, ProductStatus status, LocalDateTime lastUpdated) {
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.price = price;
        this.status = status;
        this.lastUpdated = lastUpdated;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

//    public int getAvailableQuantity() {
//        return availableQuantity;
//    }
//
//    public void setAvailableQuantity(int availableQuantity) {
//        this.availableQuantity = availableQuantity;
//    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

}