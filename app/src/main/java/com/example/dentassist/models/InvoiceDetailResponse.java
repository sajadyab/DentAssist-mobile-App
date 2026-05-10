package com.example.dentassist.models;

import java.util.List;

public class InvoiceDetailResponse {
    private boolean success;
    private String message;
    private InvoiceData data;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public InvoiceData getData() { return data; }

    public static class InvoiceData {
        private String invoice_number;
        private String invoice_date;
        private String due_date;
        private String patient_name;
        private String treatment_type;
        private String appointment_date;
        private double subtotal;
        private double total;
        private double paid_amount;
        private double balance_due;
        private String payment_status;
        private String notes;
        private List<Payment> payments;

        public String getInvoice_number() { return invoice_number; }
        public String getInvoice_date() { return invoice_date; }
        public String getDue_date() { return due_date; }
        public String getPatient_name() { return patient_name; }
        public String getTreatment_type() { return treatment_type; }
        public String getAppointment_date() { return appointment_date; }
        public double getSubtotal() { return subtotal; }
        public double getTotal() { return total; }
        public double getPaid_amount() { return paid_amount; }
        public double getBalance_due() { return balance_due; }
        public String getPayment_status() { return payment_status; }
        public String getNotes() { return notes; }
        public List<Payment> getPayments() { return payments; }
    }

    public static class Payment {
        private String date;
        private String method;
        private String reference;
        private double amount;
        private String notes;

        public String getDate() { return date; }
        public String getMethod() { return method; }
        public String getReference() { return reference; }
        public double getAmount() { return amount; }
        public String getNotes() { return notes; }
    }
}