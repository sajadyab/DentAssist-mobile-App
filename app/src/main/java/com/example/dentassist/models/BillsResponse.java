package com.example.dentassist.models;

import java.util.List;

public class BillsResponse {
    private boolean success;
    private String message;
    private BillsData data;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public BillsData getData() { return data; }

    public static class BillsData {
        private Stats stats;
        private List<Invoice> invoices;
        private List<Subscription> subscriptions;
        private String clinic_phone;

        public Stats getStats() { return stats; }
        public List<Invoice> getInvoices() { return invoices; }
        public List<Subscription> getSubscriptions() { return subscriptions; }
        public String getClinic_phone() { return clinic_phone; }
    }

    public static class Stats {
        private int total_invoices;
        private double total_paid;
        private double balance_due;

        public int getTotal_invoices() { return total_invoices; }
        public double getTotal_paid() { return total_paid; }
        public double getBalance_due() { return balance_due; }
    }

    public static class Invoice {
        private int id;
        private String invoice_number;
        private String invoice_date;
        private String due_date;
        private double subtotal;
        private double paid_amount;
        private double balance_due;
        private String payment_status;
        private String payment_method;

        public int getId() { return id; }
        public String getInvoice_number() { return invoice_number; }
        public String getInvoice_date() { return invoice_date; }
        public String getDue_date() { return due_date; }
        public double getSubtotal() { return subtotal; }
        public double getPaid_amount() { return paid_amount; }
        public double getBalance_due() { return balance_due; }
        public String getPayment_status() { return payment_status; }
        public String getPayment_method() { return payment_method; }
    }

    public static class Subscription {
        private int id;
        private String subscription_type;
        private double amount;
        private String payment_method;
        private String payment_date;
        private String status;
        private String payment_reference;

        public int getId() { return id; }
        public String getSubscription_type() { return subscription_type; }
        public double getAmount() { return amount; }
        public String getPayment_method() { return payment_method; }
        public String getPayment_date() { return payment_date; }
        public String getStatus() { return status; }
        public String getPayment_reference() { return payment_reference; }
    }
}