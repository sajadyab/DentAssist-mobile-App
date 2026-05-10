package com.example.dentassist.models;
public class SubscribeRequest {
    private String plan;
    private String action; // "clinic_payment" or "online_payment"
    private String billing_cycle;

    public SubscribeRequest(String plan, String action, String billing_cycle) {
        this.plan = plan;
        this.action = action;
        this.billing_cycle = billing_cycle;
    }
}