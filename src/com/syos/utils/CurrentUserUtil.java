package com.syos.utils;

import com.syos.domain.entity.Customer;
import com.syos.domain.entity.Employee;


public class CurrentUserUtil {

    public static boolean isCustomerLoggedIn() {
        return SessionManager.getInstance().getLoggedInCustomer() != null;
    }

    public static boolean isEmployeeLoggedIn() {
        return SessionManager.getInstance().getLoggedInEmployee() != null;
    }

    public static boolean isUserLoggedIn() {
        return isCustomerLoggedIn() || isEmployeeLoggedIn();
    }

    /**
     * Get the currently logged-in customer.
     * @throws IllegalStateException if no customer is logged in.
     */

    public static Customer getCurrentCustomer() {
        Customer customer = SessionManager.getInstance().getLoggedInCustomer();
        if (customer == null) {
            throw new IllegalStateException("No customer is currently logged in.");
        }
        return customer;
    }

    public static Employee getCurrentEmployee() {
        Employee employee = SessionManager.getInstance().getLoggedInEmployee();
        if (employee == null) {
            throw new IllegalStateException("No employee is currently logged in.");
        }
        return employee;
    }

    public static String getCurrentUserId() {
        if (isCustomerLoggedIn()) {
            return getCurrentCustomer().getCustomerID();
        } else if (isEmployeeLoggedIn()) {
            return getCurrentEmployee().getEmployeeID();
        } else {
            throw new IllegalStateException("No user is currently logged in.");
        }
    }

    public static String getCurrentUserRole() {
        if (isCustomerLoggedIn()) {
            return "CUSTOMER";
        }
        if (isEmployeeLoggedIn()) {
            Employee employee = getCurrentEmployee();
            return employee.getRole().name();
        }
        return "UNKNOWN";
    }
}