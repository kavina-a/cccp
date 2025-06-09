package com.syos.interfaces.cli;

import com.syos.domain.service.BusinessDayService;

import java.sql.SQLException;
import java.util.Scanner;

public class BusinessDayCLI {

    private final BusinessDayService businessDayService;

    public BusinessDayCLI(BusinessDayService businessDayService) {
        this.businessDayService = businessDayService;
    }

    public void start() throws SQLException {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n==========================================");
            System.out.println("         BUSINESS DAY OPERATIONS          ");
            System.out.println("==========================================");
            System.out.println("1. Start Business Day");
            System.out.println("2. Run Day-End Process");
            System.out.println("3. Return to Main Menu");
            System.out.println("==========================================");
            System.out.print("Enter your selection: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    System.out.println("\n[INFO] Initializing business day...");
                    businessDayService.runDayStart();
                    System.out.println("[SUCCESS] Business day started.");
                }
                case "2" -> {
                    System.out.println("\n[INFO] Running day-end process...");
                    businessDayService.runDayEnd();
                    System.out.println("[SUCCESS] Day-end process completed.");
                }
                case "3" -> {
                    System.out.println("Returning to main menu...");
                    return;
                }
                default -> System.out.println("[ERROR] Invalid selection. Please enter 1, 2, or 3.");
            }
        }
    }
}
