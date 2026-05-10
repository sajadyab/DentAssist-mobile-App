package com.example.dentassist;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.example.dentassist.app.network.ApiService;
import com.example.dentassist.app.network.RetrofitClient;
import com.example.dentassist.models.PointsResponse;
import com.example.dentassist.utils.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Points extends BaseDrawerActivity {

    private TextView tvTotalPoints, tvNextReward;
    private AppCompatButton btnBookAppointment;
    private View btnRedeem1, btnRedeem2, btnRedeem3, btnRedeem4;

    // Dynamic history
    private LinearLayout layoutNoPoints, layoutPointsHistory;

    private int totalPoints = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupDrawer(R.layout.points);

        tvTotalPoints = findViewById(R.id.tvTotalPoints);
        tvNextReward = findViewById(R.id.tvNextReward);
        btnBookAppointment = findViewById(R.id.btnBookAppointment);
        btnRedeem1 = findViewById(R.id.btnRedeem1);
        btnRedeem2 = findViewById(R.id.btnRedeem2);
        btnRedeem3 = findViewById(R.id.btnRedeem3);
        btnRedeem4 = findViewById(R.id.btnRedeem4);

        layoutNoPoints = findViewById(R.id.layoutNoPoints);
        layoutPointsHistory = findViewById(R.id.layoutPointsHistory);

        btnBookAppointment.setOnClickListener(v -> startActivity(new android.content.Intent(this, BookAppointment.class)));

        // Redeem buttons – just a message, as in web
        View.OnClickListener redeemListener = v ->
                Toast.makeText(this, "Contact the clinic to redeem this reward.", Toast.LENGTH_SHORT).show();
        btnRedeem1.setOnClickListener(redeemListener);
        btnRedeem2.setOnClickListener(redeemListener);
        btnRedeem3.setOnClickListener(redeemListener);
        btnRedeem4.setOnClickListener(redeemListener);

        loadPointsData();
    }

    private void loadPointsData() {
        SessionManager session = new SessionManager(this);
        if (!session.isLoggedIn()) { startActivity(new android.content.Intent(this, Login.class)); finish(); return; }

        String token = "Bearer " + session.getToken();
        RetrofitClient.getClient().create(ApiService.class)
                .getPoints(token)
                .enqueue(new Callback<PointsResponse>() {
                    @Override
                    public void onResponse(Call<PointsResponse> call, Response<PointsResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            PointsResponse.PointsData data = response.body().getData();
                            if (data != null) {
                                totalPoints = data.getTotal_points();
                                tvTotalPoints.setText(String.valueOf(totalPoints));
                                tvNextReward.setText(data.getPoints_to_next_reward() + " pts to next reward");
                                populateHistory(data.getHistory());
                                updateRewardsAvailability(totalPoints);
                            }
                        } else {
                            Toast.makeText(Points.this, "Failed to load points", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<PointsResponse> call, Throwable t) {
                        Toast.makeText(Points.this, "Network error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void populateHistory(List<PointsResponse.HistoryItem> history) {
        layoutPointsHistory.removeAllViews();

        if (history == null || history.isEmpty()) {
            layoutNoPoints.setVisibility(View.VISIBLE);
            layoutPointsHistory.setVisibility(View.GONE);
            return;
        }

        layoutNoPoints.setVisibility(View.GONE);
        layoutPointsHistory.setVisibility(View.VISIBLE);

        for (PointsResponse.HistoryItem item : history) {
            // Row container
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(0, 10, 0, 10);
            row.setGravity(Gravity.CENTER_VERTICAL);

            // Side (date/label)
            TextView tvSide = new TextView(this);
            tvSide.setText(item.getSide());
            tvSide.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
            tvSide.setTextSize(11);
            tvSide.setMinWidth(dpToPx(70));
            row.addView(tvSide);

            // Main info
            LinearLayout infoCol = new LinearLayout(this);
            infoCol.setOrientation(LinearLayout.VERTICAL);
            infoCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

            TextView tvTitle = new TextView(this);
            tvTitle.setText(item.getTitle());
            tvTitle.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            tvTitle.setTextSize(13);
            tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
            infoCol.addView(tvTitle);

            TextView tvMuted = new TextView(this);
            tvMuted.setText(item.getMuted());
            tvMuted.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
            tvMuted.setTextSize(11);
            infoCol.addView(tvMuted);

            row.addView(infoCol);

            // Points badge
            TextView tvPoints = new TextView(this);
            tvPoints.setText(item.getPointsLabel());
            tvPoints.setTextColor(Color.WHITE);
            tvPoints.setBackgroundResource(R.drawable.bg_status_badge);
            tvPoints.setPadding(12, 4, 12, 4);
            tvPoints.setTextSize(11);
            tvPoints.setTypeface(null, android.graphics.Typeface.BOLD);
            row.addView(tvPoints);

            layoutPointsHistory.addView(row);

            // Divider
            View divider = new View(this);
            divider.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
            divider.setBackgroundColor(ContextCompat.getColor(this, R.color.divider));
            layoutPointsHistory.addView(divider);
        }
    }

    private void updateRewardsAvailability(int points) {
        btnRedeem1.setEnabled(points >= 500);
        btnRedeem1.setAlpha(points >= 500 ? 1.0f : 0.5f);
        btnRedeem2.setEnabled(points >= 250);
        btnRedeem2.setAlpha(points >= 250 ? 1.0f : 0.5f);
        btnRedeem3.setEnabled(points >= 300);
        btnRedeem3.setAlpha(points >= 300 ? 1.0f : 0.5f);
        btnRedeem4.setEnabled(points >= 150);
        btnRedeem4.setAlpha(points >= 150 ? 1.0f : 0.5f);
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}