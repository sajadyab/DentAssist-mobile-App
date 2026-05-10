package com.example.dentassist;

import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.example.dentassist.models.ChangePasswordRequest;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;

import com.example.dentassist.app.network.ApiService;
import com.example.dentassist.app.network.RetrofitClient;
import com.example.dentassist.models.GenericResponse;
import com.example.dentassist.models.ProfileResponse;
import com.example.dentassist.models.ProfileUpdateRequest;
import com.example.dentassist.utils.SessionManager;
import com.example.dentassist.views.PhoneInputView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Profile extends BaseDrawerActivity {

    // ==================== HEADER VIEWS ====================
    private TextView tvPatientName;
    private TextView tvUsername;
    private TextView tvPhoneNbr;
    private TextView tvEmail;
    private TextView tvNbrOfPoints;
    private TextView tvReferralCodeVal;
    private TextView tvReferralCode;

    // ==================== FORM INPUT FIELDS ====================
    private TextInputLayout tilFullName, tilProfileUsername, tilDob, tilEmail, tilAddress, tilEmergencyName, tilRelationship;
    private TextInputEditText etFullName, etProfileUsername, etDob, etEmail, etAddress, etEmergencyName, etRelationship;

    // ==================== CHANGE PASSWORD FIELDS ====================
    private TextInputEditText etCurrentPassword, etNewPassword, etConfirmPassword;
    private CheckBox cbShowPasswords;
    private AppCompatButton btnChangePassword;

    // ==================== PHONE INPUTS ====================
    private PhoneInputView phoneInputContact;
    private PhoneInputView phoneInputEmergency;

    // ==================== GENDER ====================
    private RadioGroup radioGroupGender;
    private RadioButton radioFemale, radioMale;

    // ==================== BUTTONS ====================
    private AppCompatButton btnSaveProfile;
    private AppCompatButton btnCopyReferral;

    // ==================== CALENDAR ====================
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setupDrawer(R.layout.profile);

        calendar = Calendar.getInstance();
        initViews();
        setupDatePicker();
        setupClickListeners();
        loadProfileData();
    }

    private void initViews() {
        // ==================== HEADER VIEWS ====================
        tvPatientName = findViewById(R.id.tvPatientName);
        tvUsername = findViewById(R.id.tvUsername);
        tvPhoneNbr = findViewById(R.id.tvPhoneNbr);
        tvEmail = findViewById(R.id.tvEmail);
        tvNbrOfPoints = findViewById(R.id.tvNbrOfPoints);
        tvReferralCodeVal = findViewById(R.id.tvReferralCodeVal);
        tvReferralCode = findViewById(R.id.tvReferralCode);

        // ==================== FORM INPUT FIELDS ====================
        tilFullName = findViewById(R.id.tilFullName);
        tilProfileUsername = findViewById(R.id.tilProfileUsername);
        tilDob = findViewById(R.id.tilDob);
        tilEmail = findViewById(R.id.tilEmail);
        tilAddress = findViewById(R.id.tilAddress);
        tilEmergencyName = findViewById(R.id.tilEmergencyName);
        tilRelationship = findViewById(R.id.tilRelationship);

        etFullName = findViewById(R.id.etFullName);
        etProfileUsername = findViewById(R.id.etProfileUsername);
        etDob = findViewById(R.id.etDob);
        etEmail = findViewById(R.id.etEmail);
        etAddress = findViewById(R.id.etAddress);
        etEmergencyName = findViewById(R.id.etEmergencyName);
        etRelationship = findViewById(R.id.etRelationship);

        // ==================== CHANGE PASSWORD FIELDS ====================
        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        cbShowPasswords = findViewById(R.id.cbShowPasswords);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        // ==================== PHONE INPUTS ====================
        phoneInputContact = findViewById(R.id.phoneInputContact);
        phoneInputEmergency = findViewById(R.id.phoneInputEmergency);

        // ==================== GENDER ====================
        radioGroupGender = findViewById(R.id.radioGroupGender);
        radioFemale = findViewById(R.id.radioFemale);
        radioMale = findViewById(R.id.radioMale);
        if (radioGroupGender != null) {
            radioGroupGender.clearCheck();
        }

        // ==================== BUTTONS ====================
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        btnCopyReferral = findViewById(R.id.buttonCopyCode);
    }

    private void setupDatePicker() {
        if (etDob != null) {
            etDob.setOnClickListener(v -> showDatePicker());
        }
        if (tilDob != null) {
            tilDob.setEndIconOnClickListener(v -> showDatePicker());
        }
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
                this,
                android.R.style.Theme_DeviceDefault_Light_Dialog,
                (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
                    String formattedDate = String.format("%02d/%02d/%04d",
                            selectedDay, selectedMonth + 1, selectedYear);
                    etDob.setText(formattedDate);
                },
                year, month, day);

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void setupClickListeners() {
        if (btnSaveProfile != null) {
            btnSaveProfile.setOnClickListener(v -> saveProfile());
        }

        if (btnChangePassword != null) {
            btnChangePassword.setOnClickListener(v -> changePassword());
        }

        if (btnCopyReferral != null) {
            btnCopyReferral.setOnClickListener(v -> copyReferralCode());
        }

        if (cbShowPasswords != null) {
            cbShowPasswords.setOnCheckedChangeListener((buttonView, isChecked) -> {
                togglePasswordVisibility(isChecked);
            });
        }
    }

    // ==================== LOAD PROFILE DATA ====================

    private void loadProfileData() {
        SessionManager session = new SessionManager(this);
        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, Login.class));
            finish();
            return;
        }

        String token = "Bearer " + session.getToken();
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        apiService.getProfile(token).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    ProfileResponse.ProfileData data = response.body().getData();
                    if (data != null) {
                        populateUI(data);
                    }
                } else {
                    Toast.makeText(Profile.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                Toast.makeText(Profile.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    private String formatDateForDisplay(String dbDate) {
        if (dbDate == null || dbDate.isEmpty()) return "";
        try {
            String[] parts = dbDate.split("-");
            if (parts.length == 3) {
                return parts[2] + "/" + parts[1] + "/" + parts[0];
            }
        } catch (Exception e) {}
        return dbDate;
    }

    // ==================== SAVE PROFILE ====================

    private void saveProfile() {
        String fullName = getFullName();
        String username = getUsername();
        String dob = getDob();
        String email = getEmail();
        String address = getAddress();
        String emergencyName = getEmergencyName();
        String relationship = getRelationship();
        String gender = getGender();

        clearErrors();

        // Validations
        if (TextUtils.isEmpty(dob)) {
            tilDob.setError("Date of birth is required");
            return;
        }
        if (!dob.matches("^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/\\d{4}$")) {
            tilDob.setError("Use format dd/mm/yyyy");
            return;
        }
        if (!isValidDate(dob)) {
            tilDob.setError("Invalid date");
            return;
        }

        if (TextUtils.isEmpty(fullName)) {
            tilFullName.setError("Full name is required");
            return;
        }

        if (TextUtils.isEmpty(username)) {
            tilProfileUsername.setError("Username is required");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Email is required");
            return;
        }
        if (!isValidEmail(email)) {
            tilEmail.setError("Invalid email format");
            return;
        }

        if (phoneInputContact == null || phoneInputContact.isEmpty()) {
            Toast.makeText(this, "Contact phone number is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!phoneInputContact.isValid()) {
            Toast.makeText(this, "Please enter a valid contact phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phoneInputEmergency != null && !phoneInputEmergency.isEmpty() && !phoneInputEmergency.isValid()) {
            Toast.makeText(this, "Please enter a valid emergency phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);

        // Convert date to DB format
        String dbDob = "";
        if (dob.matches("\\d{2}/\\d{2}/\\d{4}")) {
            String[] parts = dob.split("/");
            dbDob = parts[2] + "-" + parts[1] + "-" + parts[0];
        }

        ProfileUpdateRequest request = new ProfileUpdateRequest(
                fullName, username, dbDob, gender, email,
                phoneInputContact.getFullPhoneNumber(), address,
                emergencyName,
                phoneInputEmergency != null ? phoneInputEmergency.getFullPhoneNumber() : "",
                relationship
        );

        SessionManager session = new SessionManager(this);
        String token = "Bearer " + session.getToken();
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        apiService.updateProfile(token, request).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(Profile.this, "Profile saved successfully!", Toast.LENGTH_SHORT).show();
                    // Update header
                    setPatientName(fullName);
                    setHeaderUsername(username);
                    setPhoneNbr(phoneInputContact.getFullPhoneNumber());
                    setHeaderEmail(email);
                } else {
                    String error = (response.body() != null) ? response.body().getMessage() : "Save failed";
                    Toast.makeText(Profile.this, error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                setLoading(false);
                Toast.makeText(Profile.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLoading(boolean loading) {
        if (btnSaveProfile != null) {
            btnSaveProfile.setEnabled(!loading);
            btnSaveProfile.setText(loading ? "Saving..." : "Save Profile");
        }
    }
    private void populateUI(ProfileResponse.ProfileData data) {
        // Header
        setPatientName(data.getFull_name());
        setHeaderUsername(data.getUsername());
        setPhoneNbr(data.getPhone());
        setHeaderEmail(data.getEmail());
        setNbrOfPoints(data.getPoints());
        setReferralCodeVal(data.getReferral_code());
        setReferralCode(data.getReferral_code());

        // Form fields
        setFullName(data.getFull_name());
        setUsername(data.getUsername());
        setDob(formatDateForDisplay(data.getDate_of_birth()));
        setEmail(data.getEmail());
        setAddress(data.getAddress());
        setGender(data.getGender());
        setEmergencyName(data.getEmergency_contact_name());
        setRelationship(data.getEmergency_contact_relation());

        // Phone inputs - NEW
        if (phoneInputContact != null && data.getPhone() != null && !data.getPhone().isEmpty()) {
            phoneInputContact.setPhoneNumber(data.getPhone());
        }

        if (phoneInputEmergency != null && data.getEmergency_contact_phone() != null && !data.getEmergency_contact_phone().isEmpty()) {
            phoneInputEmergency.setPhoneNumber(data.getEmergency_contact_phone());
        }
    }

    // ==================== CHANGE PASSWORD ====================

    private void changePassword() {
        String currentPassword = etCurrentPassword != null ? etCurrentPassword.getText().toString() : "";
        String newPassword = etNewPassword != null ? etNewPassword.getText().toString() : "";
        String confirmPassword = etConfirmPassword != null ? etConfirmPassword.getText().toString() : "";

        if (TextUtils.isEmpty(currentPassword)) {
            Toast.makeText(this, "Current password is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(newPassword)) {
            Toast.makeText(this, "New password is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (newPassword.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        SessionManager session = new SessionManager(this);
        String token = "Bearer " + session.getToken();
        ChangePasswordRequest request = new ChangePasswordRequest(currentPassword, newPassword);

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.changePassword(token, request).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(Profile.this, "Password changed successfully!", Toast.LENGTH_SHORT).show();
                    etCurrentPassword.setText("");
                    etNewPassword.setText("");
                    etConfirmPassword.setText("");
                } else {
                    String error = (response.body() != null) ? response.body().getMessage() : "Failed";
                    Toast.makeText(Profile.this, error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                Toast.makeText(Profile.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void togglePasswordVisibility(boolean show) {
        int inputType = show ?
                android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD;

        if (etCurrentPassword != null) etCurrentPassword.setInputType(inputType);
        if (etNewPassword != null) etNewPassword.setInputType(inputType);
        if (etConfirmPassword != null) etConfirmPassword.setInputType(inputType);
    }

    // ==================== COPY REFERRAL CODE ====================

    private void copyReferralCode() {
        String code = getReferralCode();
        if (TextUtils.isEmpty(code)) {
            code = tvReferralCodeVal != null ?
                    tvReferralCodeVal.getText().toString().replace("Code: ", "") : "";
        }

        if (!TextUtils.isEmpty(code)) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Referral Code", code);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Referral code copied!", Toast.LENGTH_SHORT).show();
        }
    }

    // ==================== HEADER SETTERS ====================
    public void setPatientName(String name) { if (tvPatientName != null) tvPatientName.setText(name != null ? name : ""); }
    public void setHeaderUsername(String username) { if (tvUsername != null) tvUsername.setText(username != null ? "@" + username : ""); }
    public void setPhoneNbr(String phone) { if (tvPhoneNbr != null) tvPhoneNbr.setText(phone != null ? phone : ""); }
    public void setHeaderEmail(String email) { if (tvEmail != null) tvEmail.setText(email != null ? email : ""); }
    public void setNbrOfPoints(int points) { if (tvNbrOfPoints != null) tvNbrOfPoints.setText(points + " Points"); }
    public void setReferralCodeVal(String code) { if (tvReferralCodeVal != null) tvReferralCodeVal.setText("Code: " + (code != null ? code : "")); }
    public void setReferralCode(String code) { if (tvReferralCode != null) tvReferralCode.setText(code != null ? code : ""); }

    // ==================== HEADER GETTERS ====================
    public String getReferralCode() { return tvReferralCode != null ? tvReferralCode.getText().toString() : ""; }

    // ==================== FORM SETTERS ====================
    public void setFullName(String name) { if (etFullName != null) etFullName.setText(name != null ? name : ""); }
    public void setUsername(String username) { if (etProfileUsername != null) etProfileUsername.setText(username != null ? username : ""); }
    public void setDob(String dob) { if (etDob != null) etDob.setText(dob != null ? dob : ""); }
    public void setEmail(String email) { if (etEmail != null) etEmail.setText(email != null ? email : ""); }
    public void setAddress(String address) { if (etAddress != null) etAddress.setText(address != null ? address : ""); }
    public void setEmergencyName(String name) { if (etEmergencyName != null) etEmergencyName.setText(name != null ? name : ""); }
    public void setRelationship(String rel) { if (etRelationship != null) etRelationship.setText(rel != null ? rel : ""); }
    public void setGender(String gender) {
        if (gender == null) return;
        if (gender.equalsIgnoreCase("female") && radioFemale != null) radioFemale.setChecked(true);
        else if (gender.equalsIgnoreCase("male") && radioMale != null) radioMale.setChecked(true);
    }

    // ==================== FORM GETTERS ====================
    public String getFullName() { return etFullName != null ? etFullName.getText().toString().trim() : ""; }
    public String getUsername() { return etProfileUsername != null ? etProfileUsername.getText().toString().trim() : ""; }
    public String getDob() { return etDob != null ? etDob.getText().toString().trim() : ""; }
    public String getEmail() { return etEmail != null ? etEmail.getText().toString().trim() : ""; }
    public String getAddress() { return etAddress != null ? etAddress.getText().toString().trim() : ""; }
    public String getEmergencyName() { return etEmergencyName != null ? etEmergencyName.getText().toString().trim() : ""; }
    public String getRelationship() { return etRelationship != null ? etRelationship.getText().toString().trim() : ""; }
    public String getGender() {
        if (radioGroupGender != null) {
            int id = radioGroupGender.getCheckedRadioButtonId();
            if (id == R.id.radioFemale) return "female";
            if (id == R.id.radioMale) return "male";
        }
        return null;
    }

    // ==================== HELPERS ====================
    private void clearErrors() {
        if (tilFullName != null) tilFullName.setError(null);
        if (tilProfileUsername != null) tilProfileUsername.setError(null);
        if (tilDob != null) tilDob.setError(null);
        if (tilEmail != null) tilEmail.setError(null);
        if (tilAddress != null) tilAddress.setError(null);
        if (tilEmergencyName != null) tilEmergencyName.setError(null);
        if (tilRelationship != null) tilRelationship.setError(null);
    }

    private boolean isValidDate(String dateStr) {
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

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}