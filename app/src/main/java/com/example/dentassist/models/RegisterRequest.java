package com.example.dentassist.models;

public class RegisterRequest {
    private String full_name;
    private String username;
    private String email;
    private String password;
    private String password_confirm;
    private String date_of_birth;
    private String phone;
    private String referral_code;

    public RegisterRequest(String full_name, String username, String email,
                           String password, String password_confirm,
                           String date_of_birth, String phone, String referral_code) {
        this.full_name = full_name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.password_confirm = password_confirm;
        this.date_of_birth = date_of_birth;
        this.phone = phone;
        this.referral_code = referral_code;
    }

    // Getters for serialization
    public String getFull_name() { return full_name; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getPassword_confirm() { return password_confirm; }
    public String getDate_of_birth() { return date_of_birth; }
    public String getPhone() { return phone; }
    public String getReferral_code() { return referral_code; }
}