package com.example.dentassist.models;

public class LoginRequest {
    private String username;
    private String password;
    private boolean rememberMe;

    public LoginRequest(String username, String password, boolean rememberMe) {
        this.username = username;
        this.password = password;
        this.rememberMe = rememberMe;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public boolean isRememberMe() { return rememberMe; }
}