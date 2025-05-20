package com.syos.data.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tblProduct")
public class Product {

    @Id
    @Column(name = "ItemCode")
    private String itemCode;

    @Column(name = "BatchCode")
    private String batchCode;

    @Column(name = "ItemName")
    private String itemName;

    @Column(name = "Price")
    private double price;

    @Column(name = "PurchaseDate")
    private LocalDateTime purchaseDate;

    @Column(name = "ExpiryDate")
    private LocalDateTime expiryDate;

    @Column(name = "InitialQuantity")
    private int initialQuantity;

    @Column(name = "CurrentQuantity")
    private int currentQuantity;

    @Column(name = "IsActive")
    private boolean isActive;

    @Column(name = "IsDeleted")
    private boolean isDeleted;

    @Column(name = "UpdatedDateTime")
    private LocalDateTime updatedDateTime;

    @Column(name = "UpdatedBy")
    private int updatedBy;

    public Product() {
    }

    // Getters and Setters
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

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public int getInitialQuantity() {
        return initialQuantity;
    }

    public void setInitialQuantity(int initialQuantity) {
        this.initialQuantity = initialQuantity;
    }

    public int getCurrentQuantity() {
        return currentQuantity;
    }

    public void setCurrentQuantity(int currentQuantity) {
        this.currentQuantity = currentQuantity;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public LocalDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    public void setUpdatedDateTime(LocalDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    public int getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(int updatedBy) {
        this.updatedBy = updatedBy;
    }
}
