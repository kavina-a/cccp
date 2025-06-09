package com.syos.interfaces.cli;

import com.syos.application.dto.AuthenticationDTO;
import com.syos.application.dto.request.EmployeeLoginRequest;
import com.syos.domain.entity.Employee;
import com.syos.domain.service.AuthenticationService;
import com.syos.utils.SessionManager;

import java.util.Scanner;

public class AuthenticationCLI {
    private final AuthenticationService authenticationService;

    public AuthenticationCLI(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public boolean start() {
        printHeader();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            printLoginPrompt();

            System.out.print("   Username (or type 'exit' to quit): ");
            String username = scanner.nextLine().trim();
            if (username.equalsIgnoreCase("exit")) {
                System.out.println(" >> Exiting login...");
                return false;
            }

            System.out.print("   Password: ");
            String password = scanner.nextLine().trim();

            EmployeeLoginRequest loginRequest = new EmployeeLoginRequest(username, password);
            AuthenticationDTO loginResult = authenticationService.loginEmployee(loginRequest);

            System.out.println();
            System.out.println(" >> " + loginResult.getMessage());

            if (loginResult.isSuccess()) {
                Employee employee = SessionManager.getInstance().getLoggedInEmployee();
                System.out.println(" >> Welcome, " + employee.getUsername() + " (" + employee.getRole() + ")");
                return true;
            }

            System.out.println(" >> Login failed. Press Ctrl+C to exit or try again.\n");
        }

    }

    private void printHeader() {
        String title = "SYOS POINT-OF-SALE TERMINAL";
        String subtitle = "Staff Login - Manage Sales, Stock & Billing";
        String border = repeat("=", 50);

        System.out.println();
        System.out.println(border);
        System.out.println(centerText(title, border.length()));
        System.out.println(centerText(subtitle, border.length()));
        System.out.println(border);
    }

    private void printLoginPrompt() {
        System.out.println("\n+-----------------------------+");
        System.out.println("|        Employee Login       |");
        System.out.println("+-----------------------------+");
    }

    private String centerText(String text, int width) {
        int padding = Math.max(0, (width - text.length()) / 2);
        return repeat(" ", padding) + text;
    }

    private String repeat(String s, int times) {
        return s.repeat(Math.max(0, times));
    }
}