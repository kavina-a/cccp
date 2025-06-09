package com.syos.application.reports.data;


import java.math.BigDecimal;

public class TotalSaleReportDTO {
    private String itemCode;
    private String itemName;
    private int totalQuantity;
    private BigDecimal totalRevenue;

    public TotalSaleReportDTO(String itemCode, String itemName, int totalQuantity, BigDecimal totalRevenue) {
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.totalQuantity = totalQuantity;
        this.totalRevenue = totalRevenue;
    }

    public String getItemCode() {
        return itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }
}