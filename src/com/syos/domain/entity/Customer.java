package com.syos.domain.entity;

import java.time.LocalDateTime;
import java.util.Objects;

public class Customer {
    private String customerID;
    private String name;
    private String email;
    private String username;
    private String password;
    private String address;
    private LocalDateTime registrationDate;

    public String getCustomerID() { return customerID; }

    public void setCustomerID(String customerID) { this.customerID = customerID; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getAddress() { return address; }

    public void setAddress(String address) { this.address = address; }

    public LocalDateTime getRegistrationDate() { return registrationDate; }

    public void setRegistrationDate(LocalDateTime registrationDate) { this.registrationDate = registrationDate; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer)) return false;
        Customer other = (Customer) o;
        return customerID == other.customerID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerID);
    }
}