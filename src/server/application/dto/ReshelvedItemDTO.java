package server.application.dto;

import java.time.LocalDate;

public class ReshelvedItemDTO {
    private String itemCode;
    private String itemName;
    private int shelfCapacity;
    private int currentQuantity;
    private int toBeReshelved;
    private LocalDate lastMovedDate;
    private int expiredQuantity;
    private int soldQuantity;


    public ReshelvedItemDTO(String itemCode, String itemName, int shelfCapacity,
                            int currentQuantity, int toBeReshelved, LocalDate lastMovedDate, int expiredQuantity, int soldQuantity) {
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.shelfCapacity = shelfCapacity;
        this.currentQuantity = currentQuantity;
        this.toBeReshelved = toBeReshelved;
        this.lastMovedDate = lastMovedDate;
        this.expiredQuantity = expiredQuantity;
        this.soldQuantity = soldQuantity;
    }

    public ReshelvedItemDTO() {

    }

    // Getters and Setters

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

    public int getShelfCapacity() {
        return shelfCapacity;
    }

    public void setShelfCapacity(int shelfCapacity) {
        this.shelfCapacity = shelfCapacity;
    }

    public int getCurrentQuantity() {
        return currentQuantity;
    }

    public void setCurrentQuantity(int currentQuantity) {
        this.currentQuantity = currentQuantity;
    }

    public int getExpiredQuantity() {
        return expiredQuantity;
    }

    public void setExpiredQuantity(int expiredQuantity) {
        this.expiredQuantity = expiredQuantity;
    }

    public int getSoldQuantity() {
        return soldQuantity;
    }

    public void setSoldQuantity(int soldQuantity) {
        this.soldQuantity = soldQuantity;
    }

    public int getToBeReshelved() {
        return toBeReshelved;
    }

    public void setToBeReshelved(int toBeReshelved) {
        this.toBeReshelved = toBeReshelved;
    }



    public LocalDate getLastMovedDate() {
        return lastMovedDate;
    }

    public void setLastMovedDate(LocalDate lastMovedDate) {
        this.lastMovedDate = lastMovedDate;
    }
}