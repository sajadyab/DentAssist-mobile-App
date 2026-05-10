package com.example.dentassist.models;

public class ForgotPasswordRequest {
    private String username;

    public ForgotPasswordRequest(String username) {
        this.username = username;
    }

    public String getUsername() { return username; }
}