package com.example.dentassist.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREFS_NAME = "DentAssistPrefs";
    private static final String KEY_TOKEN = "api_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_PATIENT_ID = "patient_id";
    private static final String KEY_USERNAME = "username";
    private SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveSession(String token, int userId, Integer patientId, String username) {
        prefs.edit()
                .putString(KEY_TOKEN, token)
                .putInt(KEY_USER_ID, userId)
                .putInt(KEY_PATIENT_ID, patientId != null ? patientId : 0)
                .putString(KEY_USERNAME, username)
                .apply();
    }

    public String getToken() { return prefs.getString(KEY_TOKEN, null); }
    public int getUserId() { return prefs.getInt(KEY_USER_ID, 0); }
    public int getPatientId() { return prefs.getInt(KEY_PATIENT_ID, 0); }
    public String getUsername() { return prefs.getString(KEY_USERNAME, ""); }
    public boolean isLoggedIn() { return getToken() != null && !getToken().isEmpty(); }

    public void logout() {
        prefs.edit().clear().apply();
    }
}