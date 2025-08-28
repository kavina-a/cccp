package client.desktop.interfaces.cli;

import server.application.dto.StoreStockDTO;
import server.application.dto.request.CreateStockRequest;
import server.domain.service.StoreService;
import client.desktop.interfaces.cli.utils.StockStockCliFormatter;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class StoreStockCLI {

    private final StoreService storeService;

    public StoreStockCLI() {
        this.storeService = new StoreService();
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== Store Stock Management ===");
            System.out.println("1. Insert Store Stock");
            System.out.println("2. View Store Stock");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> insertStoreStock(scanner);
                case 2 -> viewStoreStock(scanner);
                case 3 -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void insertStoreStock(Scanner scanner) {
        System.out.print("Enter Item Code: ");
        String itemCode = scanner.nextLine().trim();

        System.out.print("Enter Batch Code: ");
        String batchCode = scanner.nextLine().trim();

        System.out.print("Enter Quantity Received: ");
        int quantity;
        try {
            quantity = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid quantity. Aborting.");
            return;
        }

        System.out.print("Enter Expiry Date (yyyy-MM-dd): ");
        String expiryInput = scanner.nextLine().trim();
        LocalDate expiryDate;
        try {
            expiryDate = LocalDate.parse(expiryInput);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Use yyyy-MM-dd.");
            return;
        }

        CreateStockRequest request = new CreateStockRequest(
                itemCode,
                batchCode,
                quantity,
                expiryDate.atStartOfDay()
        );

        try {
            storeService.insertStoreStock(request);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Failed to insert store stock: " + e.getMessage());
        }
    }

    private void viewStoreStock(Scanner scanner) {
        System.out.print("Enter Item Code: ");
        String itemCode = scanner.nextLine();

        List<StoreStockDTO> result = storeService.getStoreStockDetails(itemCode);

        if (result.isEmpty()) {
            System.out.println("No stock found for given Item Code.");
            return;
        }

        System.out.println("\n📦 Store Stock Details:");
        StockStockCliFormatter.printList(result);
    }
}
