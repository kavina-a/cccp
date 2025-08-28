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
                .withCustomer(customer)
                .withTotalAmount(total)
                .withCashTendered(null)
                .withChangeAmount(null)
                .withCreatedAt(currentDateTime)
                .withBillType(BillType.WEB_BILL)
                .withPaymentMethod(request.getPaymentMethod())
                .build();
    }
}
