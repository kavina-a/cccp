package com.syos.interfaces.cli.customer;

import com.syos.application.dto.WebProductDTO;
import com.syos.application.dto.request.BillItemRequest;
import com.syos.application.dto.request.CreateBillRequest;
import com.syos.domain.entity.Bill;
import com.syos.domain.entity.Customer;
import com.syos.domain.entity.PaymentMethod;
import com.syos.domain.entity.TransactionType;
import com.syos.domain.service.BillingService;
import com.syos.domain.service.ProductService;
import com.syos.domain.service.WebInventoryService;
import com.syos.utils.SessionManager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class CustomerPurchaseCLI {

    private final BillingService billingService;
    private final ProductService ProductService;
    private final WebInventoryService webInventoryService;

//    private final int customerId;
//    private final Scanner scanner = new Scanner(System.in);
//    private final BillingService billingService = new BillingService();

    public CustomerPurchaseCLI(BillingService billingService, ProductService ProductService, WebInventoryService webInventoryService) {
        this.billingService = billingService;
        this.ProductService = ProductService;
        this.webInventoryService = webInventoryService;
    }

    public void createWebBill(Scanner scanner) {
        System.out.println("======================================");
        System.out.println("        SYOS Web Purchase CLI         ");
        System.out.println("======================================");

        Customer loggedInCustomer = SessionManager.getInstance().getLoggedInCustomer();
        if (loggedInCustomer == null) {
            System.out.println("❌ You must be logged in as a customer to place a web order.");
            return;
        }

        List<BillItemRequest> billItems = new ArrayList<>();
        BigDecimal totalBillAmount = BigDecimal.ZERO;

        while (true) {
            System.out.println("\n--- Add Item to Bill ---");
            System.out.print("Enter Product Code (or 'done' to finish): ");
            String productCode = scanner.nextLine().trim();

            if (productCode.equalsIgnoreCase("done")) break;
            if (productCode.isEmpty()) {
                System.out.println("⚠️ Product code cannot be empty.");
                continue;
            }

            // Check product availability
            Optional<WebProductDTO> optionalProduct = webInventoryService.getAvailableProduct(productCode);
            if (optionalProduct.isEmpty()) {
                System.out.println("❌ Product not found or is inactive.");
                continue;
            }

            int availableQty = webInventoryService.getAvailableQuantity(productCode);
            if (availableQty <= 0) {
                System.out.println("❌ Product is out of stock.");
                continue;
            }

            System.out.println("✅ Available quantity: " + availableQty);

            // Get valid quantity input
            int quantity = 0;
            while (true) {
                System.out.print("Enter Quantity: ");
                String qtyInput = scanner.nextLine().trim();
                try {
                    quantity = Integer.parseInt(qtyInput);
                    if (quantity <= 0 || quantity > availableQty) {
                        throw new IllegalArgumentException();
                    }
                    break;
                } catch (Exception ex) {
                    System.out.printf("⚠️ Invalid input. Enter a number between 1 and %d.\n", availableQty);
                }
            }

            // Create and add item
            WebProductDTO product = optionalProduct.get();
            BigDecimal pricePerItem = product.getPrice();
            BigDecimal itemTotal = pricePerItem.multiply(BigDecimal.valueOf(quantity));

            billItems.add(new BillItemRequest(productCode, quantity, pricePerItem));
            totalBillAmount = totalBillAmount.add(itemTotal);

            // Ask to continue
            System.out.print("Add another item? (yes/no): ");
            String another = scanner.nextLine().trim();
            if (another.equalsIgnoreCase("no")) break;
        }

        // Final validation and billing
        if (billItems.isEmpty()) {
            System.out.println("⚠️ No items added. Bill canceled.");
            return;
        }

        CreateBillRequest billRequest = new CreateBillRequest(billItems, loggedInCustomer, null, TransactionType.WEB, PaymentMethod.COD);
        Bill bill = billingService.createBill(billRequest);

        System.out.println("\n✅ Web Order submitted successfully!");
        System.out.println("🧾 Order Total: " + totalBillAmount);
        System.out.println("📄 You can view this bill later using the 'View Bill' option.");
    }

//    public void start() {
//        Scanner scanner = new Scanner(System.in);
//
//        while (true) {
//            System.out.println("\n=== 🛒 Welcome to SYOS Web Store ===");
//            System.out.println("1. Browse & Buy Products");
//            System.out.println("2. View Bill by ID");
//            System.out.println("3. Exit");
//            System.out.print("Choose an option: ");
//
//            if (!scanner.hasNextInt()) {
//                System.out.println("Invalid input. Please enter a number.");
//                scanner.next();
//                continue;
//            }
//
//            int choice = scanner.nextInt();
//            scanner.nextLine(); // consume newline
//
//            switch (choice) {
//                case 1 -> createWebBill(scanner);
////                case 2 -> viewBill(scanner);
//                case 3 -> {
//                    System.out.println("Thank you for shopping at SYOS!");
//                    return;
//                }
//                default -> System.out.println("Invalid option. Try again.");
//            }
//        }
//    }

//    public void createWebBill(Scanner scanner) {
//        System.out.println("======================================");
//        System.out.println("        SYOS Web Purchase CLI         ");
//        System.out.println("======================================");
//
//        Customer loggedInCustomer = SessionManager.getInstance().getLoggedInCustomer();
//        if (loggedInCustomer == null) {
//            System.out.println("You must be logged in as a customer to place a web order.");
//            return;
//        }
//
//        List<BillItemRequest> billItems = new ArrayList<>();
//        BigDecimal totalBillAmount = BigDecimal.ZERO;
//
//        while (true) {
//            System.out.println("\n--- Add Item to Bill ---");
//            System.out.print("Enter Product Code (or 'done' to finish): ");
//            String productCode = scanner.nextLine().trim();
//            if (productCode.equalsIgnoreCase("done")) break;
//            if (productCode.isEmpty()) {
//                System.out.println("Product code cannot be empty.");
//                continue;
//            }
//
//            Optional<WebProductDTO> optionalProduct = webInventoryService.getAvailableProduct(productCode);
//            int availableQty = webInventoryService.getAvailableQuantity(productCode);
//
//            if (availableQty <= 0) {
//                System.out.println("❌ Product is out of stock.");
//                continue;
//            }
//
//            System.out.println("✅ Available quantity: " + availableQty);
//
//            int quantity = 0;
//            while (true) {
//                System.out.print("Enter Quantity: ");
//                String qtyInput = scanner.nextLine().trim();
//                try {
//                    quantity = Integer.parseInt(qtyInput);
//                    if (quantity <= 0 || quantity > availableQty) {
//                        throw new IllegalArgumentException();
//                    }
//                    break;
//                } catch (Exception ex) {
//                    System.out.printf("Invalid input. Enter a number between 1 and %d.\n", availableQty);
//                }
//            }
//
//            if (optionalProduct.isEmpty()) {
//                System.out.println("❌ Product not found or is inactive.");
//                continue;
//            }
//
//
//
//            WebProductDTO product = optionalProduct.get();
//
//            int quantity = 0;
//            while (true) {
//                System.out.print("Enter Quantity: ");
//                String qtyInput = scanner.nextLine().trim();
//                try {
//                    quantity = Integer.parseInt(qtyInput);
//                    if (quantity <= 0) throw new NumberFormatException();
//                    break;
//                } catch (NumberFormatException ex) {
//                    System.out.println("Invalid input. Please enter a positive integer quantity.");
//                }
//            }
//
//            BigDecimal pricePerItem = product.getPrice();
//
//            if (pricePerItem == null) {
//                System.out.println("Product not found. Please try again.");
//                continue;
//            }
//
//            BillItemRequest billItem = new BillItemRequest(productCode, quantity, pricePerItem);
//            billItems.add(billItem);
//
//            BigDecimal itemTotal = pricePerItem.multiply(BigDecimal.valueOf(quantity));
//            totalBillAmount = totalBillAmount.add(itemTotal);
//
//            System.out.print("Add another item? (yes/no): ");
//            String another = scanner.nextLine().trim();
//            if (another.equalsIgnoreCase("no")) break;
//        }
//
//        if (billItems.isEmpty()) {
//            System.out.println("No items added. Bill canceled.");
//            return;
//        }
//
//        CreateBillRequest billRequest = new CreateBillRequest(billItems, loggedInCustomer, null, TransactionType.WEB);
//        Bill bill = billingService.createBill(billRequest);
//
//        System.out.println("\nWeb Order submitted successfully!");
//        System.out.println("Order Total: " + totalBillAmount);
//        System.out.println("You can view this bill later using the View Bill option.");
//    }
}

