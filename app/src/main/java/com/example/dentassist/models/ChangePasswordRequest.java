package com.example.dentassist.models;

public class ChangePasswordRequest {
    private String current_password;
    private String new_password;

    public ChangePasswordRequest(String current_password, String new_password) {
        this.current_password = current_password;
        this.new_password = new_password;
    }
}