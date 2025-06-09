package com.syos.application.dto;

public class AuthenticationDTO {
    private boolean success;
    private String message;

    public AuthenticationDTO(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
