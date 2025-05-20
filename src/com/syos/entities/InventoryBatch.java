package com.syos.entities;

import java.time.LocalDate;

public class InventoryBatch {
    private Product product;
    private int initialQuantity;
    private int currentQuantity;
    private LocalDate purchaseDate;
    private LocalDate expiryDate;

    public InventoryBatch(Product product, int quantity, LocalDate purchaseDate, LocalDate expiryDate) {
        this.product = product;
        this.initialQuantity = quantity;
        this.currentQuantity = quantity;
        this.purchaseDate = purchaseDate;
        this.expiryDate = expiryDate;
    }

    public boolean isExpired(LocalDate currentDate) {
        return expiryDate.isBefore(currentDate);
    }

    public void removeQuantity(int qty) {
        if (qty > currentQuantity) throw new IllegalArgumentException("Not enough stock in batch.");
        currentQuantity -= qty;
    }

    // Getters and setters omitted for brevity
}