package com.example.dentassist.models;

public class RegisterResponse {
    private boolean success;
    private String message;
    private String token;
    private int user_id;
    private int patient_id;
    private String username;
    private String full_name;
    private String role;
    private boolean redirect_to_login;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getToken() { return token; }
    public int getUser_id() { return user_id; }
    public int getPatient_id() { return patient_id; }
    public String getUsername() { return username; }
    public String getFull_name() { return full_name; }
    public String getRole() { return role; }
    public boolean isRedirect_to_login() { return redirect_to_login; }
}