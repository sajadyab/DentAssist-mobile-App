package com.example.dentassist.models;

public class OwoInfoRequest {
    private String plan;
    private String billing_cycle;

    public OwoInfoRequest(String plan, String billing_cycle) {
        this.plan = plan;
        this.billing_cycle = billing_cycle;
    }

    public String getPlan() { return plan; }
    public String getBilling_cycle() { return billing_cycle; }
}