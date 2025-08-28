package com.syos.service;

import server.application.dto.AuthenticationDTO;
import server.application.dto.request.CustomerLoginRequest;
import server.application.dto.request.CustomerSignupRequest;
import server.application.dto.request.EmployeeLoginRequest;
import server.data.dao.interfaces.CustomerDao;
import server.data.dao.interfaces.EmployeeDao;
import server.domain.entity.Customer;
import server.domain.entity.Employee;
import server.domain.service.AuthenticationService;
import server.utils.SessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthenticationServiceTest {
    private AuthenticationService authService;
    private CustomerDao customerDao;
    private EmployeeDao employeeDao;
    private SessionManager sessionManager;

    @BeforeEach
    void setUp() {
        customerDao = mock(CustomerDao.class);
        employeeDao = mock(EmployeeDao.class);
        authService = new AuthenticationService(customerDao, employeeDao);
    }

    // 1. Valid employee login
    @Test
    void shouldLoginEmployeeSuccessfully() {
        EmployeeLoginRequest request = new EmployeeLoginRequest("john", "password123");
        Employee employee = new Employee();
        employee.setUsername("john");
        employee.setPassword("password123");

        when(employeeDao.findByUsername(any(), eq("john"))).thenReturn(Optional.of(employee));

        AuthenticationDTO response = authService.loginEmployee(request);

        assertTrue(response.isSuccess());
        assertEquals("Employee login successful.", response.getMessage());
    }

    // 2. Invalid employee username
    @Test
    void shouldFailEmployeeLoginIfUsernameNotFound() {
        EmployeeLoginRequest request = new EmployeeLoginRequest("unknown", "pass");
        when(employeeDao.findByUsername(any(), eq("unknown"))).thenReturn(Optional.empty());

        AuthenticationDTO response = authService.loginEmployee(request);

        assertFalse(response.isSuccess());
        assertEquals("Invalid username or password.", response.getMessage());
    }

    // 3. Invalid employee password
    @Test
    void shouldFailEmployeeLoginIfPasswordIncorrect() {
        EmployeeLoginRequest request = new EmployeeLoginRequest("john", "wrongpass");
        Employee employee = new Employee();
        employee.setUsername("john");
        employee.setPassword("correctpass");

        when(employeeDao.findByUsername(any(), eq("john"))).thenReturn(Optional.of(employee));

        AuthenticationDTO response = authService.loginEmployee(request);

        assertFalse(response.isSuccess());
        assertEquals("Invalid username or password.", response.getMessage());
    }

    // 4. Null employee username
    @Test
    void shouldHandleNullEmployeeUsernameGracefully() {
        EmployeeLoginRequest request = new EmployeeLoginRequest(null, "pass");
        when(employeeDao.findByUsername(any(), isNull())).thenReturn(Optional.empty());

        AuthenticationDTO response = authService.loginEmployee(request);

        assertFalse(response.isSuccess());
    }

    // 5. Valid customer login
    @Test
    void shouldLoginCustomerSuccessfully() {
        CustomerLoginRequest request = new CustomerLoginRequest("user@example.com", "secret");
        Customer customer = new Customer();
        customer.setEmail("user@example.com");
        customer.setPassword("secret");

        when(customerDao.findByEmail(any(), eq("user@example.com"))).thenReturn(Optional.of(customer));

        AuthenticationDTO response = authService.loginCustomer(request);

        assertTrue(response.isSuccess());
        assertEquals("Customer login successful.", response.getMessage());
    }

    // 6. Invalid customer email
    @Test
    void shouldFailCustomerLoginIfEmailNotFound() {
        CustomerLoginRequest request = new CustomerLoginRequest("invalid@example.com", "pass");
        when(customerDao.findByEmail(any(), eq("invalid@example.com"))).thenReturn(Optional.empty());

        AuthenticationDTO response = authService.loginCustomer(request);

        assertFalse(response.isSuccess());
    }

    // 7. Customer password mismatch
    @Test
    void shouldFailCustomerLoginIfPasswordWrong() {
        CustomerLoginRequest request = new CustomerLoginRequest("user@example.com", "wrong");
        Customer customer = new Customer();
        customer.setEmail("user@example.com");
        customer.setPassword("correct");

        when(customerDao.findByEmail(any(), eq("user@example.com"))).thenReturn(Optional.of(customer));

        AuthenticationDTO response = authService.loginCustomer(request);

        assertFalse(response.isSuccess());
    }

    // 8. Successful customer registration
    @Test
    void shouldRegisterCustomerSuccessfully() throws SQLException {
        CustomerSignupRequest request = new CustomerSignupRequest("Name", "name@example.com", "user", "pass", "address", null);

        when(customerDao.findByEmail(any(), eq("name@example.com"))).thenReturn(Optional.empty());
        doNothing().when(customerDao).save(any(), any());

        AuthenticationDTO response = authService.registerCustomer(request);

        assertTrue(response.isSuccess());
        assertEquals("Registration successful.", response.getMessage());
    }

    // 9. Email already registered
    @Test
    void shouldRejectDuplicateCustomerRegistration() {
        CustomerSignupRequest request = new CustomerSignupRequest("Name", "name@example.com", "user", "pass", "address", null);
        Customer existing = new Customer();

        when(customerDao.findByEmail(any(), eq("name@example.com"))).thenReturn(Optional.of(existing));

        AuthenticationDTO response = authService.registerCustomer(request);

        assertFalse(response.isSuccess());
        assertEquals("Email already registered.", response.getMessage());
    }

    // 10. Registration with null password
    @Test
    void shouldHandleNullPasswordInRegistration() {
        CustomerSignupRequest request = new CustomerSignupRequest("Name", "name@example.com", "user", null, "address", null);
        when(customerDao.findByEmail(any(), any())).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> authService.registerCustomer(request));
    }

    @Test
    void shouldHandleNullEmployeeLoginRequestGracefully() {
        assertThrows(NullPointerException.class, () -> authService.loginEmployee(null));
    }

    @Test
    void shouldHandleNullCustomerLoginRequestGracefully() {
        assertThrows(NullPointerException.class, () -> authService.loginCustomer(null));
    }

    @Test
    void shouldHandleNullSignupRequestInRegisterCustomer() {
        assertThrows(NullPointerException.class, () -> authService.registerCustomer(null));
    }

