package com.example.dentassist.models;

import java.util.List;

public class SubscriptionResponse {
    private boolean success;
    private String message;
    private SubscriptionData data;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public SubscriptionData getData() { return data; }

    public static class SubscriptionData {
        private String current_plan;
        private String subscription_status;
        private String start_date;
        private String end_date;
        private List<Plan> plans;

        public String getCurrent_plan() { return current_plan; }
        public String getSubscription_status() { return subscription_status; }
        public String getStart_date() { return start_date; }
        public String getEnd_date() { return end_date; }
        public List<Plan> getPlans() { return plans; }
    }

    public static class Plan {
        private String plan_key;
        private String plan_name;
        private double monthly_price;
        private double annual_price;
        private String features;

        public String getPlan_key() { return plan_key; }
        public String getPlan_name() { return plan_name; }
        public double getMonthly_price() { return monthly_price; }
        public double getAnnual_price() { return annual_price; }
        public String getFeatures() { return features; }
    }
}