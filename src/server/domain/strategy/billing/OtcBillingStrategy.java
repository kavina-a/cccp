package server.domain.strategy.billing;

import server.domain.entity.Bill;
import server.domain.entity.BillItem;
import server.domain.entity.BillType;
import server.domain.entity.Customer;
import server.application.dto.builder.BillBuilder;
import server.application.dto.request.CreateBillRequest;
import com.syos.domain.entity.*;
import server.domain.strategy.billing.interfaces.BillingStrategy;
import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class OtcBillingStrategy implements BillingStrategy {
    @Override
    public Bill generateBill(CreateBillRequest request, Connection conn, List<BillItem> billItems, Customer customer, int nextSerialNumber) {

        BigDecimal total = billItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal cashTendered = request.getCashTendered();
        if (cashTendered == null || cashTendered.compareTo(total) < 0) {
            throw new IllegalArgumentException("Invalid cash tendered amount for OTC transaction.");
        }

        BigDecimal changeAmount = cashTendered.subtract(total);
        LocalDate today = LocalDate.now();
        LocalDateTime currentDateTime = LocalDateTime.now();

        return new BillBuilder()
                .withSerialNumber(nextSerialNumber)
                .withBillDate(today)
                .withCustomer(customer)
                .withCashTendered(cashTendered)
                .withTotalAmount(total)
                .withChangeAmount(changeAmount)
                .withCreatedAt(currentDateTime)
                .withBillType(BillType.OTC_BILL)
                .withPaymentMethod(request.getPaymentMethod())
                .build();
    }
}