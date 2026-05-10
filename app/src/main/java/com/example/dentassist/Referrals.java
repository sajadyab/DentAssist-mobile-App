package com.example.dentassist;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.example.dentassist.app.network.ApiService;
import com.example.dentassist.app.network.RetrofitClient;
import com.example.dentassist.models.ReferralsResponse;
import com.example.dentassist.utils.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Referrals extends BaseDrawerActivity {

    private TextView tvReferralFriends;
    private TextView tvReferralCode;
    private AppCompatButton btnShareWhatsApp;
    private AppCompatButton btnCopyCode;

    // Empty state and content
    private LinearLayout layoutEmptyReferrals;
    private LinearLayout layoutReferralContent;
    private LinearLayout layoutReferralRows;

    private String referralCode = "";
    private int totalReferred = 0;
    private int referralFriends = 0;
    private int pointsPerReferral = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setupDrawer(R.layout.referrals);

        initViews();
        setupClickListeners();
        loadReferralsData();
    }

    private void initViews() {
        tvReferralFriends = findViewById(R.id.tvReferralFriends);
        tvReferralCode = findViewById(R.id.tvReferralCode);
        btnShareWhatsApp = findViewById(R.id.btnShareViaWhatsApp);
        btnCopyCode = findViewById(R.id.btnCopyCode);

        // Empty state and content
        layoutEmptyReferrals = findViewById(R.id.layoutEmptyReferrals);
        layoutReferralContent = findViewById(R.id.layoutReferralContent);
        layoutReferralRows = findViewById(R.id.layoutReferralRows);
    }

    private void setupClickListeners() {
        if (btnShareWhatsApp != null) {
            btnShareWhatsApp.setOnClickListener(v -> shareViaWhatsApp());
        }
        if (btnCopyCode != null) {
            btnCopyCode.setOnClickListener(v -> copyReferralCode());
        }
    }

    private void loadReferralsData() {
        SessionManager session = new SessionManager(this);
        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, Login.class));
            finish();
            return;
        }

        String token = "Bearer " + session.getToken();
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        apiService.getReferrals(token).enqueue(new Callback<ReferralsResponse>() {
            @Override
            public void onResponse(Call<ReferralsResponse> call, Response<ReferralsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    ReferralsResponse.ReferralsData data = response.body().getData();
                    if (data != null) {
                        referralCode = data.getReferral_code() != null ? data.getReferral_code() : "";
                        referralFriends = data.getReferral_count();
                        totalReferred = data.getReferral_count();
                        pointsPerReferral = data.getPoints_per_referral();

                        tvReferralCode.setText(referralCode);
                        tvReferralFriends.setText(referralFriends + " friends");

                        // Update empty state and populate table
                        updateEmptyState();

                        // Populate referred friends table
                        if (data.getReferred_friends() != null) {
                            populateReferredFriends(data.getReferred_friends());
                        }
                    }
                } else {
                    Toast.makeText(Referrals.this, "Failed to load referrals", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ReferralsResponse> call, Throwable t) {
                Toast.makeText(Referrals.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateEmptyState() {
        if (referralFriends > 0) {
            layoutEmptyReferrals.setVisibility(View.GONE);
            layoutReferralContent.setVisibility(View.VISIBLE);
        } else {
            layoutEmptyReferrals.setVisibility(View.VISIBLE);
            layoutReferralContent.setVisibility(View.GONE);
        }
    }

    private void populateReferredFriends(List<ReferralsResponse.ReferredFriend> friends) {
        layoutReferralRows.removeAllViews();

        if (friends == null || friends.isEmpty()) return;

        boolean firstRow = true;
        for (ReferralsResponse.ReferredFriend friend : friends) {
            if (!firstRow) {
                View divider = new View(this);
                LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 1);
                divider.setLayoutParams(dividerParams);
                divider.setBackgroundColor(ContextCompat.getColor(this, R.color.divider));
                layoutReferralRows.addView(divider);
            }
            firstRow = false;

            LinearLayout row = new LinearLayout(this);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(rowParams);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(0, 10, 0, 10);
            row.setWeightSum(100); // Using 100 for easier percentage-based weights

            // Date column - 45% (largest space)
            TextView tvDate = new TextView(this);
            LinearLayout.LayoutParams dateParams = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 45f);
            tvDate.setLayoutParams(dateParams);
            String formattedDate = formatDisplayDate(friend.getJoined_date());
            tvDate.setText(formattedDate != null ? formattedDate : "—");
            tvDate.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            tvDate.setTextSize(11);
            row.addView(tvDate);

            // Name column - 35%
            TextView tvName = new TextView(this);
            LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 35f);
            tvName.setLayoutParams(nameParams);
            tvName.setText(friend.getFull_name() != null ? friend.getFull_name() : "—");
            tvName.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            tvName.setTextSize(11);
            row.addView(tvName);

            // Points column - 20%
            TextView tvPoints = new TextView(this);
            LinearLayout.LayoutParams pointsParams = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 20f);
            tvPoints.setLayoutParams(pointsParams);
            tvPoints.setText("+50 pts");
            tvPoints.setTextColor(ContextCompat.getColor(this, R.color.accent_green));
            tvPoints.setTextSize(11);
            tvPoints.setTypeface(null, android.graphics.Typeface.BOLD);
            tvPoints.setGravity(Gravity.END);
            row.addView(tvPoints);

            layoutReferralRows.addView(row);
        }
    }

    // Updated date formatter - handles both "2025-04-25" and "2025-04-25 21:35:55" formats
    private String formatDisplayDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return "—";
        try {
            // Extract just the date part if time is included
            String dateOnly = dateStr.contains(" ") ? dateStr.substring(0, dateStr.indexOf(" ")) : dateStr;
            dateOnly = dateOnly.contains("T") ? dateOnly.substring(0, dateOnly.indexOf("T")) : dateOnly;

            String[] parts = dateOnly.split("-");
            if (parts.length == 3) {
                String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
                int monthIndex = Integer.parseInt(parts[1]) - 1;
                if (monthIndex >= 0 && monthIndex < 12) {
                    return months[monthIndex] + " " + Integer.parseInt(parts[2]) + ", " + parts[0];
                }
            }
        } catch (Exception e) {}
        return dateStr;
    }



    private void shareViaWhatsApp() {
        try {
            String message = "Use my referral code " + referralCode +
                    " to join DentAssist and get a welcome bonus!";
            String url = "https://wa.me/?text=" + android.net.Uri.encode(message);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(android.net.Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "WhatsApp not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void copyReferralCode() {
        if (referralCode.isEmpty()) {
            Toast.makeText(this, "No referral code available", Toast.LENGTH_SHORT).show();
            return;
        }
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Referral Code", referralCode);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Referral code copied!", Toast.LENGTH_SHORT).show();
    }
}