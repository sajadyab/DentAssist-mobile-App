package com.example.dentassist;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.example.dentassist.app.network.ApiService;
import com.example.dentassist.app.network.RetrofitClient;
import com.example.dentassist.models.BillsResponse;
import com.example.dentassist.utils.SessionManager;
import com.google.android.material.card.MaterialCardView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;





public class Bills extends BaseDrawerActivity {

    // Header stats
    private TextView tvBalanceDue, tvTotalPayments, tvTotalPaid, tvBalanceDueSmall;

    // Treatment invoices
    private LinearLayout layoutNoInvoices, layoutInvoicesContainer;
    private AppCompatButton btnBookAppointment;

    // Subscription payments
    private LinearLayout layoutNoSubscriptions, layoutSubscriptionsContainer;
    private AppCompatButton btnSubscribeNow;

    // Help
    private AppCompatButton btnChatWhatsApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupDrawer(R.layout.bills);

        initViews();
        setupClickListeners();
        fetchBillsData();
    }

    private void initViews() {
        tvBalanceDue = findViewById(R.id.tvBalanceDue);
        tvTotalPayments = findViewById(R.id.tvTotalPayments);
        tvTotalPaid = findViewById(R.id.tvTotalPaid);
        tvBalanceDueSmall = findViewById(R.id.tvBalanceDueSmall);

        layoutNoInvoices = findViewById(R.id.layoutNoInvoices);
        layoutInvoicesContainer = findViewById(R.id.layoutInvoicesContainer);
        btnBookAppointment = findViewById(R.id.btnBookAppointment);

        layoutNoSubscriptions = findViewById(R.id.layoutNoSubscriptions);
        layoutSubscriptionsContainer = findViewById(R.id.layoutSubscriptionsContainer);
        btnSubscribeNow = findViewById(R.id.btnSubscribeNow);

        btnChatWhatsApp = findViewById(R.id.btnChatWhatsApp);
    }

    private void setupClickListeners() {
        btnBookAppointment.setOnClickListener(v -> {
            startActivity(new Intent(this, BookAppointment.class));
        });
        btnSubscribeNow.setOnClickListener(v -> {
            startActivity(new Intent(this, Subscription.class));
        });
    }

    private void fetchBillsData() {
        SessionManager session = new SessionManager(this);
        if (!session.isLoggedIn()) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, Login.class));
            finish();
            return;
        }

        String token = "Bearer " + session.getToken();
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.getBills(token).enqueue(new Callback<BillsResponse>() {
            @Override
            public void onResponse(Call<BillsResponse> call, Response<BillsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    BillsResponse.BillsData data = response.body().getData();
                    if (data != null) {
                        updateStats(data.getStats());
                        populateInvoices(data.getInvoices());
                        populateSubscriptions(data.getSubscriptions());
                        setupHelp(data.getClinic_phone());
                    }
                } else {
                    String error = (response.body() != null) ? response.body().getMessage() : "Failed to load bills";
                    Toast.makeText(Bills.this, error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BillsResponse> call, Throwable t) {
                Toast.makeText(Bills.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateStats(BillsResponse.Stats stats) {
        if (stats == null) return;
        double total = stats.getTotal_paid() + stats.getBalance_due();
        tvTotalPayments.setText(formatCurrency(total));
        tvTotalPaid.setText(formatCurrency(stats.getTotal_paid()));
        tvBalanceDue.setText(formatCurrency(stats.getBalance_due()));
        tvBalanceDueSmall.setText(String.valueOf(stats.getTotal_invoices()));
    }

    private void populateInvoices(List<BillsResponse.Invoice> invoices) {
        layoutInvoicesContainer.removeAllViews();

        if (invoices == null || invoices.isEmpty()) {
            layoutNoInvoices.setVisibility(View.VISIBLE);
            layoutInvoicesContainer.setVisibility(View.GONE);
            return;
        }

        layoutNoInvoices.setVisibility(View.GONE);
        layoutInvoicesContainer.setVisibility(View.VISIBLE);

        for (BillsResponse.Invoice inv : invoices) {
            MaterialCardView card = createInvoiceCard(inv);
            layoutInvoicesContainer.addView(card);
        }
    }

    private MaterialCardView createInvoiceCard(BillsResponse.Invoice inv) {
        MaterialCardView card = new MaterialCardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(0, 0, 0, 12);
        card.setLayoutParams(cardParams);
        card.setCardElevation(2);
        card.setRadius(16);
        card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.surface_white));
        card.setContentPadding(16, 16, 16, 16);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);

        // Invoice number - PRIMARY BLUE, BOLD
        TextView tvNumber = new TextView(this);
        tvNumber.setText(inv.getInvoice_number());
        tvNumber.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        tvNumber.setTextSize(13);
        tvNumber.setTypeface(null, android.graphics.Typeface.BOLD);
        tvNumber.setGravity(Gravity.CENTER);
        layout.addView(tvNumber);

        // Date info - PRIMARY TEXT
        TextView tvDate = new TextView(this);
        String dateInfo = formatDisplayDate(inv.getInvoice_date()) + "\nDue " + formatDisplayDate(inv.getDue_date())
                + " · Total " + formatCurrency(inv.getSubtotal()) + " · Paid " + formatCurrency(inv.getPaid_amount());
        tvDate.setText(dateInfo);
        tvDate.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        tvDate.setTextSize(11);
        tvDate.setGravity(Gravity.CENTER);
        tvDate.setPadding(0, 4, 0, 8);
        layout.addView(tvDate);

        // Balance - GREEN if $0.00, RED if >$0.00
        TextView tvBalance = new TextView(this);
        tvBalance.setText(formatCurrency(inv.getBalance_due()) + " due");
        if (inv.getBalance_due() <= 0.0) {
            tvBalance.setTextColor(ContextCompat.getColor(this, R.color.accent_green));
        } else {
            tvBalance.setTextColor(ContextCompat.getColor(this, R.color.accent_red));
        }
        tvBalance.setTextSize(16);
        tvBalance.setTypeface(null, android.graphics.Typeface.BOLD);
        tvBalance.setGravity(Gravity.CENTER);
        tvBalance.setPadding(0, 0, 0, 4);
        layout.addView(tvBalance);

        // Status - PRIMARY TEXT, BOLD
        TextView tvStatus = new TextView(this);
        tvStatus.setText(capitalizeFirst(inv.getPayment_status()));
        tvStatus.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        tvStatus.setTextSize(13);
        tvStatus.setTypeface(null, android.graphics.Typeface.BOLD);
        tvStatus.setGravity(Gravity.CENTER);
        tvStatus.setPadding(0, 0, 0, 8);
        layout.addView(tvStatus);

        // View button - using TextView for reliability
        TextView btnView = new TextView(this);
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        btnView.setLayoutParams(btnParams);
        btnView.setText("View");
        btnView.setTextColor(android.graphics.Color.WHITE);
        btnView.setTextSize(14);
        btnView.setTypeface(null, android.graphics.Typeface.BOLD);
        btnView.setGravity(Gravity.CENTER);
        btnView.setBackgroundResource(R.drawable.button_primary_gradient);
        btnView.setPadding(0, 14, 0, 14);
        btnView.setClickable(true);
        btnView.setFocusable(true);
        btnView.setOnClickListener(v -> {
            Intent intent = new Intent(Bills.this, ViewInvoiceActivity.class);
            intent.putExtra("invoice_id", inv.getId());
            startActivity(intent);
        });
        layout.addView(btnView);

        card.addView(layout);
        return card;
    }

    private void populateSubscriptions(List<BillsResponse.Subscription> subscriptions) {
        layoutSubscriptionsContainer.removeAllViews();

        if (subscriptions == null || subscriptions.isEmpty()) {
            layoutNoSubscriptions.setVisibility(View.VISIBLE);
            layoutSubscriptionsContainer.setVisibility(View.GONE);
            return;
        }

        layoutNoSubscriptions.setVisibility(View.GONE);
        layoutSubscriptionsContainer.setVisibility(View.VISIBLE);

        for (BillsResponse.Subscription sub : subscriptions) {
            LinearLayout row = new LinearLayout(this);
            row.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            row.setOrientation(LinearLayout.VERTICAL);
            row.setPadding(0, 12, 0, 12);
            row.setGravity(Gravity.CENTER);

            // Amount - GREEN, BOLD
            TextView tvAmount = new TextView(this);
            tvAmount.setText(formatCurrency(sub.getAmount()));
            tvAmount.setTextColor(ContextCompat.getColor(this, R.color.accent_green));
            tvAmount.setTextSize(16);
            tvAmount.setTypeface(null, android.graphics.Typeface.BOLD);
            tvAmount.setGravity(Gravity.CENTER);
            row.addView(tvAmount);

            // Plan type - PRIMARY
            TextView tvPlan = new TextView(this);
            tvPlan.setText(capitalizeFirst(sub.getSubscription_type()) + " plan");
            tvPlan.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            tvPlan.setTextSize(13);
            tvPlan.setGravity(Gravity.CENTER);
            row.addView(tvPlan);

            // Payment method and date - PRIMARY
            TextView tvInfo = new TextView(this);
            tvInfo.setText(capitalizeFirst(sub.getPayment_method()) + " · " + formatDisplayDate(sub.getPayment_date()));
            tvInfo.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            tvInfo.setTextSize(11);
            tvInfo.setGravity(Gravity.CENTER);
            tvInfo.setPadding(0, 4, 0, 4);
            row.addView(tvInfo);

            // Status - PRIMARY, BOLD
            TextView tvStatus = new TextView(this);
            tvStatus.setText(capitalizeFirst(sub.getStatus()));
            tvStatus.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            tvStatus.setTextSize(12);
            tvStatus.setTypeface(null, android.graphics.Typeface.BOLD);
            tvStatus.setGravity(Gravity.CENTER);
            row.addView(tvStatus);

            // Reference - PRIMARY
            String ref = sub.getPayment_reference() != null && !sub.getPayment_reference().isEmpty()
                    ? sub.getPayment_reference() : "—";
            TextView tvRef = new TextView(this);
            tvRef.setText("Ref: " + ref);
            tvRef.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            tvRef.setTextSize(11);
            tvRef.setGravity(Gravity.CENTER);
            tvRef.setPadding(0, 4, 0, 0);
            row.addView(tvRef);

            layoutSubscriptionsContainer.addView(row);

            View divider = new View(this);
            divider.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 1));
            divider.setBackgroundColor(ContextCompat.getColor(this, R.color.divider));
            layoutSubscriptionsContainer.addView(divider);
        }
    }

    private void setupHelp(String phone) {
        if (phone != null && !phone.isEmpty()) {
            String cleanPhone = phone.replaceAll("[^0-9+]", "");
            String whatsappUrl = "https://wa.me/" + cleanPhone.replace("+", "");
            btnChatWhatsApp.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(whatsappUrl));
                startActivity(intent);
            });
            btnChatWhatsApp.setVisibility(View.VISIBLE);
        } else {
            btnChatWhatsApp.setVisibility(View.GONE);
        }
    }

    private String formatCurrency(double amount) {
        return NumberFormat.getCurrencyInstance(Locale.US).format(amount);
    }

    private String formatDisplayDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return "";
        try {
            // Handle both "2026-04-25" and "2026-04-25T12:00:37" formats
            String dateOnly = dateStr.contains("T") ? dateStr.substring(0, dateStr.indexOf("T")) : dateStr;
            String[] parts = dateOnly.split("-");
            if (parts.length == 3) {
                String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
                return months[Integer.parseInt(parts[1]) - 1] + " " + Integer.parseInt(parts[2]) + ", " + parts[0];
            }
        } catch (Exception e) {}
        return dateStr;
    }

    private String capitalizeFirst(String text) {
        if (text == null || text.isEmpty()) return "";
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    private int getStatusColor(String status) {
        if (status == null) return R.color.text_primary;
        switch (status.toLowerCase()) {
            case "paid": return R.color.accent_green;
            case "pending": return R.color.primary;
            case "overdue": return R.color.accent_red;
            case "partial": return R.color.accent_yellow;
            default: return R.color.text_primary;
        }
    }
}