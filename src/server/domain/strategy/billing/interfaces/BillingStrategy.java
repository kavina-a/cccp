package server.domain.strategy.billing.interfaces;

import server.application.dto.request.CreateBillRequest;
import server.domain.entity.Bill;
import server.domain.entity.BillItem;
import server.domain.entity.Customer;

import java.sql.Connection;
import java.util.List;

public interface BillingStrategy {
    Bill generateBill(CreateBillRequest request, Connection conn, List<BillItem> billItems, Customer customer, int nextSerialNumber);
}