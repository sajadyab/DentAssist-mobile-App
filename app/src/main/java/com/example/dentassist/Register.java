package com.example.dentassist;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;

import com.example.dentassist.app.network.ApiService;
import com.example.dentassist.app.network.RetrofitClient;
import com.example.dentassist.models.RegisterRequest;
import com.example.dentassist.models.RegisterResponse;
import com.example.dentassist.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register extends AppCompatActivity {

    // ==================== FORM INPUT LAYOUTS ====================
    private TextInputLayout tilFullName;
    private TextInputLayout tilDob;
    private TextInputLayout tilUsername;
    private TextInputLayout tilPassword;
    private TextInputLayout tilConfirmPassword;
    private TextInputLayout tilReferral;

    // ==================== FORM INPUT FIELDS ====================
    private TextInputEditText etFullName;
    private TextInputEditText etDob;
    private TextInputEditText etUsername;
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmPassword;
    private TextInputEditText etReferral;

    // ==================== PHONE INPUT ====================
    private Spinner spinnerCountryCode;
    private TextInputEditText etPhoneNumber;
    private PhoneNumberUtil phoneUtil;

    // ==================== BUTTONS ====================
    private AppCompatButton btnRegister;
    private View tvSignInLink;
    private View tvFooterSignIn;

    // ==================== DATE PICKER ====================
    private Calendar calendar;

    // ==================== COUNTRY DATA ====================
    private static final String[] COUNTRY_DIAL_CODES = {
            "961", "1", "1", "44", "33", "49", "39", "34", "86", "91", "92", "81", "82", "61", "55", "20", "27", "971", "966"
    };

    private static final String[] COUNTRY_ISO_CODES = {
            "LB", "US", "CA", "GB", "FR", "DE", "IT", "ES", "CN", "IN", "PK", "JP", "KR", "AU", "BR", "EG", "ZA", "AE", "SA"
    };

    private static final java.util.Map<String, Integer> PHONE_LIMITS = new java.util.HashMap<String, Integer>() {{
        put("LB", 8); put("US", 10); put("CA", 10); put("GB", 10); put("FR", 9);
        put("DE", 10); put("IT", 10); put("ES", 9); put("CN", 11); put("IN", 10);
        put("PK", 10); put("JP", 10); put("KR", 11); put("AU", 9); put("BR", 11);
        put("EG", 10); put("ZA", 9); put("AE", 9); put("SA", 9);
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        phoneUtil = PhoneNumberUtil.getInstance();
        calendar = Calendar.getInstance();

        initViews();
        setupSpinner();
        setupPhoneNumberLimit();
        setupDatePicker();
        setupClickListeners();
    }

    private void initViews() {
        tilFullName = findViewById(R.id.tilFullName);
        tilDob = findViewById(R.id.tilDob);
        tilUsername = findViewById(R.id.tilUsername);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        tilReferral = findViewById(R.id.tilReferral);

        etFullName = findViewById(R.id.etFullName);
        etDob = findViewById(R.id.etDob);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etReferral = findViewById(R.id.etReferral);

        spinnerCountryCode = findViewById(R.id.spinnerCountryCode);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);

        btnRegister = findViewById(R.id.btnRegister);
        tvSignInLink = findViewById(R.id.tvSignInLink);
        tvFooterSignIn = findViewById(R.id.tvFooterSignIn);
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.country_codes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCountryCode.setAdapter(adapter);
        spinnerCountryCode.setSelection(0); // Default to Lebanon
    }

    private void setupPhoneNumberLimit() {
        spinnerCountryCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String isoCode = COUNTRY_ISO_CODES[position];
                Integer limit = PHONE_LIMITS.getOrDefault(isoCode, 15);
                etPhoneNumber.setFilters(new android.text.InputFilter[]{
                        new android.text.InputFilter.LengthFilter(limit)
                });
                String current = etPhoneNumber.getText().toString();
                if (current.length() > limit) {
                    etPhoneNumber.setText(current.substring(0, limit));
                    etPhoneNumber.setSelection(limit);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        etPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (!text.matches("^\\d*$")) {
                    String filtered = text.replaceAll("[^\\d]", "");
                    etPhoneNumber.setText(filtered);
                    etPhoneNumber.setSelection(filtered.length());
                }
            }
        });
    }

    private void setupDatePicker() {
        etDob.setOnClickListener(v -> showDatePicker());
        tilDob.setEndIconOnClickListener(v -> showDatePicker());
    }

    private void showDatePicker() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        String existingDate = etDob.getText().toString().trim();
        if (!existingDate.isEmpty() && existingDate.matches("\\d{2}/\\d{2}/\\d{4}")) {
            String[] parts = existingDate.split("/");
            day = Integer.parseInt(parts[0]);
            month = Integer.parseInt(parts[1]) - 1;
            year = Integer.parseInt(parts[2]);
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, android.R.style.Theme_DeviceDefault_Light_Dialog,
                (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
                    String formattedDate = String.format("%02d/%02d/%04d",
                            selectedDay, selectedMonth + 1, selectedYear);
                    etDob.setText(formattedDate);
                }, year, month, day);

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private boolean isValidDate(String dateStr) {
        if (TextUtils.isEmpty(dateStr)) return false;
        try {
            String[] parts = dateStr.split("/");
            int day = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int year = Integer.parseInt(parts[2]);
            Calendar cal = Calendar.getInstance();
            cal.setLenient(false);
            cal.set(year, month - 1, day);
            cal.getTime();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void setupClickListeners() {
        btnRegister.setOnClickListener(v -> performRegister());
        if (tvSignInLink != null) tvSignInLink.setOnClickListener(v -> navigateToLogin());
        if (tvFooterSignIn != null) tvFooterSignIn.setOnClickListener(v -> navigateToLogin());
    }

    private void performRegister() {
        String fullName = getFullName();
        String dob = getDob();
        String username = getUsername();
        String password = getPassword();
        String confirmPassword = getConfirmPassword();
        String referral = getReferral();
        String phoneNumber = getPhoneNumber();

        clearErrors();

        // Validation
        if (TextUtils.isEmpty(fullName)) { tilFullName.setError("Full name is required"); return; }
        if (TextUtils.isEmpty(dob)) { tilDob.setError("Date of birth is required"); return; }
        if (!dob.matches("^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/\\d{4}$")) { tilDob.setError("Use format dd/mm/yyyy"); return; }
        if (!isValidDate(dob)) { tilDob.setError("Invalid date"); return; }
        if (TextUtils.isEmpty(username)) { tilUsername.setError("Username is required"); return; }
        if (TextUtils.isEmpty(password)) { tilPassword.setError("Password is required"); return; }
        if (password.length() < 6) { tilPassword.setError("Password must be at least 6 characters"); return; }
        if (!password.equals(confirmPassword)) { tilConfirmPassword.setError("Passwords do not match"); return; }
        if (TextUtils.isEmpty(phoneNumber)) { Toast.makeText(this, "Phone number is required", Toast.LENGTH_SHORT).show(); return; }

        int position = spinnerCountryCode.getSelectedItemPosition();
        String dialCode = COUNTRY_DIAL_CODES[position];
        String isoCode = COUNTRY_ISO_CODES[position];
        String fullPhoneNumber = "+" + dialCode + phoneNumber;

        int expectedLength = PHONE_LIMITS.getOrDefault(isoCode, 15);
        if (phoneNumber.length() != expectedLength) {
            Toast.makeText(this, "Phone number must be " + expectedLength + " digits", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Phonenumber.PhoneNumber parsed = phoneUtil.parse(fullPhoneNumber, isoCode);
            if (!phoneUtil.isValidNumber(parsed)) {
                Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberParseException e) {
            Toast.makeText(this, "Invalid phone number format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert date from dd/mm/yyyy to yyyy-mm-dd for backend
        String[] dateParts = dob.split("/");
        String dbFormattedDob = dateParts[2] + "-" + dateParts[1] + "-" + dateParts[0];

        // Show loading
        setLoading(true);

        // ==================== REAL API CALL ====================
        RegisterRequest request = new RegisterRequest(
                fullName, username, username + "@dentassist.com", // email from username
                password, confirmPassword, dbFormattedDob, fullPhoneNumber, referral
        );

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.register(request).enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                setLoading(false);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    RegisterResponse body = response.body();

                    if (body.isRedirect_to_login()) {
                        Toast.makeText(Register.this, body.getMessage(), Toast.LENGTH_LONG).show();
                        navigateToLogin();
                    } else {
                        // Auto-login success
                        SessionManager session = new SessionManager(Register.this);
                        session.saveSession(body.getToken(), body.getUser_id(),
                                body.getPatient_id(), body.getUsername());

                        Toast.makeText(Register.this, "Welcome " + body.getFull_name() + "!", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(Register.this, Dashboard.class));
                        finish();
                    }
                } else {
                    String error = (response.body() != null) ? response.body().getMessage() : "Registration failed";
                    Toast.makeText(Register.this, error, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                setLoading(false);
                Toast.makeText(Register.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void clearErrors() {
        if (tilFullName != null) tilFullName.setError(null);
        if (tilDob != null) tilDob.setError(null);
        if (tilUsername != null) tilUsername.setError(null);
        if (tilPassword != null) tilPassword.setError(null);
        if (tilConfirmPassword != null) tilConfirmPassword.setError(null);
        if (tilReferral != null) tilReferral.setError(null);
    }

    private void setLoading(boolean loading) {
        btnRegister.setEnabled(!loading);
        btnRegister.setText(loading ? "Registering..." : "Register");
    }

    private void navigateToLogin() {
        startActivity(new Intent(this, Login.class));
        finish();
    }

    // ==================== GETTER METHODS ====================

    public String getFullName() { return etFullName != null ? etFullName.getText().toString().trim() : ""; }
    public String getDob() { return etDob != null ? etDob.getText().toString().trim() : ""; }
    public String getUsername() { return etUsername != null ? etUsername.getText().toString().trim() : ""; }
    public String getPassword() { return etPassword != null ? etPassword.getText().toString().trim() : ""; }
    public String getConfirmPassword() { return etConfirmPassword != null ? etConfirmPassword.getText().toString().trim() : ""; }
    public String getReferral() { return etReferral != null ? etReferral.getText().toString().trim() : ""; }
    public String getPhoneNumber() { return etPhoneNumber != null ? etPhoneNumber.getText().toString().trim() : ""; }
}