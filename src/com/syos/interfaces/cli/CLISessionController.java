package com.syos.interfaces.cli;

import com.syos.application.reports.service.ReportGenerationService;
import com.syos.domain.entity.Employee;
import com.syos.domain.service.*;
import com.syos.domain.service.interfaces.AllocationService;
import com.syos.utils.SessionManager;

import java.sql.SQLException;
import java.util.Scanner;

import static com.syos.interfaces.cli.utils.EntryMenuFormatter.*;

public class CLISessionController {
    private final AuthenticationService authService;
    private final BillingService billingService;
    private final ProductService productService;
    private final ShelfService shelfService;
    private final AllocationService allocationService;
    private final WebInventoryService webInventoryService;
    private final ReportGenerationService reportService;
    private final BusinessDayService businessDayService;

    public CLISessionController(
            AuthenticationService authService,
            BillingService billingService,
            ProductService productService,
            ShelfService shelfService,
            AllocationService allocationService,
            WebInventoryService webInventoryService,
            ReportGenerationService reportService,
            BusinessDayService businessDayService
    ) {
        this.authService = authService;
        this.billingService = billingService;
        this.productService = productService;
        this.shelfService = shelfService;
        this.allocationService = allocationService;
        this.webInventoryService = webInventoryService;
        this.reportService = reportService;
        this.businessDayService = businessDayService;
    }

    public void run() throws SQLException {
        AuthenticationCLI authCLI = new AuthenticationCLI(authService);
        boolean isAuthenticated = authCLI.start();

        if (!isAuthenticated) {
            System.out.println("Authentication failed. Exiting.");
            return;
        }

        Employee employee = SessionManager.getInstance().getLoggedInEmployee();
        if (employee == null) {
            System.out.println("No employee session found. Exiting.");
            return;
        }

        switch (employee.getRole()) {
            case CASHIER -> runCashierCLI(employee);
            case MANAGER -> runManagerCLI(employee);
            default -> System.out.println("Unknown role: " + employee.getRole());
        }

        System.out.println("Session ended.");
    }

    private void runCashierCLI(Employee employee) {
        printHeader(employee, "Cashier");
        new BillingCLI(billingService, productService, authService).start();
    }


    private void runManagerCLI(Employee employee) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            printHeader(employee, "Manager");

            String[] options = {
                    "1. Billing Terminal",
                    "2. Product Catalog Management",
                    "3. Back Store Inventory Management",
                    "4. Shelf Inventory Management",
                    "5. Web Inventory Management",
                    "6. Report Generation",
                    "7. Logout",
                    "#. Run Business Day Operations"
            };
            printMenuBox(options);

            System.out.print("Select an option (1-7): ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> new BillingCLI(billingService, productService, authService).start();
                case "2" -> new ProductCLI(productService).start();
                case "3" -> new StoreStockCLI().start();
                case "4" -> new ShelfCLI(shelfService, allocationService).start();
                case "5" -> new WebInventoryCLI(webInventoryService, allocationService).start();
                case "6" -> new ReportCLI(reportService).start();
                case "7" -> {
                    System.out.println("\nLogging out... Goodbye, " + employee.getUsername() + "!\n");
                    running = false;
                }
                case "#" -> {
                    new BusinessDayCLI(businessDayService).start();
                }
                default -> {
                    System.out.println("\n⚠️ Invalid choice. Please select a valid option (1-6).\n");
                    pauseForInput(scanner);
                }
            }
        }
    }
}