package com.syos.domain.strategy.billing.interfaces;

import com.syos.application.dto.request.CreateBillRequest;
import com.syos.domain.entity.Bill;
import com.syos.domain.entity.BillItem;
import com.syos.domain.entity.Customer;

import java.sql.Connection;
import java.util.List;

public interface BillingStrategy {
    Bill generateBill(CreateBillRequest request, Connection conn, List<BillItem> billItems, Customer customer, int nextSerialNumber);
}