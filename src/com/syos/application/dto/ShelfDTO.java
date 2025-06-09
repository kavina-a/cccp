package com.syos.application.dto;

public class ShelfDTO {
    private boolean success;
    private String message;
    private String shelfId;
    private String itemCode;
    private int quantityOnShelf;
    private String updatedAt;

    public ShelfDTO(boolean success, String message, String shelfId, String itemCode, int quantityOnShelf, String updatedAt) {
        this.success = success;
        this.message = message;
        this.shelfId = shelfId;
        this.itemCode = itemCode;
        this.quantityOnShelf = quantityOnShelf;
        this.updatedAt = updatedAt;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getShelfId() {
        return shelfId;
    }

    public String getItemCode() {
        return itemCode;
    }

    public int getQuantityOnShelf() {
        return quantityOnShelf;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
}
