package com.syos.interfaces.cli;

import com.syos.application.reports.service.ReportGenerationService;
import com.syos.domain.entity.ReportType;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Scanner;

public class ReportCLI {

    private final ReportGenerationService reportService;

    public ReportCLI(ReportGenerationService reportService) {
        this.reportService = reportService;
    }

    public void start() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n===== REPORT MENU =====");
            System.out.println("1. Total Sales Report");
            System.out.println("2. Reshelving Report");
            System.out.println("3. Reorder Level Report");
            System.out.println("4. Stock Report");
            System.out.println("5. Bill Report");
            System.out.println("6. Exit");
            System.out.print("Select an option: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1 -> runTotalSalesReport(scanner);         // a. Total sales
                case 2 -> reportService.generateReshelfReport(); // b. Reshelving
                case 3 -> reportService.generateRestockReport(); // c. Reorder level (Restock)
                case 4 -> reportService.generateStockReport();   // d. Stock Report
                case 5 -> runBillReport(scanner);                // e. Bill Report
                case 6 -> {
                    System.out.println("Exiting report menu.");
                    return;
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    private void runTotalSalesReport(Scanner scanner) throws SQLException {
        System.out.print("Enter date (YYYY-MM-DD): ");
        LocalDate date = LocalDate.parse(scanner.nextLine());

        ReportType type = selectTransactionType(scanner);
        if (type != null) {
            reportService.generateTotalSaleReport(date, type);
        }
    }

    private void runBillReport(Scanner scanner) throws SQLException {
        ReportType type = selectTransactionType(scanner);
        if (type != null) {
            reportService.generateBillReport(type);
        }
    }

    private ReportType selectTransactionType(Scanner scanner) {
        System.out.println("Select transaction type:");
        System.out.println("1. OTC");
        System.out.println("2. WEB");
        System.out.println("3. Combined");
        System.out.print("Your choice: ");

        int typeChoice = Integer.parseInt(scanner.nextLine());
        return switch (typeChoice) {
            case 1 -> ReportType.OTC;
            case 2 -> ReportType.WEB;
            case 3 -> ReportType.COMBINED;
            default -> {
                System.out.println("Invalid transaction type.");
                yield null;
            }
        };
    }
}