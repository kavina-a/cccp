package server.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Bill {

    private int id;
    private int serialNumber;
    private LocalDate billDate;
    private BigDecimal totalAmount;
    private BigDecimal cashTendered;
    private BigDecimal changeAmount;
    private LocalDateTime createdAt = LocalDateTime.now();
    private BillType billType;
    private List<BillItem> items = new ArrayList<>();
    private Customer customer;
    private PaymentMethod paymentMethod;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getSerialNumber() { return serialNumber; }
    public void setSerialNumber(int serialNumber) { this.serialNumber = serialNumber; }

    public LocalDate getBillDate() { return billDate; }
    public void setBillDate(LocalDate billDate) { this.billDate = billDate; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public BigDecimal getCashTendered() { return cashTendered; }
    public void setCashTendered(BigDecimal cashTendered) { this.cashTendered = cashTendered; }

    public BigDecimal getChangeAmount() { return changeAmount; }

    public void setChangeAmount(BigDecimal changeAmount) { this.changeAmount = changeAmount; }

    public List<BillItem> getItems() { return items; }
    public void setItems(List<BillItem> items) { this.items = items; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public BillType getBillType() { return billType; }
    public void setBillType(BillType billType) {
        this.billType = billType;
    }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void addItem(BillItem item) {
        if (items == null) items = new ArrayList<>();
        item.setBill(this);
        items.add(item);
    }
}