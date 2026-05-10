package com.example.dentassist;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import androidx.appcompat.widget.AppCompatButton;
import java.util.List;

import com.example.dentassist.app.network.ApiService;
import com.example.dentassist.app.network.RetrofitClient;
import com.example.dentassist.models.DashboardResponse;
import com.example.dentassist.utils.SessionManager;
import com.google.android.material.card.MaterialCardView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Dashboard extends BaseDrawerActivity {

    // ==================== HEADER VIEWS ====================
    private TextView tvPatientName;
    private TextView tvMemberSince;
    private TextView tvLastVisit;
    private TextView tvNbrOfVisits;

    // Stat Badges
    private TextView tvTotalVisits;
    private TextView tvPointsEarned;
    private TextView tvReferralsMade;
    private TextView tvSubscriptionStatus;

    // ==================== BUTTONS ====================
    private AppCompatButton btnBookNow;

    // ==================== QUICK ACTION BADGES ====================
    private MaterialCardView badgeBookQueue;
    private MaterialCardView badgeBills;
    private MaterialCardView badgeDentalChart;
    private MaterialCardView badgeProfile;
    private MaterialCardView badgeSubscription;
    private MaterialCardView badgePoints;
    private MaterialCardView badgeReferrals;

    // Next appointment detail views
    private TextView tvNextApptDate;
    private TextView tvNextApptTime;
    private TextView tvNextApptDoctor;
    private TextView tvNextApptTreatment;
    private LinearLayout layoutNoAppointment;
    private LinearLayout layoutApptDetails;
    private AppCompatButton btnReschedule;
    private AppCompatButton btnCancelAppt;

    // Recent appointments views
    private LinearLayout layoutNoRecentAppointments;
    private LinearLayout layoutRecentApptTable;
    private LinearLayout layoutRecentApptRows;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupDrawer(R.layout.dashboard);

        initViews();
        setupClickListeners();
        fetchDashboardData();
    }

    private void initViews() {
        // ==================== HEADER VIEWS ====================
        tvPatientName = findViewById(R.id.tvPatientName);
        tvMemberSince = findViewById(R.id.memberSince);
        tvLastVisit = findViewById(R.id.lastVisit);
        tvNbrOfVisits = findViewById(R.id.nbrOfVisits);

        // Stat Badges
        tvTotalVisits = findViewById(R.id.tvTotalVisits);
        tvPointsEarned = findViewById(R.id.tvPointsEarned);
        tvReferralsMade = findViewById(R.id.tvReferralsMade);
        tvSubscriptionStatus = findViewById(R.id.tvSubscriptionStatus);

        // ==================== BUTTONS ====================
        btnBookNow = findViewById(R.id.btnBookNow);

        // ==================== QUICK ACTION BADGES ====================
        badgeBookQueue = findViewById(R.id.badgeBookQueue);
        badgeBills = findViewById(R.id.badgeBills);
        badgeDentalChart = findViewById(R.id.badgeDentalChart);
        badgeProfile = findViewById(R.id.badgeProfile);
        badgeSubscription = findViewById(R.id.badgeSubscription);
        badgePoints = findViewById(R.id.badgePoints);
        badgeReferrals = findViewById(R.id.badgeReferrals);

        // Next appointment detail views
        tvNextApptDate = findViewById(R.id.tvNextApptDate);
        tvNextApptTime = findViewById(R.id.tvNextApptTime);
        tvNextApptDoctor = findViewById(R.id.tvNextApptDoctor);
        tvNextApptTreatment = findViewById(R.id.tvNextApptTreatment);
        layoutNoAppointment = findViewById(R.id.layoutNoAppointment);
        layoutApptDetails = findViewById(R.id.layoutApptDetails);
        btnReschedule = findViewById(R.id.btnReschedule);
        btnCancelAppt = findViewById(R.id.btnCancelAppt);

        // Recent appointments views
        layoutNoRecentAppointments = findViewById(R.id.layoutNoRecentAppointments);
        layoutRecentApptTable = findViewById(R.id.layoutRecentApptTable);
        layoutRecentApptRows = findViewById(R.id.layoutRecentApptRows);
    }

    private void setupClickListeners() {
        if (btnBookNow != null) {
            btnBookNow.setOnClickListener(v -> navigateTo(BookAppointment.class));
        }
        if (badgeBookQueue != null) {
            badgeBookQueue.setOnClickListener(v -> navigateTo(BookAppointment.class));
        }
        if (badgeBills != null) {
            badgeBills.setOnClickListener(v -> navigateTo(Bills.class));
        }
        if (badgeDentalChart != null) {
            badgeDentalChart.setOnClickListener(v -> navigateTo(MyTeeth.class));
        }
        if (badgeProfile != null) {
            badgeProfile.setOnClickListener(v -> navigateTo(Profile.class));
        }
        if (badgeSubscription != null) {
            badgeSubscription.setOnClickListener(v -> navigateTo(Subscription.class));
        }
        if (badgePoints != null) {
            badgePoints.setOnClickListener(v -> navigateTo(Points.class));
        }
        if (badgeReferrals != null) {
            badgeReferrals.setOnClickListener(v -> navigateTo(Referrals.class));
        }
    }

    private void navigateTo(Class<?> target) {
        startActivity(new Intent(this, target));
    }



    private void populateRecentAppointments(List<DashboardResponse.RecentAppointment> appointments) {
        if (appointments == null || appointments.isEmpty()) {
            layoutNoRecentAppointments.setVisibility(View.VISIBLE);
            layoutRecentApptTable.setVisibility(View.GONE);
            return;
        }

        layoutNoRecentAppointments.setVisibility(View.GONE);
        layoutRecentApptTable.setVisibility(View.VISIBLE);
        layoutRecentApptRows.removeAllViews();

        boolean firstRow = true;
        for (DashboardResponse.RecentAppointment appt : appointments) {
            // Add divider before each row (except first)
            if (!firstRow) {
                View divider = new View(this);
                LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 1);
                dividerParams.setMargins(0, 0, 0, 0);
                divider.setLayoutParams(dividerParams);
                divider.setBackgroundColor(getResources().getColor(R.color.divider));
                layoutRecentApptRows.addView(divider);
            }
            firstRow = false;

            // Create row
            LinearLayout row = new LinearLayout(this);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(rowParams);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(0, 10, 0, 10);

            // Date - 28%
            TextView tvDate = createTableCell(formatDisplayDate(appt.getAppointment_date()), 0.22f);
            row.addView(tvDate);

            // Time - 18%
            TextView tvTime = createTableCell(formatDisplayTime(appt.getAppointment_time()), 0.18f);
            row.addView(tvTime);

            // Treatment - 22%
            TextView tvTreatment = createTableCell(appt.getTreatment_type(), 0.20f);
            row.addView(tvTreatment);

            // Doctor - 20%
            String doctorName = appt.getDoctor_name() != null ? appt.getDoctor_name().replace("Dr. ", "") : "";
            TextView tvDoctor = createTableCell(doctorName, 0.22f);
            row.addView(tvDoctor);

            // Status - 12%
            TextView tvStatus = createTableCell(capitalizeFirst(appt.getStatus()), 0.18f);
            row.addView(tvStatus);

            layoutRecentApptRows.addView(row);
        }
    }

    private TextView createTableCell(String text, float weight) {
        TextView tv = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, weight);
        tv.setLayoutParams(params);
        tv.setText(text != null ? text : "");
        tv.setTextColor(getResources().getColor(R.color.text_primary));
        tv.setTextSize(11);
        tv.setMaxLines(1);
        tv.setEllipsize(android.text.TextUtils.TruncateAt.END);
        return tv;
    }

    private String capitalizeFirst(String text) {
        if (text == null || text.isEmpty()) return "";
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    // ==================== BACKEND DATA LOADING ====================

    private void fetchDashboardData() {
        SessionManager session = new SessionManager(this);

        if (!session.isLoggedIn()) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, Login.class));
            finish();
            return;
        }

        String token = "Bearer " + session.getToken();
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<DashboardResponse> call = apiService.getDashboardData(token);

        call.enqueue(new Callback<DashboardResponse>() {
            @Override
            public void onResponse(Call<DashboardResponse> call, Response<DashboardResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    DashboardResponse.DashboardData data = response.body().getData();

                    if (data != null) {
                        // Patient info
                        if (data.getPatient() != null) {
                            setPatientName(data.getPatient().getFull_name());
                            setMemberSince(data.getPatient().getMember_since());
                            setLastVisit(data.getPatient().getLast_visit());
                        }

                        // Stats
                        if (data.getStats() != null) {
                            setTotalVisits(String.valueOf(data.getStats().getTotal_visits()));
                            setNbrOfVisits(String.valueOf(data.getStats().getTotal_visits()));
                            setPointsEarned(String.valueOf(data.getStats().getPoints()));
                            setReferralsMade(String.valueOf(data.getStats().getReferrals()));
                            setSubscriptionStatus(data.getStats().getSubscription());
                        }


                        // Next appointment
                        if (data.getNext_appointment() != null) {
                            DashboardResponse.NextAppointment appt = data.getNext_appointment();

                            // Show appointment details, hide placeholder
                            layoutNoAppointment.setVisibility(View.GONE);
                            layoutApptDetails.setVisibility(View.VISIBLE);

                            tvNextApptDate.setText(formatDisplayDate(appt.getAppointment_date()));
                            tvNextApptTime.setText(formatDisplayTime(appt.getAppointment_time()));
                            tvNextApptDoctor.setText("Dr. " + appt.getDoctor_name());
                            tvNextApptTreatment.setText(appt.getTreatment_type());
                        } else {
                            // Show placeholder, hide details
                            layoutNoAppointment.setVisibility(View.VISIBLE);
                            layoutApptDetails.setVisibility(View.GONE);
                        }

                        Toast.makeText(Dashboard.this, "Welcome back, " +
                                session.getUsername() + "!", Toast.LENGTH_SHORT).show();
                        // Recent appointments
                        if (data.getRecent_appointments() != null) {
                            populateRecentAppointments(data.getRecent_appointments());
                        }
                    }
                } else {
                    String error = (response.body() != null) ? response.body().getMessage() : "Failed to load";
                    Toast.makeText(Dashboard.this, error, Toast.LENGTH_SHORT).show();
                    loadPlaceholder();
                }
            }

            @Override
            public void onFailure(Call<DashboardResponse> call, Throwable t) {
                Toast.makeText(Dashboard.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                loadPlaceholder();
            }
        });

    }
    private void loadPlaceholder() {
        SessionManager session = new SessionManager(this);
        setPatientName(session.getUsername());
        setMemberSince("Loading...");
        setLastVisit("Loading...");
        setNbrOfVisits("--");
        setTotalVisits("--");
        setPointsEarned("--");
        setReferralsMade("--");
        setSubscriptionStatus("Loading...");
    }

    // ==================== SETTER METHODS ====================

    public void setPatientName(String name) {
        if (tvPatientName != null) tvPatientName.setText(name != null ? name : "");
    }

    public void setMemberSince(String since) {
        if (tvMemberSince != null) tvMemberSince.setText("Member since " + (since != null ? since : ""));
    }

    public void setLastVisit(String visit) {
        if (tvLastVisit != null) tvLastVisit.setText("Last visit: " + (visit != null ? visit : "Never"));
    }

    public void setNbrOfVisits(String visits) {
        if (tvNbrOfVisits != null) tvNbrOfVisits.setText((visits != null ? visits : "0") + " visits");
    }

    public void setTotalVisits(String total) {
        if (tvTotalVisits != null) tvTotalVisits.setText(total != null ? total : "0");
    }

    public void setPointsEarned(String points) {
        if (tvPointsEarned != null) tvPointsEarned.setText(points != null ? points : "0");
    }

    public void setReferralsMade(String referrals) {
        if (tvReferralsMade != null) tvReferralsMade.setText(referrals != null ? referrals : "0");
    }

    public void setSubscriptionStatus(String status) {
        if (tvSubscriptionStatus != null) tvSubscriptionStatus.setText(status != null ? status : "None");
    }

    private String formatDisplayDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return "";
        try {
            String[] parts = dateStr.split("-");
            if (parts.length == 3) {
                int month = Integer.parseInt(parts[1]);
                String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
                return months[month - 1] + " " + Integer.parseInt(parts[2]) + ", " + parts[0];
            }
        } catch (Exception e) {}
        return dateStr;
    }

    private String formatDisplayTime(String timeStr) {
        if (timeStr == null || timeStr.isEmpty()) return "";
        try {
            String[] parts = timeStr.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            String amPm = hour >= 12 ? "PM" : "AM";
            if (hour > 12) hour -= 12;
            if (hour == 0) hour = 12;
            return hour + ":" + String.format("%02d", minute) + " " + amPm;
        } catch (Exception e) {}
        return timeStr;
    }


}