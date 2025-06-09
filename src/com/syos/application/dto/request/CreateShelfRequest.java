package com.syos.application.dto.request;

public class CreateShelfRequest {

    private String shelfId;
    private String itemCode;
    private int quantityOnShelf;
    private String location;

    public CreateShelfRequest(String shelfId, String itemCode, int quantityOnShelf, String location) {
        this.shelfId = shelfId;
        this.itemCode = itemCode;
        this.quantityOnShelf = quantityOnShelf;
        this.location = location;
    }

    public String getShelfId() {
        return shelfId;
    }

    public void setShelfId(String shelfId) {
        this.shelfId = shelfId;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public int getQuantityOnShelf() {
        return quantityOnShelf;
    }

    public void setQuantityOnShelf(int quantityOnShelf) {
        this.quantityOnShelf = quantityOnShelf;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}