package com.example.dentassist;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;

import com.example.dentassist.app.network.ApiService;
import com.example.dentassist.app.network.RetrofitClient;
import com.example.dentassist.models.GenericResponse;
import com.example.dentassist.models.PaymentConfirmRequest;
import com.example.dentassist.utils.SessionManager;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OwoPaymentActivity extends BaseDrawerActivity {

    private TextView tvPlanName, tvAmount, tvPatientName, tvClinicNumber;
    private AppCompatButton btnOpenWish, btnIvePaid;

    private String clinicOwoNumber;
    private double amount;
    private String plan;
    private String reference;
    private int patientId;
    private String patientName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupDrawer(R.layout.activity_owo_payment);   // unified drawer & toolbar

        // Set only the title – no back arrow, drawer toggle stays
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Online Payment");
        }

        // Bind views
        tvPlanName = findViewById(R.id.tvPlanName);
        tvAmount = findViewById(R.id.tvAmount);
        tvPatientName = findViewById(R.id.tvPatientName);
        tvClinicNumber = findViewById(R.id.tvClinicNumber);
        btnOpenWish = findViewById(R.id.btnOpenWish);
        btnIvePaid = findViewById(R.id.btnIvePaid);

        // Get data from intent
        Intent intent = getIntent();
        clinicOwoNumber = intent.getStringExtra("clinic_owo_number");
        amount = intent.getDoubleExtra("amount", 0);
        plan = intent.getStringExtra("plan");
        reference = intent.getStringExtra("reference");
        patientId = intent.getIntExtra("patient_id", 0);
        patientName = intent.getStringExtra("patient_name");

        // Fill UI
        tvPlanName.setText(capitalize(plan) + " Plan");
        tvAmount.setText(String.format(Locale.US, "$%.2f", amount));
        tvPatientName.setText(patientName);
        tvClinicNumber.setText(clinicOwoNumber);

        // Open Wish/OWO – deep link first, fallback to WhatsApp
        btnOpenWish.setOnClickListener(v -> launchWishOrWhatsApp());

        // Confirm payment
        btnIvePaid.setOnClickListener(v -> confirmPayment());
    }

    private void launchWishOrWhatsApp() {
        // 1. Try Wish/OWO deep link (same as your web page)
        String owoDeepLink = "owo://send?number=" + clinicOwoNumber.replaceAll("[^0-9]", "")
                + "&amount=" + amount
                + "&note=" + reference;

        Intent owoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(owoDeepLink));
        if (owoIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(owoIntent);
            return;
        }

        // 2. Fallback to WhatsApp
        try {
            String message = "I'm making a payment for subscription.\n"
                    + "Reference: " + reference + "\n"
                    + "Amount: $" + amount;
            String whatsappUrl = "https://wa.me/"
                    + clinicOwoNumber.replaceAll("[^0-9]", "")
                    + "?text=" + android.net.Uri.encode(message);
            Intent whatsappIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(whatsappUrl));
            startActivity(whatsappIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Neither Wish nor WhatsApp is available.", Toast.LENGTH_LONG).show();
        }
    }

    private void confirmPayment() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Payment")
                .setMessage("Have you completed the payment?")
                .setPositiveButton("Yes, activate now", (dialog, which) -> {
                    SessionManager session = new SessionManager(this);
                    String token = "Bearer " + session.getToken();
                    PaymentConfirmRequest request = new PaymentConfirmRequest(patientId, plan, amount, reference, "owo");
                    RetrofitClient.getClient().create(ApiService.class)
                            .confirmOwoPayment(token, request)
                            .enqueue(new Callback<GenericResponse>() {
                                @Override
                                public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                                        Toast.makeText(OwoPaymentActivity.this, "Subscription activated!", Toast.LENGTH_LONG).show();
                                        finish();
                                    } else {
                                        // Log the actual error
                                        try {
                                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "empty";
                                            android.util.Log.e("OWO_PAYMENT", "Error: " + response.code() + " - " + errorBody);
                                        } catch (Exception e) { }
                                        Toast.makeText(OwoPaymentActivity.this, "Confirmation failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                @Override
                                public void onFailure(Call<GenericResponse> call, Throwable t) {
                                    android.util.Log.e("OWO_PAYMENT", "Network failure: " + t.getMessage(), t);
                                    Toast.makeText(OwoPaymentActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return "";
        return s.substring(0,1).toUpperCase() + s.substring(1);
    }
}