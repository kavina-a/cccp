package com.syos.interfaces.cli;

import com.syos.application.dto.ProductDTO;
import com.syos.application.dto.request.CreateProductRequest;
import com.syos.domain.service.ProductService;
import com.syos.utils.ProductCliFormatter;

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
                case 5 -> deleteProduct(scanner);
                case 6 -> {
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void addProduct(Scanner scanner) {
        System.out.print("Enter Item Code: ");
        String code = scanner.nextLine();

        System.out.print("Enter Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter Price: ");
        double price = scanner.nextDouble();
        scanner.nextLine();

        CreateProductRequest request = new CreateProductRequest(code, name, price);
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

        CreateProductRequest updated = new CreateProductRequest(code, name, price);
        Optional<ProductDTO> updatedOpt = productService.updateProduct(updated);

        if (updatedOpt.isPresent()) {
            System.out.println("Product updated successfully:");
            ProductCliFormatter.printHeader();
            ProductCliFormatter.print(updatedOpt.get());
        } else {
            System.out.println("Product not found or update failed.");
        }
    }

    private void deleteProduct(Scanner scanner) {
        System.out.print("Enter Item Code to Delete: ");
        String code = scanner.nextLine();

        boolean deleted = productService.deleteProduct(code);

        if (deleted) {
            System.out.println(" Product deleted (soft delete).");
        } else {
            System.out.println(" Product not found — nothing deleted.");
        }
    }

}