package com.example.dentassist;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;

import com.example.dentassist.app.network.ApiService;
import com.example.dentassist.app.network.RetrofitClient;
import com.example.dentassist.models.ForgotPasswordRequest;
import com.example.dentassist.models.ForgotPasswordResponse;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Forgot_Pass extends AppCompatActivity {

    private static final String TAG = "FORGOT_PASS";

    // ==================== FORM INPUT LAYOUT ====================
    private TextInputLayout tilUsername;

    // ==================== FORM INPUT FIELD ====================
    private TextInputEditText etUsername;

    // ==================== BUTTONS & LINKS ====================
    private AppCompatButton btnResetPassword;
    private AppCompatButton btnBackToLogin;
    private View tvFooterSignIn;
    private View tvFooterRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_pass);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        tilUsername = findViewById(R.id.tilUsername);
        etUsername = findViewById(R.id.etUsername);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        btnBackToLogin = findViewById(R.id.btnBackToLogin);
        tvFooterSignIn = findViewById(R.id.tvFooterSignIn);
        tvFooterRegister = findViewById(R.id.tvFooterRegister);
    }

    private void setupClickListeners() {
        btnResetPassword.setOnClickListener(v -> performPasswordReset());
        btnBackToLogin.setOnClickListener(v -> navigateToLogin());
        if (tvFooterSignIn != null) tvFooterSignIn.setOnClickListener(v -> navigateToLogin());
        if (tvFooterRegister != null) tvFooterRegister.setOnClickListener(v -> navigateToRegister());
    }

    private void performPasswordReset() {
        String username = getUsername();
        clearError();

        if (TextUtils.isEmpty(username)) {
            setUsernameError("Username is required");
            return;
        }

        setLoading(true);

        Log.d(TAG, "Sending forgot password request for username: " + username);

        ForgotPasswordRequest request = new ForgotPasswordRequest(username);
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        apiService.forgotPassword(request).enqueue(new Callback<ForgotPasswordResponse>() {
            @Override
            public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                setLoading(false);

                Log.d(TAG, "Response code: " + response.code());

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    // SUCCESS
                    String message = response.body().getMessage();
                    if (response.body().isWhatsapp_sent()) {
                        message = "Reset link sent to your WhatsApp!";
                    }
                    Toast.makeText(Forgot_Pass.this, message, Toast.LENGTH_LONG).show();
                    btnResetPassword.postDelayed(() -> navigateToLogin(), 2000);
                } else {
                    // ERROR - show specific message based on response code
                    String error;
                    if (response.code() == 404) {
                        error = "No account found with that username.";
                    } else if (response.body() != null && response.body().getMessage() != null) {
                        error = response.body().getMessage();
                    } else {
                        error = "Request failed. Please try again.";
                    }
                    Log.e(TAG, "Error: " + error);
                    Toast.makeText(Forgot_Pass.this, error, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                setLoading(false);
                Log.e(TAG, "Network failure: " + t.getMessage(), t);
                Toast.makeText(Forgot_Pass.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // ==================== GETTERS ====================
    public String getUsername() {
        return etUsername != null ? etUsername.getText().toString().trim() : "";
    }

    // ==================== SETTERS ====================
    public void setUsername(String username) {
        if (etUsername != null) etUsername.setText(username != null ? username : "");
    }

    // ==================== ERROR HANDLING ====================
    public void setUsernameError(String error) {
        if (tilUsername != null) tilUsername.setError(error);
    }

    private void clearError() {
        if (tilUsername != null) tilUsername.setError(null);
    }

    // ==================== LOADING STATE ====================
    private void setLoading(boolean loading) {
        if (btnResetPassword != null) {
            btnResetPassword.setEnabled(!loading);
            btnResetPassword.setText(loading ? "Sending..." : "Reset Password");
        }
    }

    // ==================== NAVIGATION ====================
    private void navigateToLogin() {
        startActivity(new Intent(this, Login.class));
        finish();
    }

    private void navigateToRegister() {
        startActivity(new Intent(this, Register.class));
        finish();
    }

    // ==================== CLEAR ====================
    public void clearFields() {
        setUsername("");
        clearError();
    }
}