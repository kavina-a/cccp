package client.desktop.interfaces.cli;

import server.application.dto.AllocatedRestockDTO;
import server.application.dto.WebProductDTO;
import server.application.dto.request.AllocationRequest;
import server.application.dto.request.CreateWebInventoryRequest;
import server.domain.entity.AllocationTarget;
import server.domain.service.interfaces.AllocationService;
import server.domain.service.WebInventoryService;
import client.desktop.interfaces.cli.utils.WebProductCliFormatter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class WebInventoryCLI {
    private final WebInventoryService webInventoryService;
    private final AllocationService allocationService;

    public WebInventoryCLI(WebInventoryService webInventoryService, AllocationService allocationService) {
        this.webInventoryService = webInventoryService;
        this.allocationService = allocationService;
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== Web Inventory Management ===");
            System.out.println("1. Add New Web Product");
            System.out.println("2. View All Web Products");
            System.out.println("3. Restock Web Inventory");
//            System.out.println("4. Delete Web Product");
            System.out.println("4. Exit");

            System.out.print("Enter choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> addProduct(scanner);
                case 2 -> viewAllProducts();
                case 3 -> allocateStockToWeb(scanner);
//                case 4 -> {
//                }
                case 4 -> {
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void addProduct(Scanner scanner) {
        System.out.print("Enter Item Code: ");
        String code = scanner.nextLine();

        System.out.print("Enter Item Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter Price: ");
        BigDecimal price = scanner.nextBigDecimal();

        scanner.nextLine();

        CreateWebInventoryRequest request = new CreateWebInventoryRequest();
        request.setItemCode(code);
        request.setItemName(name);
        request.setPrice(price);
//        request.setQuantity(null);

        WebProductDTO created = webInventoryService.createWebProduct(request);
        System.out.println("Web product created successfully:");
        WebProductCliFormatter.printHeader();
        WebProductCliFormatter.print(created);
    }

    private void viewAllProducts() {
        List<WebProductDTO> products = webInventoryService.getAllWebItems();
        if (products.isEmpty()) {
            System.out.println("No web products found.");
            return;
        }

        WebProductCliFormatter.printList(products);
    }

    private void allocateStockToWeb(Scanner scanner) {
        System.out.print("Enter Item Code to Allocate: ");
        String itemCode = scanner.nextLine();

        System.out.print("Enter Quantity to Allocate: ");
        int quantity = scanner.nextInt();
        scanner.nextLine();

        AllocationRequest request = new AllocationRequest(itemCode, quantity, AllocationTarget.WEB);

        try {
            List<AllocatedRestockDTO> results = allocationService.allocateToWeb(request);

            if (results.isEmpty()) {
                System.out.println("⚠️ No allocations returned.");
                return;
            }

            System.out.println("\n✅ Allocation to Web Successful:");
            for (AllocatedRestockDTO dto : results) {
                System.out.printf("Item: %s | Batch: %s | Quantity: %d%n",
                        dto.getItemCode(), dto.getBatchCode(), dto.getAllocatedQuantity());
            }

        } catch (Exception e) {
            System.out.println("❌ Allocation failed: " + e.getMessage());
        }
    }

}