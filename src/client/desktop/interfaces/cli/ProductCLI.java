package client.desktop.interfaces.cli;

import server.application.dto.ProductDTO;
import server.application.dto.request.CreateProductRequest;
import server.domain.service.ProductService;
import client.desktop.interfaces.cli.utils.ProductCliFormatter;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ProductCLI {
    private final ProductService productService;

    public ProductCLI(ProductService productService) {
        this.productService = productService;
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== Product Management ===");
            System.out.println("1. Add New Product");
            System.out.println("2. View All Products");
            System.out.println("3. Search Product by Code");
            System.out.println("4. Update Product Details");
            System.out.println("5. Delete Product");
            System.out.println("6. Back to Main Menu");
            System.out.print("Enter choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> addProduct(scanner);
                case 2 -> viewAllProducts();
                case 3 -> searchProduct(scanner);
                case 4 -> updateProduct(scanner);
//                case 5 -> deleteProduct(scanner);
                case 6 -> {
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void addProduct(Scanner scanner) {
        System.out.print("Enter Category Code (e.g., BEV): ");
        String categoryCode = scanner.nextLine().trim().toUpperCase();

        System.out.print("Enter Brand Code (e.g., TRO): ");
        String brandCode = scanner.nextLine().trim().toUpperCase();

        System.out.print("Enter Size Code (e.g., 1L): ");
        String sizeCode = scanner.nextLine().trim().toUpperCase();

        System.out.print("Enter Product Number (e.g., 0012): ");
        String productNo = scanner.nextLine().trim();

        String itemCode = String.format("%s-%s-%s-%s", categoryCode, brandCode, sizeCode, productNo);

        System.out.println("Generated Item Code: " + itemCode);
        System.out.print("Proceed with creation? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        if (!confirm.equals("yes")) {
            System.out.println("Product creation cancelled.");
            return;
        }

        System.out.print("Enter Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter Price: ");
        double price = scanner.nextDouble();
        scanner.nextLine();

        System.out.print("Enter Estimated Reorder Level (optional, press Enter to skip): ");
        String reorderLevelInput = scanner.nextLine().trim();

        Integer reorderLevel = null;
        if (!reorderLevelInput.isEmpty()) {
            try {
                reorderLevel = Integer.parseInt(reorderLevelInput);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Reorder level will be skipped.");
            }
        }


        CreateProductRequest request = new CreateProductRequest(itemCode, name, price, reorderLevel);
        productService.createProduct(request);
        System.out.println("Product added successfully.");
    }


    private void viewAllProducts() {
        List<ProductDTO> products = productService.getAllProducts();
        if (products.isEmpty()) {
            System.out.println("No products found.");
            return;
        }

        ProductCliFormatter.printList(products);
    }

    private void searchProduct(Scanner scanner) {
        System.out.print("Enter Item Code to Search: ");
        String code = scanner.nextLine();

        Optional<ProductDTO> productOpt = productService.getProductByItemCode(code);
        if (productOpt.isEmpty()) {
            System.out.println("Product not found.");
            return;
        }

        ProductCliFormatter.printHeader();
        ProductCliFormatter.print(productOpt.get());
    }

    private void updateProduct(Scanner scanner) {
        System.out.print("Enter Item Code to Update: ");
        String code = scanner.nextLine();

        System.out.print("Enter New Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter New Price: ");
        double price = scanner.nextDouble();
        scanner.nextLine();

        System.out.print("Enter New Estimated Reorder Level (optional, press Enter to skip): ");
        String reorderLevelInput = scanner.nextLine().trim();

        Integer reorderLevel = null;
        if (!reorderLevelInput.isEmpty()) {
            try {
                reorderLevel = Integer.parseInt(reorderLevelInput);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Reorder level will be skipped.");
            }
        }

        CreateProductRequest updated = new CreateProductRequest(code, name, price,reorderLevel);
        Optional<ProductDTO> updatedOpt = productService.updateProduct(updated);

        if (updatedOpt.isPresent()) {
            System.out.println("Product updated successfully:");
            ProductCliFormatter.printHeader();
            ProductCliFormatter.print(updatedOpt.get());
        } else {
            System.out.println("Product not found or update failed.");
        }
    }

//    private void deleteProduct(Scanner scanner) {
//        System.out.print("Enter Item Code to Delete: ");
//        String code = scanner.nextLine();
//
//        boolean deleted = productService.deleteProduct(code);
//
//        if (deleted) {
//            System.out.println(" Product deleted (soft delete).");
//        } else {
//            System.out.println(" Product not found — nothing deleted.");
//        }
//    }

}