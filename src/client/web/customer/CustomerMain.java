package client.web.customer;

import server.application.postprocessors.StockDeductionProcessor;
import server.data.dao.*;
import server.data.dao.interfaces.*;
import server.domain.entity.Customer;
import server.domain.entity.TransactionType;
import server.domain.notifications.StockEventPublisher;
import server.domain.service.*;
import server.domain.strategy.billing.OtcBillingStrategy;
import server.domain.strategy.billing.WebBillingStrategy;
import server.domain.strategy.billing.interfaces.BillingStrategy;
import server.domain.strategy.stockselection.FEFOStockSelectionStrategy;
import server.domain.strategy.stockselection.interfaces.StockSelectionStrategy;
import server.utils.SessionManager;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class CustomerMain {

    public static void main(String[] args) {

        Map<TransactionType, BillingStrategy> strategyMap = Map.of(
                TransactionType.OTC, new OtcBillingStrategy(),
                TransactionType.WEB, new WebBillingStrategy()
        );

        StockEventPublisher stockEventPublisher = new StockEventPublisher();

        BillDao billDao = new BillDaoImpl();
        BillItemDao billItemDao = new BillItemDaoImpl();
        ProductDaoImpl productDao = new ProductDaoImpl();
        CustomerDaoImpl customerDao = new CustomerDaoImpl();
        EmployeeDaoImpl employeeDao = new EmployeeDaoImpl();
        StockSelectionStrategy stockSelectionStrategy = new FEFOStockSelectionStrategy();
        ShelfDao shelfDao = new ShelfDaoImpl();
        StoreDao storeDao = new StoreDaoImpl();
        WebInventoryDao webInventoryDao = new WebInventoryDaoImpl();



        AuthenticationService authService = new AuthenticationService(customerDao,employeeDao);
        CustomerAuthenticationCLI authCLI = new CustomerAuthenticationCLI(authService);
        BillItemService billItemService = new BillItemService(productDao);
        ProductService productService = new ProductService(productDao);
        ShelfService shelfService = new ShelfService(shelfDao, storeDao, stockEventPublisher);
        WebInventoryService webInventoryService = new WebInventoryService(webInventoryDao);
        StockDeductionProcessor stockDeductionProcessor = new StockDeductionProcessor(webInventoryService, shelfService);

        BillingService billingService = new BillingService(billDao, billItemDao, billItemService, customerDao, strategyMap, List.of(stockDeductionProcessor));

        boolean authenticated = authCLI.start();
        if (!authenticated) {
            System.out.println("Exiting the system. Please try again later.");
            return;
        }

        Customer customer = SessionManager.getInstance().getLoggedInCustomer();
        String customerId = customer.getCustomerID();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== SYOS Customer Menu ===");
            System.out.println("1. Browse & Buy Products");
            System.out.println("2. View All Products");
            System.out.println("3. View Bill by ID");
            System.out.println("4. Logout");
            System.out.print("Enter your option: ");

            int option;
            while (!scanner.hasNextInt()) {
                System.out.print("Please enter a valid number: ");
                scanner.next();
            }
            option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1 -> {
                    new CustomerPurchaseCLI(billingService, productService, webInventoryService).createWebBill(scanner);
                }
                case 2 -> {
                    System.out.println("Feature not implemented yet.");
//                    new PurchaseCLI(customerId).start();
                }
                case 3 -> {
                    System.out.println("Logged out successfully. Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }
}