//    @Test
//    void shouldRejectIncompleteSignupRequest() {
//        CustomerSignupRequest request = new CustomerSignupRequest(null, "", "", null, null, null);
//        when(customerDao.findByEmail(any(), any())).thenReturn(Optional.empty());
//
//        assertThrows(NullPointerException.class, () -> authService.registerCustomer(request));
//    }

    @Test
    void shouldWrapDaoFailureDuringRegistration() {
        CustomerSignupRequest request = new CustomerSignupRequest("Name", "test@mail.com", "user", "pass", "addr", null);
        when(customerDao.findByEmail(any(), any())).thenReturn(Optional.empty());
        doThrow(new RuntimeException("DAO error")).when(customerDao).save(any(), any());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.registerCustomer(request));
        assertTrue(ex.getMessage().contains("DAO error"));
    }

    @Test
    void shouldWrapDaoFailureDuringCustomerLogin() {
        CustomerLoginRequest request = new CustomerLoginRequest("email@mail.com", "pass");
        when(customerDao.findByEmail(any(), any())).thenThrow(new RuntimeException("Login failed"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.loginCustomer(request));
        assertTrue(ex.getMessage().contains("Login failed"));
    }

    @Test
    void shouldWrapUncheckedExceptionInLoginEmployee() {
        EmployeeLoginRequest request = new EmployeeLoginRequest("admin", "1234");
        when(employeeDao.findByUsername(any(), any())).thenThrow(new RuntimeException("DB error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.loginEmployee(request));
        assertTrue(ex.getMessage().contains("DB error"));
    }

    @Test
    void shouldReturnValidCustomerWhenExistsInValidateCustomerById() {
        Customer customer = new Customer();
        when(customerDao.findById(any(), eq("CUST123"))).thenReturn(Optional.of(customer));

        Optional<Customer> result = authService.validateCustomerById("CUST123");
        assertTrue(result.isPresent());
    }

    @Test
    void shouldReturnEmptyWhenCustomerNotFoundById() {
        when(customerDao.findById(any(), eq("INVALID_ID"))).thenReturn(Optional.empty());

        Optional<Customer> result = authService.validateCustomerById("INVALID_ID");
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldWrapExceptionIfCustomerLookupFails() {
        when(customerDao.findById(any(), eq("FAIL_ID"))).thenThrow(new RuntimeException("DB Failure"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.validateCustomerById("FAIL_ID"));
        assertTrue(ex.getMessage().contains("DB Failure"));
    }


}
