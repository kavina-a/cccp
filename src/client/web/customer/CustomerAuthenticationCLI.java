package client.web.customer;

import server.application.dto.AuthenticationDTO;
import server.application.dto.request.CustomerLoginRequest;
import server.application.dto.request.CustomerSignupRequest;
import server.domain.service.AuthenticationService;

import java.util.Scanner;

public class CustomerAuthenticationCLI {

    private final AuthenticationService authService;
    private final Scanner scanner = new Scanner(System.in);

    public CustomerAuthenticationCLI(AuthenticationService authService) {
        this.authService = authService;
    }

    public boolean start() {
        printHeader();
        showMenu();
        int option = readOption();

        return switch (option) {
            case 1 -> handleLogin();
            case 2 -> handleRegistration();
            default -> {
                System.out.println("\n❌ Invalid choice. Please restart and try again.");
                yield false;
            }
        };
    }

    private void printHeader() {
        System.out.println("\n========================================");
        System.out.println("     🛍️  Welcome to SYOS Customer Portal");
        System.out.println("========================================");
        System.out.println(" Your shopping, simplified. Login or sign up below!\n");
    }

    private void showMenu() {
        System.out.println("╔══════════════════════╗");
        System.out.println("║  1. Login            ║");
        System.out.println("║  2. Register         ║");
        System.out.println("╚══════════════════════╝");
        System.out.print("👉 Choose option (1 or 2): ");
    }

    private int readOption() {
        while (!scanner.hasNextInt()) {
            System.out.println("⚠️  Please enter a valid number (1 or 2).");
            scanner.next();
            System.out.print("👉 Choose option (1 or 2): ");
        }
        int option = scanner.nextInt();
        scanner.nextLine(); // consume newline
        return option;
    }

    private boolean handleLogin() {
        System.out.println("\n🔐 Login to Your Account");
        System.out.println("------------------------");

        System.out.print("📧 Email: ");
        String email = scanner.nextLine();

        System.out.print("🔑 Password: ");
        String password = scanner.nextLine();

        CustomerLoginRequest loginRequest = new CustomerLoginRequest(email, password);
        AuthenticationDTO result = authService.loginCustomer(loginRequest);

        System.out.println("\n" + (result.isSuccess() ? "✅ " : "❌ ") + result.getMessage());
        return result.isSuccess();
    }

    private boolean handleRegistration() {
        System.out.println("\n📝 Create Your Account");
        System.out.println("----------------------");

        System.out.print("🙋 Full Name: ");
        String name = scanner.nextLine();

        System.out.print("📧 Email: ");
        String email = scanner.nextLine();

        System.out.print("👤 Username: ");
        String username = scanner.nextLine();

        System.out.print("🔑 Password: ");
        String password = scanner.nextLine();

        System.out.print("🏠 Address: ");
        String address = scanner.nextLine();

        CustomerSignupRequest signupRequest = new CustomerSignupRequest();
        signupRequest.setName(name);
        signupRequest.setEmail(email);
        signupRequest.setUsername(username);
        signupRequest.setPassword(password);
        signupRequest.setAddress(address);

        AuthenticationDTO result = authService.registerCustomer(signupRequest);

        System.out.println("\n" + (result.isSuccess() ? "🎉 " : "❌ ") + result.getMessage());
        return result.isSuccess();
    }
}


