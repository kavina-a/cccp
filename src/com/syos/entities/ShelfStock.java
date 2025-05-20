package com.syos.entities;

public class ShelfStock {
    private Product product;
    private int quantity;

    public ShelfStock(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public void addStock(int qty) {
        this.quantity += qty;
    }

    public void removeStock(int qty) {
        if (qty > quantity) throw new IllegalArgumentException("Not enough stock on shelf.");
        this.quantity -= qty;
    }

    // Getters and setters omitted for brevity
}
