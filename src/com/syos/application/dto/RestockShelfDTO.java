package com.syos.application.dto;

import java.util.List;

public class RestockShelfDTO {
    private final String itemCode;
    private final int totalRestocked;
    private final List<String> batchCodesUsed;
    private final boolean success;
    private final String message;

    public RestockShelfDTO(String itemCode, int totalRestocked, List<String> batchCodesUsed, boolean success, String message) {
        this.itemCode = itemCode;
        this.totalRestocked = totalRestocked;
        this.batchCodesUsed = batchCodesUsed;
        this.success = success;
        this.message = message;
    }

    public String getItemCode() {
        return itemCode;
    }

    public int getTotalRestocked() {
        return totalRestocked;
    }

    public List<String> getBatchCodesUsed() {
        return batchCodesUsed;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}