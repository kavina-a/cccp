package server.application.dto.builder;

import server.domain.entity.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BillBuilder {

    private Integer serialNumber;
    private LocalDate billDate;
    private Customer customer;
    private BigDecimal cashTendered;
    private BigDecimal totalAmount;
    private BigDecimal changeAmount;
    private List<BillItem> billItems;
    private LocalDateTime createdAt;
    private BillType billType;
    private PaymentMethod paymentMethod;

    public BillBuilder withSerialNumber(Integer serialNumber) {
        this.serialNumber = serialNumber;
        return this;
    }

    public BillBuilder withBillDate(LocalDate billDate) {
        this.billDate = billDate;
        return this;
    }

    public BillBuilder withCustomer(Customer customer) {
        this.customer = customer;
        return this;
    }

    public BillBuilder withCashTendered(BigDecimal cashTendered) {
        this.cashTendered = cashTendered;
        return this;
    }

    public BillBuilder withTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
        return this;
    }

    public BillBuilder withChangeAmount(BigDecimal changeAmount) {
        this.changeAmount = changeAmount;
        return this;
    }

    public BillBuilder withBillItems(List<BillItem> billItems) {
        this.billItems = billItems;
        return this;
    }

    public BillBuilder withCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public BillBuilder withBillType(BillType billType) {
        this.billType = billType;
        return this;
    }

    public BillBuilder withPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
        return this;
    }


    /**
     * Builds a Bill object with the provided parameters.
     *
     * @return a Bill object
     */
    public Bill build() {
        Bill bill = new Bill();
        bill.setItems(billItems != null ? billItems : new ArrayList<>());
        bill.setSerialNumber(serialNumber);
        bill.setBillDate(billDate != null ? billDate : LocalDate.now());
        bill.setCustomer(customer);
        bill.setCashTendered(cashTendered);
        bill.setTotalAmount(totalAmount);
        bill.setChangeAmount(changeAmount != null ? changeAmount : BigDecimal.ZERO);
        bill.setCreatedAt(createdAt != null ? createdAt : LocalDateTime.now());
        bill.setBillType(billType);
        bill.setPaymentMethod(paymentMethod);
        return bill;
    }
}