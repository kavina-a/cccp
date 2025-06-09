package com.syos.application.dto.request;

import com.syos.domain.entity.Customer;
import com.syos.domain.entity.PaymentMethod;
import com.syos.domain.entity.TransactionType;
import jakarta.persistence.criteria.CriteriaBuilder;

import java.math.BigDecimal;
import java.util.List;

public class CreateBillRequest {

    private List<BillItemRequest> items;
    private BigDecimal cashTendered;
    private Customer customer;
    private TransactionType transactionType;
    private PaymentMethod paymentMethod;

    public CreateBillRequest(List<BillItemRequest> items, Customer customer, BigDecimal cashTendered, TransactionType transactionType, PaymentMethod paymentMethod) {
        this.items = items;
        this.cashTendered = cashTendered;
        this.customer = customer;
        this.transactionType = transactionType;
        this.paymentMethod = paymentMethod;
    }

    public CreateBillRequest() {

    }

    public List<BillItemRequest> getItems() {
        return items;
    }

    public void setItems(List<BillItemRequest> items) {
        this.items = items;
    }

    public BigDecimal getCashTendered() {
        return cashTendered;
    }

    public void setCashTendered(BigDecimal cashTendered) {
        this.cashTendered = cashTendered;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

}
