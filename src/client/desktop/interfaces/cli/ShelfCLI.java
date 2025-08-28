package client.desktop.interfaces.cli;

import server.application.dto.AllocatedRestockDTO;
import server.application.dto.ShelfDTO;
import server.application.dto.request.AllocationRequest;
import server.application.dto.request.CreateShelfRequest;
import server.domain.entity.AllocationTarget;
import server.domain.service.ShelfService;
import server.domain.service.interfaces.AllocationService;

import java.util.List;
import java.util.Scanner;

public class ShelfCLI {

    private final ShelfService shelfService;
    private final AllocationService allocationService;

    public ShelfCLI(ShelfService shelfService, AllocationService allocationService
    ) {
        this.shelfService = shelfService;
        this.allocationService = allocationService;
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== Shelf Restocking ===");

            System.out.println("1. Create Shelf");
            System.out.println("2. Allocate Stock's to Shelf");
            System.out.println("3. Back");
            System.out.print("Enter choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> createShelf(scanner);
                case 2 -> allocateToShelf(scanner);
                case 3 -> {
                    System.out.println("Returning to previous menu...");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void createShelf(Scanner scanner) {
        System.out.println("\n--- Create New Shelf ---");

        System.out.println("Enter Shelf ID: ");
        String shelfId = scanner.nextLine().trim();

        System.out.print("Enter Shelf Location: ");
        String location = scanner.nextLine().trim();

        System.out.println("Enter Shelf Capacity: ");
        int quantityOnShelf = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Enter Product Code: ");
        String productCode = scanner.nextLine().trim();


        CreateShelfRequest request = new CreateShelfRequest(shelfId, productCode, quantityOnShelf, location);

        try {
            ShelfDTO created = shelfService.createShelf(request);
            if (created.isSuccess()) {
                System.out.println("✅ Shelf created successfully!");
                System.out.println("--- Shelf Details ---");
                System.out.println("Shelf ID       : " + created.getShelfId());
                System.out.println("Product Code   : " + created.getItemCode());
                System.out.println("Quantity on Shelf : " + created.getQuantityOnShelf());
                System.out.println("Updated At     : " + created.getUpdatedAt());
            } else {
                System.out.println("⚠️ Failed to create shelf: " + created.getMessage());
            }
        } catch (Exception e) {
            System.out.println("❌ Error creating shelf: " + e.getMessage());
        }
    }

    private void allocateToShelf(Scanner scanner) {
        System.out.println("\n--- Allocate Item to Shelf ---");

        System.out.print("Enter Shelf ID: ");
        String shelfId = scanner.nextLine().trim();

        System.out.print("Enter Item Code: ");
        String itemCode = scanner.nextLine().trim();

        System.out.print("Enter Quantity to Allocate to Shelf: ");
        int quantity = scanner.nextInt();
        scanner.nextLine();


        AllocationRequest request = new AllocationRequest(itemCode, quantity, AllocationTarget.SHELF);
        request.setShelfId(shelfId);

        try {
            List<AllocatedRestockDTO> results = allocationService.allocateToShelf(request);

            if (results.isEmpty()) {
                System.out.println("⚠️ No allocations returned.");
                return;
            }

            System.out.println("\n✅ Allocation to Shelf Successful:");
            for (AllocatedRestockDTO dto : results) {
                System.out.printf("Item: %s | Batch: %s | Quantity: %d%n",
                        dto.getItemCode(), dto.getBatchCode(), dto.getAllocatedQuantity());
            }

        } catch (Exception e) {
            System.out.println("❌ Allocation failed: " + e.getMessage());
        }
    }
}