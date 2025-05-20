package com.syos.entities;

public class Customer {
    private String id;
    private String name;
    private String email;
    private String phoneNumber;
    private String address;

    public Customer(String id, String name, String email, String phoneNumber, String address) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

}