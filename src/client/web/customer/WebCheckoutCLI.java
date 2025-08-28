//package com.syos.interfaces.cli.customer;
//
//import com.syos.application.dto.request.BillItemRequest;
//import com.syos.application.dto.request.CreateBillRequest;
//import com.syos.domain.entity.Bill;
//import com.syos.domain.entity.Customer;
//import com.syos.domain.entity.TransactionType;
//import com.syos.domain.service.BillingService;
//import com.syos.domain.service.ProductService;
//import com.syos.utils.SessionManager;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Scanner;
//
//public class WebCheckoutCLI {
//
//    private final BillingService billingService;
//    private final ProductService ProductService;
//
//    public WebCheckoutCLI(BillingService billingService, ProductService ProductService) {
//        this.billingService = billingService;
//        this.ProductService = ProductService;
//    }
//
//    public void start() {
//        Scanner scanner = new Scanner(System.in);
//
//        while (true) {
//            System.out.println("\n=== SYOS Web Checkout ===");
//            System.out.println("1. Place New Web Order");
//            System.out.println("2. Exit");
//            System.out.print("Enter your choice: ");
//
//            if (!scanner.hasNextInt()) {
//                System.out.println("Invalid input. Please enter a number.");
//                scanner.next();
//                continue;
//            }
//
//            int choice = scanner.nextInt();
//            scanner.nextLine();
//
//            switch (choice) {
//                case 1 -> createWebBill(scanner);
//                case 2 -> {
//                    System.out.println("Exiting Web Checkout. Thank you for using SYOS.");
//                    return;
//                }
//                default -> System.out.println("Invalid choice. Please try again.");
//            }
//        }
//    }
//
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
//            Double pricePerItem = ProductService.getProductPriceByCode(productCode);
//            if (pricePerItem == null) {
//                System.out.println("Product not found. Please try again.");
//                continue;
//            }
//
//            BillItemRequest billItem = new BillItemRequest(productCode, quantity, BigDecimal.valueOf(pricePerItem));
//            billItems.add(billItem);
//
//            BigDecimal itemTotal = BigDecimal.valueOf(pricePerItem).multiply(BigDecimal.valueOf(quantity));
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
//}
