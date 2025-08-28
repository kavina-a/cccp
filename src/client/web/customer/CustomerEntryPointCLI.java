//package com.syos.interfaces.cli.customer;
//
//import com.syos.application.dto.AuthenticationDTO;
//import com.syos.domain.service.AuthenticationService;
//
//public class CustomerEntryPointCLI {
//    private final AuthenticationService authService = new AuthenticationService();
//
//    public void start() {
//        CustomerAuthenticationCLI authCLI = new CustomerAuthenticationCLI(authService);
//        AuthenticationDTO result = authCLI.authenticate();
//
//        if (!result.isSuccess()) {
//            System.out.println("Authentication failed: " + result.getMessage());
//            return;
//        }
//
//        new CustomerMainMenuCLI(result.getUserId()).start(); // if you pass ID
//    }
//}