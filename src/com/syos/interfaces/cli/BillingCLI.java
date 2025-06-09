package com.syos.interfaces.cli;

import com.syos.application.dto.BillReceiptDTO;
import com.syos.application.dto.PaymentResultDTO;
import com.syos.application.dto.request.BillItemRequest;
import com.syos.application.dto.request.CreateBillRequest;
import com.syos.domain.entity.Customer;
import com.syos.domain.service.AuthenticationService;
import com.syos.domain.strategy.payment.CashPaymentStrategy;
import com.syos.domain.strategy.payment.intefaces.PaymentStrategy;
import com.syos.interfaces.cli.utils.BillReceiptFormatter;
import com.syos.domain.entity.Bill;
import com.syos.domain.entity.TransactionType;
import com.syos.domain.service.BillingService;
import com.syos.domain.service.ProductService;
import com.syos.interfaces.cli.utils.EntryMenuFormatter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BillingCLI {
    private final BillingService billingService;
    private final ProductService ProductService;
    private final AuthenticationService authenticationService;

    public BillingCLI(BillingService billingService, ProductService ProductService,
                      AuthenticationService authenticationService) {
        this.billingService = billingService;
        this.ProductService = ProductService;
        this.authenticationService = authenticationService;
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== Billing System ===");
            System.out.println("1. Create Bill");
            System.out.println("2. View Bill (by ID)");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");

            if (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next();
                continue;
            }

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> createBill(scanner);
                case 2 -> viewBill(scanner);
                case 3 -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    public void createBill(Scanner scanner) {
        try {
            System.out.println("======================================");
            System.out.println("      SYOS Point-of-Sale Terminal     ");
            System.out.println("======================================");

            List<BillItemRequest> billItems = new ArrayList<>();
            BigDecimal totalBillAmount = BigDecimal.ZERO;

            Customer customer = null;
            while (true) {
                System.out.print("Enter Customer ID (leave blank for WALK-IN): ");
                String customerId = scanner.nextLine().trim();

                if (customerId.isEmpty()) {
                    break; // Proceed as WALK-IN
                }

                try {
                    customer = authenticationService.validateCustomerById(customerId)
                            .orElseThrow(() -> new IllegalArgumentException("Customer not found."));
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println("❌ " + e.getMessage());
                } catch (RuntimeException e) {
                    System.out.println("❌ Internal error validating customer. Try again.");
                    System.err.println("[LOG] " + e.getMessage());
                }
            }


            while (true) {
                System.out.println("\n--- Add Item to Bill ---");
                System.out.print("Enter Product Code (or 'done' to finish): ");
                String productCode = scanner.nextLine().trim();
                if (productCode.equalsIgnoreCase("done")) break;
                if (productCode.isEmpty()) {
                    System.out.println("Product code cannot be empty.");
                    continue;
                }

                int quantity = 0;
                while (true) {
                    System.out.print("Enter Quantity: ");
                    String qtyInput = scanner.nextLine().trim();
                    try {
                        quantity = Integer.parseInt(qtyInput);
                        if (quantity <= 0) throw new NumberFormatException();
                        break;
                    } catch (NumberFormatException ex) {
                        System.out.println("Invalid input. Please enter a positive integer quantity.");
                    }
                }

                Integer discountID = null;
                System.out.print("Enter Discount ID (or leave blank): ");
                String discountInput = scanner.nextLine().trim();
                if (!discountInput.isEmpty()) {
                    try {
                        discountID = Integer.parseInt(discountInput);
                    } catch (NumberFormatException ignored) {
                        System.out.println("Invalid discount ID. Ignoring.");
                    }
                }

                Double pricePerItem = ProductService.getProductPriceByCode(productCode);
                if (pricePerItem == null) {
                    System.out.println("Product not found. Please try again.");
                    continue;
                }

                BillItemRequest billItem = new BillItemRequest(productCode, quantity, BigDecimal.valueOf(pricePerItem));
                billItems.add(billItem);

                BigDecimal itemTotal = BigDecimal.valueOf(pricePerItem).multiply(BigDecimal.valueOf(quantity));
                totalBillAmount = totalBillAmount.add(itemTotal);


                System.out.print("Add another item? (yes/no): ");
                String another = scanner.nextLine().trim();
                if (another.equalsIgnoreCase("no")) break;
            }

            if (billItems.isEmpty()) {
                System.out.println("No items added. Bill canceled.");
                return;
            }

            BigDecimal cashTendered;
            while (true) {
                System.out.print("Enter Cash Tendered: ");
                String cashInput = scanner.nextLine().trim();
                try {
                    cashTendered = new BigDecimal(cashInput);
                    if (cashTendered.compareTo(totalBillAmount) < 0) {
                        System.out.println("Insufficient amount. Please enter at least: " + totalBillAmount);
                        continue;
                    }
                    break;
                } catch (NumberFormatException ex) {
                    System.out.println("Invalid amount. Please enter a valid number.");
                }
            }
            BigDecimal change = cashTendered.subtract(totalBillAmount);

            PaymentStrategy paymentStrategy = new CashPaymentStrategy();
            PaymentResultDTO payment = paymentStrategy.processPayment(totalBillAmount, cashTendered);

            CreateBillRequest billRequest = new CreateBillRequest(billItems, customer, cashTendered, TransactionType.OTC, payment.getMethod());
            Bill bill = billingService.createBill(billRequest); // Should return the created Bill, with line items/prices

            System.out.println("\nBill created successfully!");
            System.out.println("Change to return: " + change);

            System.out.println("Press [Enter] to return to Main Menu...");
            scanner.nextLine();

        } catch (IllegalStateException e) {
            System.out.println("\n❌ " + e.getMessage());
            EntryMenuFormatter.pauseForInput(scanner);
            return;

        } catch (RuntimeException e) {
        System.out.println("\n❌ An unexpected error occurred. Please try again.");
        e.printStackTrace(System.out);
        EntryMenuFormatter.pauseForInput(scanner);
    }
    }

    private void viewBill(Scanner scanner) {
        System.out.print("Enter Bill Date (yyyy-MM-dd): ");
        String dateInput = scanner.nextLine();

        LocalDate billDate;
        try {
            billDate = LocalDate.parse(dateInput);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Please use yyyy-MM-dd.");
            return;
        }

        System.out.print("Enter Bill Serial Number: ");
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a numeric Bill Serial Number.");
            scanner.next();
        }
        int serialNo = scanner.nextInt();
        scanner.nextLine();


        List<BillReceiptDTO> receipt = billingService.findBillReceipt(billDate, serialNo);

        if (receipt.isEmpty()) {
            System.out.println("No bill found for date " + billDate + " with serial number " + serialNo);
            return;
        }

        BillReceiptFormatter.printBillDetails(receipt);
    }
}

