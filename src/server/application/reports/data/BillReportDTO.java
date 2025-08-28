package server.application.reports.data;

import java.math.BigDecimal;

public class BillReportDTO {
    private String billIdentifier; // combination of serialNumber + billDate
    private String customerId;
    private String transactionType;
    private int totalItems;
    private BigDecimal totalAmount;
    private BigDecimal cashTendered;
    private BigDecimal changeAmount;

    public BillReportDTO(String billIdentifier, String customerId, String transactionType, int totalItems,
                         BigDecimal totalAmount, BigDecimal cashTendered, BigDecimal changeAmount) {
        this.billIdentifier = billIdentifier;
        this.customerId = customerId;
        this.transactionType = transactionType;
        this.totalItems = totalItems;
        this.totalAmount = totalAmount;
        this.cashTendered = cashTendered;
        this.changeAmount = changeAmount;
    }

    public String getBillIdentifier() {
        return billIdentifier;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public BigDecimal getCashTendered() {
        return cashTendered;
    }

    public BigDecimal getChangeAmount() {
        return changeAmount;
    }
}