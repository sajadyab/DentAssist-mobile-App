package com.example.dentassist.models;

public class ProfileUpdateRequest {
    private String full_name;
    private String username;
    private String date_of_birth;
    private String gender;
    private String email;
    private String phone;
    private String address;
    private String emergency_contact_name;
    private String emergency_contact_phone;
    private String emergency_contact_relation;

    public ProfileUpdateRequest(String full_name, String username, String date_of_birth,
                                String gender, String email, String phone, String address,
                                String emergency_contact_name, String emergency_contact_phone,
                                String emergency_contact_relation) {
        this.full_name = full_name;
        this.username = username;
        this.date_of_birth = date_of_birth;
        this.gender = gender;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.emergency_contact_name = emergency_contact_name;
        this.emergency_contact_phone = emergency_contact_phone;
        this.emergency_contact_relation = emergency_contact_relation;
    }
}