package com.example.dentassist.models;

public class OwoPaymentInfoResponse {
    private boolean success;
    private String message;
    private Data data;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public Data getData() { return data; }

    public static class Data {
        private String clinic_owo_number;
        private double amount;
        private String plan;
        private String billing_cycle;
        private String reference;
        private int patient_id;
        private String patient_name;

        public String getClinic_owo_number() { return clinic_owo_number; }
        public double getAmount() { return amount; }
        public String getPlan() { return plan; }
        public String getBilling_cycle() { return billing_cycle; }
        public String getReference() { return reference; }
        public int getPatient_id() { return patient_id; }
        public String getPatient_name() { return patient_name; }
    }
}