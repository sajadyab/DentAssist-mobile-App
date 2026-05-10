package com.example.dentassist.app.network;

/**
 * Central network configuration.
 * Change BASE_URL here and it applies everywhere.
 */
public class NetworkConfig {

    // ==================== BASE URL ====================
    // Android Emulator: use 10.0.2.2
    // Physical Device:  use your computer's local IP (e.g., 192.168.1.100)
    // Production:       use your actual domain

    // CHANGE THIS ONE LINE TO SWITCH:
    public static final String BASE_URL = "http://10.0.2.2/Dental_test/";
    // my ip 192.168.0.113  10.0.2.2
    // Alternative URLs (comment/uncomment as needed):
    // public static final String BASE_URL = "http://192.168.1.100/Dental_test/";  // Physical device
    // public static final String BASE_URL = "https://yourdomain.com/Dental_test/"; // Production

    // ==================== WHATSAPP SERVER ====================
    // Same as BASE_URL host but port 3210
    public static final String WHATSAPP_SERVER_URL = BASE_URL.replace("/Dental_test/", ":3210");

    // Or set manually:
    // public static final String WHATSAPP_SERVER_URL = "http://10.0.2.2:3210";
    // public static final String WHATSAPP_SERVER_URL = "http://192.168.1.100:3210";
}