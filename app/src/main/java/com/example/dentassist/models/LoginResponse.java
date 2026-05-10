package com.example.dentassist.models;

public class LoginResponse {
    private boolean success;
    private String message;
    private String token;
    private int user_id;
    private Integer patient_id;
    private String username;
    private String full_name;
    private String role;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getToken() { return token; }
    public int getUser_id() { return user_id; }
    public Integer getPatient_id() { return patient_id; }
    public String getUsername() { return username; }
    public String getFull_name() { return full_name; }
    public String getRole() { return role; }
}