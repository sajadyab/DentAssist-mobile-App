package com.example.dentassist.models;

public class ProfileResponse {
    private boolean success;
    private String message;
    private ProfileData data;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public ProfileData getData() { return data; }

    public static class ProfileData {
        private String full_name;
        private String username;
        private String date_of_birth;
        private String gender;
        private String phone;
        private String email;
        private String address;
        private String emergency_contact_name;
        private String emergency_contact_phone;
        private String emergency_contact_relation;
        private int points;
        private String referral_code;

        public String getFull_name() { return full_name; }
        public String getUsername() { return username; }
        public String getDate_of_birth() { return date_of_birth; }
        public String getGender() { return gender; }
        public String getPhone() { return phone; }
        public String getEmail() { return email; }
        public String getAddress() { return address; }
        public String getEmergency_contact_name() { return emergency_contact_name; }
        public String getEmergency_contact_phone() { return emergency_contact_phone; }
        public String getEmergency_contact_relation() { return emergency_contact_relation; }
        public int getPoints() { return points; }
        public String getReferral_code() { return referral_code; }
    }
}