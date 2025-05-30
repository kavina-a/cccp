package com.syos.application.dto.request;

public class CreateProductRequest {
    private String itemCode;
    private String name;
    private double price;

    public CreateProductRequest() {}

    public CreateProductRequest(String itemCode, String name, double price) {
        this.itemCode = itemCode;
        this.name = name;
        this.price = price;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}