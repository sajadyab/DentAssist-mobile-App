package com.example.dentassist.models;

public class PaymentConfirmRequest {
    private int patient_id;
    private String plan;
    private double amount;
    private String reference;
    private String payment_method;

    public PaymentConfirmRequest(int patient_id, String plan, double amount, String reference, String payment_method) {
        this.patient_id = patient_id;
        this.plan = plan;
        this.amount = amount;
        this.reference = reference;
        this.payment_method = payment_method;
    }

    // Getters (required for Gson serialisation)
    public int getPatient_id() { return patient_id; }
    public String getPlan() { return plan; }
    public double getAmount() { return amount; }
    public String getReference() { return reference; }
    public String getPayment_method() { return payment_method; }
}