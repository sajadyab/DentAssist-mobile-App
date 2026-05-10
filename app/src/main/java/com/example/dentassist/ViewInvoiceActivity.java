package com.example.dentassist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.dentassist.app.network.ApiService;
import com.example.dentassist.app.network.RetrofitClient;
import com.example.dentassist.models.InvoiceDetailResponse;
import com.example.dentassist.utils.SessionManager;

import java.text.NumberFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewInvoiceActivity extends BaseDrawerActivity {

    private TextView tvInvoiceNumber, tvHeaderStatus, tvHeaderBalance, tvHeaderDueDate;
    private TextView tvDetailInvoiceNumber, tvDetailStatus, tvDetailInvoiceDate,
            tvDetailDueDate, tvDetailPatientName, tvDetailTotal;
    private TextView tvDetailTreatment, tvDetailApptDate;
    private View layoutTreatment, layoutApptDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setupDrawer(R.layout.activity_view_invoice);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Invoice details");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Bind views
        tvInvoiceNumber = findViewById(R.id.tvInvoiceNumber);
        tvHeaderStatus = findViewById(R.id.tvHeaderStatus);
        tvHeaderBalance = findViewById(R.id.tvHeaderBalance);
        tvHeaderDueDate = findViewById(R.id.tvHeaderDueDate);

        tvDetailInvoiceNumber = findViewById(R.id.tvDetailInvoiceNumber);
        tvDetailStatus = findViewById(R.id.tvDetailStatus);
        tvDetailInvoiceDate = findViewById(R.id.tvDetailInvoiceDate);
        tvDetailDueDate = findViewById(R.id.tvDetailDueDate);
        tvDetailPatientName = findViewById(R.id.tvDetailPatientName);
        tvDetailTotal = findViewById(R.id.tvDetailTotal);
        tvDetailTreatment = findViewById(R.id.tvDetailTreatment);
        tvDetailApptDate = findViewById(R.id.tvDetailApptDate);
        layoutTreatment = findViewById(R.id.layoutTreatment);
        layoutApptDate = findViewById(R.id.layoutApptDate);

        int invoiceId = getIntent().getIntExtra("invoice_id", 0);
        if (invoiceId > 0) {
            loadInvoiceDetail(invoiceId);
        } else {
            Toast.makeText(this, "Invalid invoice", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadInvoiceDetail(int invoiceId) {
        SessionManager session = new SessionManager(this);
        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, Login.class));
            finish();
            return;
        }

        String token = "Bearer " + session.getToken();
        RetrofitClient.getClient().create(ApiService.class)
                .getInvoiceDetail(token, invoiceId)
                .enqueue(new Callback<InvoiceDetailResponse>() {
                    @Override
                    public void onResponse(Call<InvoiceDetailResponse> call, Response<InvoiceDetailResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            populateUI(response.body().getData());
                        } else {
                            Toast.makeText(ViewInvoiceActivity.this, "Failed to load invoice", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<InvoiceDetailResponse> call, Throwable t) {
                        Toast.makeText(ViewInvoiceActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void populateUI(InvoiceDetailResponse.InvoiceData data) {
        tvInvoiceNumber.setText(data.getInvoice_number());
        tvHeaderStatus.setText(capitalize(data.getPayment_status()));
        tvHeaderBalance.setText(formatCurrency(data.getBalance_due()) + " balance");
        tvHeaderDueDate.setText("Due " + formatDate(data.getDue_date()));

        tvDetailInvoiceNumber.setText(data.getInvoice_number());
        tvDetailStatus.setText(capitalize(data.getPayment_status()));
        tvDetailInvoiceDate.setText(formatDate(data.getInvoice_date()));
        tvDetailDueDate.setText(formatDate(data.getDue_date()));
        tvDetailPatientName.setText(data.getPatient_name());
        tvDetailTotal.setText(formatCurrency(data.getTotal()));

        // Optional treatment details
        if (data.getTreatment_type() != null && !data.getTreatment_type().isEmpty()) {
            layoutTreatment.setVisibility(View.VISIBLE);
            tvDetailTreatment.setText(data.getTreatment_type());
            if (data.getAppointment_date() != null && !data.getAppointment_date().isEmpty()) {
                layoutApptDate.setVisibility(View.VISIBLE);
                tvDetailApptDate.setText(formatDate(data.getAppointment_date()));
            } else {
                layoutApptDate.setVisibility(View.GONE);
            }
        } else {
            layoutTreatment.setVisibility(View.GONE);
            layoutApptDate.setVisibility(View.GONE);
        }
    }

    private String formatCurrency(double amount) {
        return NumberFormat.getCurrencyInstance(Locale.US).format(amount);
    }

    private String formatDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return "";
        try {
            String[] parts = dateStr.split("-");
            if (parts.length == 3) {
                String[] months = {"Jan","Feb","Mar","Apr","May","Jun",
                        "Jul","Aug","Sep","Oct","Nov","Dec"};
                int monthIndex = Integer.parseInt(parts[1]) - 1;
                if (monthIndex >= 0 && monthIndex < 12) {
                    return months[monthIndex] + " " + Integer.parseInt(parts[2]) + ", " + parts[0];
                }
            }
        } catch (Exception e) {
            // fall through
        }
        return dateStr;  // always returns
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return "";
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}