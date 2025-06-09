package com.syos.utils;

import com.syos.domain.entity.Customer;
import com.syos.domain.entity.Employee;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SessionManagerTest {

    private final SessionManager sessionManager = SessionManager.getInstance();

    @AfterEach
    void resetSession() {
        sessionManager.logout(); // Ensure clean state between tests
    }

    @Test
    void shouldSetAndGetLoggedInEmployee() {
        Employee employee = new Employee();
        employee.setEmployeeID("EMP001");

        sessionManager.setLoggedInEmployee(employee);

        assertEquals(employee, sessionManager.getLoggedInEmployee());
        assertTrue(sessionManager.isEmployeeLoggedIn());
        assertNull(sessionManager.getLoggedInCustomer()); // Dual login prevention
    }

    @Test
    void shouldSetAndGetLoggedInCustomer() {
        Customer customer = new Customer();
        customer.setCustomerID("CUS001");

        sessionManager.setLoggedInCustomer(customer);

        assertEquals(customer, sessionManager.getLoggedInCustomer());
        assertTrue(sessionManager.isCustomerLoggedIn());
        assertNull(sessionManager.getLoggedInEmployee()); // Dual login prevention
    }

    @Test
    void shouldPreventDualLoginWhenEmployeeLogsInAfterCustomer() {
        Customer customer = new Customer();
        customer.setCustomerID("CUS002");
        sessionManager.setLoggedInCustomer(customer);

        Employee employee = new Employee();
        employee.setEmployeeID("EMP002");
        sessionManager.setLoggedInEmployee(employee);

        assertEquals(employee, sessionManager.getLoggedInEmployee());
        assertTrue(sessionManager.isEmployeeLoggedIn());
        assertNull(sessionManager.getLoggedInCustomer()); // Customer session should be cleared
    }

    @Test
    void shouldPreventDualLoginWhenCustomerLogsInAfterEmployee() {
        Employee employee = new Employee();
        employee.setEmployeeID("EMP003");
        sessionManager.setLoggedInEmployee(employee);

        Customer customer = new Customer();
        customer.setCustomerID("CUS003");
        sessionManager.setLoggedInCustomer(customer);

        assertEquals(customer, sessionManager.getLoggedInCustomer());
        assertTrue(sessionManager.isCustomerLoggedIn());
        assertNull(sessionManager.getLoggedInEmployee()); // Employee session should be cleared
    }

    @Test
    void shouldClearBothSessionsOnLogout() {
        sessionManager.setLoggedInEmployee(new Employee());
        sessionManager.setLoggedInCustomer(new Customer());

        sessionManager.logout();

        assertNull(sessionManager.getLoggedInCustomer());
        assertNull(sessionManager.getLoggedInEmployee());
        assertFalse(sessionManager.isCustomerLoggedIn());
        assertFalse(sessionManager.isEmployeeLoggedIn());
    }

    @Test
    void shouldKeepOnlyLastSetRoleWhenRolesAreSetBackToBack() {
        Employee emp = new Employee();
        emp.setEmployeeID("E001");

        Customer cust = new Customer();
        cust.setCustomerID("C001");

        sessionManager.setLoggedInEmployee(emp);
        sessionManager.setLoggedInCustomer(cust); // This should override

        assertTrue(sessionManager.isCustomerLoggedIn());
        assertFalse(sessionManager.isEmployeeLoggedIn());
        assertNull(sessionManager.getLoggedInEmployee());
        assertEquals(cust, sessionManager.getLoggedInCustomer());
    }

    @Test
    void shouldAllowSettingSameCustomerAgainWithoutSideEffects() {
        Customer cust = new Customer();
        cust.setCustomerID("C002");

        sessionManager.setLoggedInCustomer(cust);
        sessionManager.setLoggedInCustomer(cust); // Repeat

        assertEquals(cust, sessionManager.getLoggedInCustomer());
    }

    @Test
    void shouldReturnNullAndFalseIfNoLoginHappened() {
        assertNull(sessionManager.getLoggedInCustomer());
        assertNull(sessionManager.getLoggedInEmployee());
        assertFalse(sessionManager.isCustomerLoggedIn());
        assertFalse(sessionManager.isEmployeeLoggedIn());
    }


}