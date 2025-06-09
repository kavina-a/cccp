package com.syos.domain.service;

import com.syos.application.dto.request.CustomerLoginRequest;
import com.syos.application.dto.request.CustomerSignupRequest;
import com.syos.application.dto.request.EmployeeLoginRequest;
import com.syos.data.dao.interfaces.CustomerDao;
import com.syos.data.dao.interfaces.EmployeeDao;
import com.syos.domain.entity.Customer;

import java.time.LocalDateTime;
import java.util.Optional;

import com.syos.application.dto.AuthenticationDTO;
import com.syos.domain.entity.Employee;
import com.syos.utils.SQLTransactionManager;
import com.syos.utils.SessionManager;

public class AuthenticationService {
    private final CustomerDao customerDao;
    private final EmployeeDao employeeDao;

    public AuthenticationService(CustomerDao customerDao, EmployeeDao employeeDao) {
        this.customerDao = customerDao;
        this.employeeDao = employeeDao;
    }

    public AuthenticationDTO loginEmployee(EmployeeLoginRequest request) {
        return SQLTransactionManager.execute(conn -> {
            Optional<Employee> employeeOpt = employeeDao.findByUsername(conn, request.getUsername());
            if (employeeOpt.isPresent() && employeeOpt.get().getPassword().equals(request.getPassword())) {
                SessionManager.getInstance().setLoggedInEmployee(employeeOpt.get());
                return new AuthenticationDTO(true, "Employee login successful.");
            } else {
                return new AuthenticationDTO(false, "Invalid username or password.");
            }
        });
    }

    /**
     * Customer login
     */
    public AuthenticationDTO loginCustomer(CustomerLoginRequest request) {
        return SQLTransactionManager.execute(conn -> {
            Optional<Customer> customerOpt = customerDao.findByEmail(conn, request.getEmail());
            if (customerOpt.isPresent() && customerOpt.get().getPassword().equals(request.getPassword())) {
                SessionManager.getInstance().setLoggedInCustomer(customerOpt.get());
                return new AuthenticationDTO(true, "Customer login successful.");
            } else {
                return new AuthenticationDTO(false, "Invalid email or password.");
            }
        });
    }

    public AuthenticationDTO registerCustomer(CustomerSignupRequest request) {
        return SQLTransactionManager.execute(conn -> {
            Optional<Customer> existing = customerDao.findByEmail(conn, request.getEmail());
            if (existing.isPresent()) {
                return new AuthenticationDTO(false, "Email already registered.");
            }
            Customer customer = new Customer();
            customer.setName(request.getName());
            customer.setEmail(request.getEmail());
            customer.setUsername(request.getUsername());
            customer.setAddress(request.getAddress());
            customer.setPassword(request.getPassword());
            customer.setRegistrationDate(request.getRegistrationDate() != null
                    ? request.getRegistrationDate()
                    : LocalDateTime.now());

            customerDao.save(conn, customer);

            SessionManager.getInstance().setLoggedInCustomer(customer);
            return new AuthenticationDTO(true, "Registration successful.");
        });
    }
    public Optional<Customer> validateCustomerById(String customerId) {
        return SQLTransactionManager.execute(conn ->
                customerDao.findById(conn, customerId)
        );
    }

    public void logout() {
        SessionManager.getInstance().logout();
    }
}