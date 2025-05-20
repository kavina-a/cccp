package com.syos.entities;

import java.time.LocalDateTime;
import java.math.BigDecimal;

//public class Product {
//    private SKU sku;
//    private String name;
//    private String brand;
//    private String variant;
//    private String unitSize;
//    private String barcode;
//    private double price;
//    private double discountRate;
//
//    public Product(SKU sku, String name, String brand, String variant, String unitSize, String barcode, double price, double discountRate) {
//        this.sku = sku;
//        this.name = name;
//        this.brand = brand;
//        this.variant = variant;
//        this.unitSize = unitSize;
//        this.barcode = barcode;
//        this.price = price;
//        this.discountRate = discountRate;
//    }
//
//    public double getPriceAfterDiscount() {
//        return price * (1 - discountRate);
//    }
//}

public class Product {
    private final String itemCode;
    private final String batchCode;
    private final String itemName;
    private final double price;
    private final LocalDateTime purchaseDate;
    private final LocalDateTime expiryDate;
    private final int initialQuantity;
    private final int currentQuantity;
    private final boolean isActive;
    private final boolean isDeleted;
    private final LocalDateTime updatedDateTime;
    private final int updatedBy;

    public Product(String itemCode, String batchCode, String itemName, double price,
                   LocalDateTime purchaseDate, LocalDateTime expiryDate, int initialQuantity,
                   int currentQuantity, boolean isActive, boolean isDeleted,
                   LocalDateTime updatedDateTime, int updatedBy) {
        this.itemCode = itemCode;
        this.batchCode = batchCode;
        this.itemName = itemName;
        this.price = price;
        this.purchaseDate = purchaseDate;
        this.expiryDate = expiryDate;
        this.initialQuantity = initialQuantity;
        this.currentQuantity = currentQuantity;
        this.isActive = isActive;
        this.isDeleted = isDeleted;
        this.updatedDateTime = updatedDateTime;
        this.updatedBy = updatedBy;
    }

    public String getItemCode() { return itemCode; }
    public String getBatchCode() { return batchCode; }
    public String getItemName() { return itemName; }
    public double getPrice() { return price; }
    public LocalDateTime getPurchaseDate() { return purchaseDate; }
    public LocalDateTime getExpiryDate() { return expiryDate; }
    public int getInitialQuantity() { return initialQuantity; }
    public int getCurrentQuantity() { return currentQuantity; }
    public boolean isActive() { return isActive; }
    public boolean isDeleted() { return isDeleted; }
    public LocalDateTime getUpdatedDateTime() { return updatedDateTime; }
    public int getUpdatedBy() { return updatedBy; }
}