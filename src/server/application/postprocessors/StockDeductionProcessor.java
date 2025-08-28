package server.application.postprocessors;

import server.application.postprocessors.interfaces.BillPostProcessor;
import server.domain.entity.Bill;
import server.domain.entity.BillItem;
import server.domain.entity.BillType;
import server.domain.service.ShelfService;
import server.domain.service.WebInventoryService;

import java.sql.Connection;
import java.sql.SQLException;

public class StockDeductionProcessor implements BillPostProcessor {

    private final WebInventoryService webInventoryService;
    private final ShelfService shelfService;

    public StockDeductionProcessor(WebInventoryService webInventoryService,
                                   ShelfService shelfService) {
        this.webInventoryService = webInventoryService;
        this.shelfService = shelfService;
    }

    @Override
    public void process(Bill bill, Connection connection) throws SQLException {

        for (BillItem item : bill.getItems()) {
            String itemCode = item.getProduct().getItemCode();
            int quantity = item.getQuantity();

            switch (bill.getBillType()) {
                case BillType.WEB_BILL -> {
                    webInventoryService.deductStock(connection, itemCode, quantity);
                }
                case BillType.OTC_BILL -> {
                    shelfService.deductStock(connection, itemCode, quantity);
                }
                default -> throw new UnsupportedOperationException("Unsupported transaction type: " + bill.getBillType());
            }
        }
    }
}