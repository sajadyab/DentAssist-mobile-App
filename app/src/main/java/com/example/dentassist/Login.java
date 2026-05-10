package com.example.dentassist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;

import com.example.dentassist.app.network.ApiService;
import com.example.dentassist.app.network.RetrofitClient;
import com.example.dentassist.models.LoginRequest;
import com.example.dentassist.models.LoginResponse;
import com.example.dentassist.utils.SessionManager;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    private TextInputLayout tilUsername, tilPassword;
    private TextInputEditText etUsername, etPassword;
    private MaterialCheckBox cbRememberMe;
    private AppCompatButton btnSignIn;
    private View tvRegisterLink, tvForgotPasswordLink;

    private static final String PREFS_NAME = "DentAssistPrefs";
    private static final String KEY_SAVED_USERNAME = "saved_username";
    private static final String KEY_REMEMBER_ME = "remember_me";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        initViews();
        loadSavedCredentials();
        setupClickListeners();
    }

    private void initViews() {
        tilUsername = findViewById(R.id.tilUsername);
        tilPassword = findViewById(R.id.tilPassword);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        cbRememberMe = findViewById(R.id.cbRememberMe);
        btnSignIn = findViewById(R.id.btnSignIn);
        tvRegisterLink = findViewById(R.id.tvRegisterLink);
        tvForgotPasswordLink = findViewById(R.id.tvForgotPasswordLink);
    }

    private void loadSavedCredentials() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (prefs.getBoolean(KEY_REMEMBER_ME, false)) {
            etUsername.setText(prefs.getString(KEY_SAVED_USERNAME, ""));
            cbRememberMe.setChecked(true);
        }
    }

    private void saveCredentials(String username) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (cbRememberMe.isChecked()) {
            prefs.edit()
                    .putString(KEY_SAVED_USERNAME, username)
                    .putBoolean(KEY_REMEMBER_ME, true)
                    .apply();
        } else {
            prefs.edit().clear().apply();
        }
    }

    private void setupClickListeners() {
        btnSignIn.setOnClickListener(v -> performLogin());
        tvRegisterLink.setOnClickListener(v -> startActivity(new Intent(this, Register.class)));
        tvForgotPasswordLink.setOnClickListener(v -> startActivity(new Intent(this, Forgot_Pass.class)));
    }

    private void performLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        clearErrors();

        if (TextUtils.isEmpty(username)) {
            tilUsername.setError("Username is required");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Password is required");
            return;
        }

        setLoading(true);

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        LoginRequest request = new LoginRequest(username, password, cbRememberMe.isChecked());

        apiService.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                setLoading(false);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    LoginResponse body = response.body();

                    // Save credentials
                    saveCredentials(username);

                    // Save session
                    SessionManager session = new SessionManager(Login.this);
                    session.saveSession(body.getToken(), body.getUser_id(),
                            body.getPatient_id(), body.getUsername());

                    // Success toast
                    Toast.makeText(Login.this, "\"" + body.getUsername() + "\" logged in", Toast.LENGTH_LONG).show();

                    // Navigate to dashboard
                    startActivity(new Intent(Login.this, Dashboard.class));
                    finish();
                } else {
                    String error = (response.body() != null) ? response.body().getMessage() : "Login failed";
                    Toast.makeText(Login.this, error, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                setLoading(false);
                Toast.makeText(Login.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void clearErrors() {
        tilUsername.setError(null);
        tilPassword.setError(null);
    }

    private void setLoading(boolean loading) {
        btnSignIn.setEnabled(!loading);
        btnSignIn.setText(loading ? "Signing in..." : "Sign In");
    }
}