package com.example.dentassist.models;

public class ForgotPasswordResponse {
    private boolean success;
    private String message;
    private boolean whatsapp_sent;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public boolean isWhatsapp_sent() { return whatsapp_sent; }
}