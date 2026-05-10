package com.example.dentassist;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.example.dentassist.app.network.ApiService;
import com.example.dentassist.app.network.RetrofitClient;
import com.example.dentassist.models.GenericResponse;
import com.example.dentassist.models.OwoInfoRequest;
import com.example.dentassist.models.OwoPaymentInfoResponse;
import com.example.dentassist.models.SubscribeRequest;
import com.example.dentassist.models.SubscriptionResponse;
import com.example.dentassist.utils.SessionManager;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Subscription extends BaseDrawerActivity {

    private TextView tvSubscriptionStatus;
    // Basic plan
    private TextView tvPlanNameBasic, tvPlanPriceMonthlyBasic, tvPlanPriceYearlyBasic, tvPlanFeaturesBasic;
    private AppCompatButton btnChooseBasic;
    private TextView tvPlanHintBasic;
    // Premium
    private TextView tvPlanNamePremium, tvPlanPriceMonthlyPremium, tvPlanPriceYearlyPremium, tvPlanFeaturesPremium;
    private AppCompatButton btnChoosePremium;
    private TextView tvPlanHintPremium;
    // Family
    private TextView tvPlanNameFamily, tvPlanPriceMonthlyFamily, tvPlanPriceYearlyFamily, tvPlanFeaturesFamily;
    private AppCompatButton btnChooseFamily;
    private TextView tvPlanHintFamily;

    // Active subscription card
    private MaterialCardView cardActiveSubscription;
    private TextView tvActivePlanName, tvActiveStatus, tvActiveDates, tvActiveCost, tvActiveFeatures;

    private List<SubscriptionResponse.Plan> plans;
    private String currentPlan = "none";
    private String currentStatus = "none";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setupDrawer(R.layout.subscription);

        tvSubscriptionStatus = findViewById(R.id.tvSubscriptionStatus);

        // Basic
        tvPlanNameBasic = findViewById(R.id.tvPlanNameBasic);
        tvPlanPriceMonthlyBasic = findViewById(R.id.tvPlanPriceMonthlyBasic);
        tvPlanPriceYearlyBasic = findViewById(R.id.tvPlanPriceYearlyBasic);
        tvPlanFeaturesBasic = findViewById(R.id.tvPlanFeaturesBasic);
        btnChooseBasic = findViewById(R.id.btnSelectBasic);
        tvPlanHintBasic = findViewById(R.id.tvPlanHintBasic);

        // Premium
        tvPlanNamePremium = findViewById(R.id.tvPlanNamePremium);
        tvPlanPriceMonthlyPremium = findViewById(R.id.tvPlanPriceMonthlyPremium);
        tvPlanPriceYearlyPremium = findViewById(R.id.tvPlanPriceYearlyPremium);
        tvPlanFeaturesPremium = findViewById(R.id.tvPlanFeaturesPremium);
        btnChoosePremium = findViewById(R.id.btnSelectPremium);
        tvPlanHintPremium = findViewById(R.id.tvPlanHintPremium);

        // Family
        tvPlanNameFamily = findViewById(R.id.tvPlanNameFamily);
        tvPlanPriceMonthlyFamily = findViewById(R.id.tvPlanPriceMonthlyFamily);
        tvPlanPriceYearlyFamily = findViewById(R.id.tvPlanPriceYearlyFamily);
        tvPlanFeaturesFamily = findViewById(R.id.tvPlanFeaturesFamily);
        btnChooseFamily = findViewById(R.id.btnSelectFamily);
        tvPlanHintFamily = findViewById(R.id.tvPlanHintFamily);

        // Active card
        cardActiveSubscription = findViewById(R.id.cardActiveSubscription);
        tvActivePlanName = findViewById(R.id.tvActivePlanName);
        tvActiveStatus = findViewById(R.id.tvActiveStatus);
        tvActiveDates = findViewById(R.id.tvActiveDates);
        tvActiveCost = findViewById(R.id.tvActiveCost);
        tvActiveFeatures = findViewById(R.id.tvActiveFeatures);

        btnChooseBasic.setOnClickListener(v -> selectPlan("basic"));
        btnChoosePremium.setOnClickListener(v -> selectPlan("premium"));
        btnChooseFamily.setOnClickListener(v -> selectPlan("family"));

        loadSubscriptionData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload subscription data every time the screen is shown
        loadSubscriptionData();
    }

    private void loadSubscriptionData() {
        SessionManager session = new SessionManager(this);
        if (!session.isLoggedIn()) { startActivity(new Intent(this, Login.class)); finish(); return; }
        String token = "Bearer " + session.getToken();
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.getSubscription(token).enqueue(new Callback<SubscriptionResponse>() {
            @Override public void onResponse(Call<SubscriptionResponse> call, Response<SubscriptionResponse> r) {
                if (r.isSuccessful() && r.body() != null && r.body().isSuccess()) {
                    SubscriptionResponse.SubscriptionData d = r.body().getData();
                    if (d != null) {
                        currentPlan = d.getCurrent_plan() != null ? d.getCurrent_plan() : "none";
                        currentStatus = d.getSubscription_status() != null ? d.getSubscription_status() : "none";
                        plans = d.getPlans();
                        updateUI(d);
                    }
                }
            }
            @Override public void onFailure(Call<SubscriptionResponse> call, Throwable t) {}
        });
    }

    private void updateUI(SubscriptionResponse.SubscriptionData data) {
        String statusText = "No plan yet";
        if (!currentPlan.equals("none")) {
            if (currentStatus.equals("active")) statusText = "Active - " + capitalize(currentPlan) + " Plan";
            else if (currentStatus.equals("pending")) statusText = "Pending - " + capitalize(currentPlan) + " Plan";
        }
        tvSubscriptionStatus.setText(statusText);

        if (!currentPlan.equals("none") && (currentStatus.equals("active") || currentStatus.equals("pending"))) {
            cardActiveSubscription.setVisibility(View.VISIBLE);
            tvActivePlanName.setText(capitalize(currentPlan) + " Plan");
            tvActiveStatus.setText(currentStatus.equals("active") ? "Active" : "Pending Payment");
            if (data.getStart_date() != null && data.getEnd_date() != null)
                tvActiveDates.setText(formatDate(data.getStart_date()) + " – " + formatDate(data.getEnd_date()));
            if (plans != null)
                for (SubscriptionResponse.Plan p : plans)
                    if (p.getPlan_key().equals(currentPlan)) {
                        tvActiveCost.setText(NumberFormat.getCurrencyInstance(Locale.US).format(p.getMonthly_price()) + "/month");
                        tvActiveFeatures.setText(formatFeatures(p.getFeatures()));
                        break;
                    }
        } else cardActiveSubscription.setVisibility(View.GONE);

        if (plans != null)
            for (SubscriptionResponse.Plan p : plans) {
                switch (p.getPlan_key()) {
                    case "basic": fillPlanCard(tvPlanNameBasic, tvPlanPriceMonthlyBasic, tvPlanPriceYearlyBasic,
                            tvPlanFeaturesBasic, btnChooseBasic, tvPlanHintBasic, p); break;
                    case "premium": fillPlanCard(tvPlanNamePremium, tvPlanPriceMonthlyPremium, tvPlanPriceYearlyPremium,
                            tvPlanFeaturesPremium, btnChoosePremium, tvPlanHintPremium, p); break;
                    case "family": fillPlanCard(tvPlanNameFamily, tvPlanPriceMonthlyFamily, tvPlanPriceYearlyFamily,
                            tvPlanFeaturesFamily, btnChooseFamily, tvPlanHintFamily, p); break;
                }
            }
    }

    private void fillPlanCard(TextView name, TextView monthly, TextView yearly, TextView features,
                              AppCompatButton btn, TextView hint, SubscriptionResponse.Plan plan) {
        name.setText(plan.getPlan_name());
        monthly.setText(NumberFormat.getCurrencyInstance(Locale.US).format(plan.getMonthly_price()) + "/month");
        yearly.setText(NumberFormat.getCurrencyInstance(Locale.US).format(plan.getAnnual_price()) + "/year");
        features.setText(formatFeatures(plan.getFeatures()));

        boolean isCurrent = plan.getPlan_key().equals(currentPlan) &&
                (currentStatus.equals("active") || currentStatus.equals("pending"));
        boolean activeAnother = !currentPlan.equals("none") &&
                (currentStatus.equals("active") || currentStatus.equals("pending"));

        if (isCurrent) {
            btn.setText("Current Plan"); btn.setEnabled(false);
            btn.setTextColor(ContextCompat.getColor(this, R.color.accent_green));
            btn.setBackgroundColor(Color.TRANSPARENT); btn.setElevation(0);
            hint.setVisibility(View.GONE);
        } else if (activeAnother) {
            btn.setVisibility(View.GONE); hint.setVisibility(View.VISIBLE);
            hint.setText("You'll be able to activate a different plan once your current subscription ends.");
        } else {
            btn.setVisibility(View.VISIBLE); hint.setVisibility(View.GONE);
            btn.setText("Choose plan"); btn.setEnabled(true);
            btn.setTextColor(Color.WHITE); btn.setBackgroundResource(R.drawable.button_green_gradient);
        }
    }

    private void selectPlan(String planKey) {
        if (currentStatus.equals("active") || currentStatus.equals("pending")) {
            Toast.makeText(this, "Already have an active/pending subscription", Toast.LENGTH_SHORT).show();
            return;
        }
        SubscriptionResponse.Plan selected = null;
        if (plans != null) for (SubscriptionResponse.Plan p : plans) if (p.getPlan_key().equals(planKey)) { selected = p; break; }
        if (selected == null) return;
        showBillingCycleDialog(planKey);
    }

    private void showBillingCycleDialog(String planKey) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Choose Billing Cycle")
                .setSingleChoiceItems(new String[]{"Monthly", "Yearly"}, 0, null)
                .setPositiveButton("Next", (dialog, which) -> {
                    int pos = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                    String cycle = pos == 1 ? "yearly" : "monthly";
                    dialog.dismiss();
                    showPaymentMethodDialog(planKey, cycle);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showPaymentMethodDialog(String plan, String billingCycle) {
        View layout = getLayoutInflater().inflate(R.layout.dialog_payment_choice, null);
        MaterialCardView clinicCard = layout.findViewById(R.id.cardClinic);
        MaterialCardView onlineCard = layout.findViewById(R.id.cardOnline);
        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(layout)
                .create();
        dialog.show();

        clinicCard.setOnClickListener(v -> {
            dialog.dismiss();
            submitSubscription(plan, billingCycle, "clinic_payment");   // send request → pending
        });
        onlineCard.setOnClickListener(v -> {
            dialog.dismiss();
            startOwoPayment(plan, billingCycle);   // no request, just open payment screen
        });
    }

    private void startOwoPayment(String plan, String billingCycle) {
        SessionManager session = new SessionManager(this);
        String token = "Bearer " + session.getToken();
        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        api.getOwoPaymentInfo(token, new OwoInfoRequest(plan, billingCycle))
                .enqueue(new Callback<OwoPaymentInfoResponse>() {
                    @Override public void onResponse(Call<OwoPaymentInfoResponse> c, Response<OwoPaymentInfoResponse> r) {
                        if (r.isSuccessful() && r.body() != null && r.body().isSuccess()) {
                            OwoPaymentInfoResponse.Data d = r.body().getData();
                            Intent intent = new Intent(Subscription.this, OwoPaymentActivity.class);
                            intent.putExtra("clinic_owo_number", d.getClinic_owo_number());
                            intent.putExtra("amount", d.getAmount());
                            intent.putExtra("plan", d.getPlan());
                            intent.putExtra("billing_cycle", d.getBilling_cycle());
                            intent.putExtra("reference", d.getReference());
                            intent.putExtra("patient_id", d.getPatient_id());
                            intent.putExtra("patient_name", d.getPatient_name());
                            startActivity(intent);
                        } else {
                            Toast.makeText(Subscription.this, "Failed to load payment info", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override public void onFailure(Call<OwoPaymentInfoResponse> c, Throwable t) {
                        Toast.makeText(Subscription.this, "Network error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void submitSubscription(String plan, String billingCycle, String action) {
        SessionManager session = new SessionManager(this);
        String token = "Bearer " + session.getToken();
        RetrofitClient.getClient().create(ApiService.class)
                .subscribe(token, new SubscribeRequest(plan, action, billingCycle))
                .enqueue(new Callback<GenericResponse>() {
                    @Override public void onResponse(Call<GenericResponse> c, Response<GenericResponse> r) {
                        if (r.isSuccessful() && r.body() != null && r.body().isSuccess()) {
                            if (action.equals("clinic_payment"))
                                Toast.makeText(Subscription.this, "Visit clinic to complete payment", Toast.LENGTH_LONG).show();
                            loadSubscriptionData();
                        } else
                            Toast.makeText(Subscription.this, r.body() != null ? r.body().getMessage() : "Failed", Toast.LENGTH_LONG).show();
                    }
                    @Override public void onFailure(Call<GenericResponse> c, Throwable t) {
                        Toast.makeText(Subscription.this, "Network error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // --- helpers ---
    private String formatFeatures(String features) {
        if (features == null) return "";
        String[] lines = features.split("[\\r\\n,]+");
        StringBuilder sb = new StringBuilder();
        for (String l : lines) { String t = l.trim(); if (!t.isEmpty()) sb.append("✓ ").append(t).append("\n"); }
        return sb.toString().trim();
    }

    private String formatDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return "";
        try {
            String[] p = dateStr.split("-");
            String[] m = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
            return m[Integer.parseInt(p[1])-1] + " " + Integer.parseInt(p[2]) + ", " + p[0];
        } catch (Exception e) { return dateStr; }
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return "";
        return s.substring(0,1).toUpperCase() + s.substring(1);
    }
}