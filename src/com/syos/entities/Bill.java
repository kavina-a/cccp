package com.syos.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Bill {
    private int serialNumber;
    private LocalDate billDate;
    private List<BillItem> items = new ArrayList<>();
    private double totalAmount;
    private double discountApplied;
    private double cashTendered;
    private double changeGiven;
    private TransactionType transactionType;
    private Customer customer;
    private Employee processedBy;

    public Bill(int serialNumber, TransactionType type, Customer customer, Employee processedBy) {
        this.serialNumber = serialNumber;
        this.billDate = LocalDate.now();
        this.transactionType = type;
        this.customer = customer;
        this.processedBy = processedBy;
    }

//    public void addItem(BillItem item) {
//        items.add(item);
//        totalAmount += item.lineTotal;
//    }

    public void completePayment(double tendered) {
        this.cashTendered = tendered;
        this.changeGiven = tendered - totalAmount;
    }

    // Getters and setters omitted for brevity
}