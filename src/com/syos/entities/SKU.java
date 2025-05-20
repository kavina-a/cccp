package com.syos.entities;

public class SKU {
    private final String value;

    public SKU(String categoryCode, int serial) {
        this.value = String.format("%s-%04d", categoryCode.toUpperCase(), serial);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}