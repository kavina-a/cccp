package com.syos.utils;

import com.syos.domain.entity.Customer;
import com.syos.domain.entity.Employee;

public class SessionManager {

    private static SessionManager instance;

    private Customer loggedInCustomer;
    private Employee loggedInEmployee;

    private SessionManager() {}

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setLoggedInEmployee(Employee employee) {
        this.loggedInEmployee = employee;
        this.loggedInCustomer = null; // prevent dual login
    }

    public Employee getLoggedInEmployee() {
        return loggedInEmployee;
    }

    public boolean isEmployeeLoggedIn() {
        return loggedInEmployee != null;
    }

    public void setLoggedInCustomer(Customer customer) {
        this.loggedInCustomer = customer;
        this.loggedInEmployee = null; // prevent dual login
    }

    public Customer getLoggedInCustomer() {
        return loggedInCustomer;
    }

    public boolean isCustomerLoggedIn() {
        return loggedInCustomer != null;
    }

    public void logout() {
        this.loggedInEmployee = null;
        this.loggedInCustomer = null;
    }
}