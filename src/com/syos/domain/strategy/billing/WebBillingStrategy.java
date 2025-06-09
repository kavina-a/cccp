package com.syos.domain.strategy.billing;

import com.syos.application.dto.builder.BillBuilder;
import com.syos.application.dto.request.CreateBillRequest;
import com.syos.domain.entity.*;
import com.syos.domain.strategy.billing.interfaces.BillingStrategy;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


public class WebBillingStrategy implements BillingStrategy {
    @Override
    public Bill generateBill(CreateBillRequest request, Connection conn, List<BillItem> billItems, Customer customer, int nextSerialNumber) {

        BigDecimal total = billItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LocalDate today = LocalDate.now();
        LocalDateTime currentDateTime = LocalDateTime.now();

        return new BillBuilder()
                .withSerialNumber(nextSerialNumber)
                .withBillDate(today)
                .withCustomer(customer)  // customer required or nullable based on your future design
                .withTotalAmount(total)
                .withCashTendered(null) // explicitly null or optional based on future logic
                .withChangeAmount(null)       // explicitly null
                .withCreatedAt(currentDateTime)
                .withBillType(BillType.WEB_BILL)
                .withPaymentMethod(request.getPaymentMethod())
                .build();
    }
}
