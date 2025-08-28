package client.desktop.interfaces.cli.utils;

import server.domain.entity.Employee;
import java.util.Scanner;

public class EntryMenuFormatter {

    public static void printHeader(Employee employee, String roleLabel) {
        String border = "=".repeat(50);
        System.out.println("\n" + border);
        System.out.println(roleLabel.toUpperCase() + " CONSOLE - SYOS POS SYSTEM");
        System.out.println(border + "\n");
    }

    public static void pauseForInput(Scanner scanner) {
        System.out.print("Press Enter to return to the menu...");
        scanner.nextLine();
    }

    public static void printMenuBox(String[] options) {
        System.out.println("┌────────────────────────────────────────────┐");
        for (String option : options) {
            System.out.printf("│  %-42s│%n", option);
        }
        System.out.println("└────────────────────────────────────────────┘");
    }
}
