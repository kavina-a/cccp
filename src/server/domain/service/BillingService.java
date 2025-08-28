package server.domain.service;

import server.application.dto.BillReceiptDTO;
import server.application.dto.request.CreateBillRequest;
import server.application.postprocessors.interfaces.BillPostProcessor;
import server.data.dao.interfaces.BillDao;
import server.data.dao.interfaces.BillItemDao;
import server.data.dao.interfaces.CustomerDao;
import server.domain.entity.Bill;
import server.domain.entity.BillItem;
import server.domain.entity.Customer;
import server.domain.entity.TransactionType;
import server.domain.strategy.billing.interfaces.BillingStrategy;
import server.utils.SQLTransactionManager;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BillingService {

    private final BillDao billDao;
    private final BillItemDao billItemDao;
    private final CustomerDao customerDao;
    private final BillItemService billItemService;
    private final Map<TransactionType, BillingStrategy> strategies;
    private final List<BillPostProcessor> postProcessors;

    public BillingService(BillDao billDao, BillItemDao billItemDao, BillItemService billItemService,
                          CustomerDao customerDao, Map<TransactionType, BillingStrategy> strategies, List<BillPostProcessor> postProcessors) {
        this.billDao = billDao;
        this.billItemDao = billItemDao;
        this.billItemService = billItemService;
        this.customerDao = customerDao;
        this.strategies = strategies;
        this.postProcessors = postProcessors;
    }

    public Optional<Bill> findBill(LocalDate billDate, Integer serialNumber) {
        return SQLTransactionManager.execute(connection ->
                billDao.findByBillDateAndSerialNumber(connection, billDate, serialNumber)
        );
    }

    public List<BillReceiptDTO> findBillReceipt(LocalDate billDate, Integer serialNumber) {
        return SQLTransactionManager.execute(connection ->
                billDao.findBillReceipt(connection, billDate, serialNumber)
        );
    }

    public Bill createBill(CreateBillRequest request) {
        return SQLTransactionManager.execute(connection -> {

            Customer customer = request.getCustomer();

            List<BillItem> billItems = billItemService.createBillItems(connection, request.getItems());

            if (request.getItems() == null || request.getItems().isEmpty()) {
                throw new IllegalArgumentException("Item list cannot be null or empty.");
            }

            int nextSerialNumber;
            try {
                nextSerialNumber = billDao.getNextSerialNumber(connection, LocalDate.now());
            } catch (SQLException e) {
                throw new RuntimeException("Failed to retrieve next serial number for billing", e);
            }
            BillingStrategy strategy = strategies.get(request.getTransactionType());

            if (strategy == null) {
                throw new UnsupportedOperationException("No billing strategy defined for " + request.getTransactionType());
            }

            Bill bill = strategy.generateBill(request, connection, billItems, customer, nextSerialNumber);
            bill.setItems(billItems);

            billDao.save(connection, bill);

            for (BillItem item : billItems) {
                item.setBill(bill);
                billItemDao.save(connection, item);
            }

            for (BillPostProcessor processor : postProcessors) {
                try {
                    processor.process(bill, connection);
                } catch (Exception e) {
                    System.err.println("Post-processor failed: " + processor.getClass().getSimpleName());
                    e.printStackTrace();
                }
            }
            System.out.println("Bill processed: " + bill.getTotalAmount());
            return bill;
        });
    }
}