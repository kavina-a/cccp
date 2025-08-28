package server.application.dto.request;

import java.time.LocalDateTime;
import java.util.Date;

public class CustomerSignupRequest {

    private String name;
    private String email;
    private String address;
    private String username;
    private String password;

    public CustomerSignupRequest(String name, String email, String username, String password, String address, LocalDateTime registrationDate) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        this.address = address;
        this.registrationDate = registrationDate;
    }

    public  CustomerSignupRequest() {
    }


    // Optional for advanced cases
    private LocalDateTime registrationDate;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }
}
