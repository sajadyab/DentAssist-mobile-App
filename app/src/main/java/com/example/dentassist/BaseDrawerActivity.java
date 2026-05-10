package com.example.dentassist;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

public abstract class BaseDrawerActivity extends AppCompatActivity {

    protected DrawerLayout drawerLayout;
    protected NavigationView navigationView;
    protected Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // FORCE LIGHT MODE - ignore system dark mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);

        // Force status bar color to grey
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.status_bar_grey));


    }

    protected void setupDrawer(int contentLayoutId) {
        setContentView(R.layout.activity_drawer_base);

        getLayoutInflater().inflate(contentLayoutId,
                findViewById(R.id.content_frame), true);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Disable the default navigation icon (we're using custom)
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Set up custom menu icon click
        ImageView ivMenuIcon = findViewById(R.id.ivMenuIcon);
        ivMenuIcon.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END);
            } else {
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });

        setupCustomToolbar();
        setupNavigationMenu();
    }

    private void setupNavigationMenu() {
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                startActivity(new Intent(this, Dashboard.class));
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, Profile.class));
            } else if (id == R.id.nav_appointment) {
                startActivity(new Intent(this, BookAppointment.class));
            } else if (id == R.id.nav_bills) {
                startActivity(new Intent(this, Bills.class));
            } else if (id == R.id.nav_teeth) {
                startActivity(new Intent(this, MyTeeth.class));
            } else if (id == R.id.nav_points) {
                startActivity(new Intent(this, Points.class));
            } else if (id == R.id.nav_subscription) {
                startActivity(new Intent(this, Subscription.class));
            } else if (id == R.id.nav_referrals) {
                startActivity(new Intent(this, Referrals.class));
            } else if (id == R.id.nav_logout) {
                performLogout();
            }

            drawerLayout.closeDrawer(GravityCompat.END);
            return true;
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    private void setupCustomToolbar() {
        ImageView ivLogo = toolbar.findViewById(R.id.ivToolbarLogo);
        TextView tvTitle = toolbar.findViewById(R.id.tvToolbarTitle);

        if (ivLogo != null) {
            ivLogo.setImageResource(R.drawable.logo);
            // Make it circular if needed
            ivLogo.setClipToOutline(true);
        }
        if (tvTitle != null) {
            tvTitle.setText("DentAssist");
        }
    }

    private void performLogout() {
        getSharedPreferences("DentAssistPrefs", MODE_PRIVATE)
                .edit()
                .clear()
                .apply();

        Intent intent = new Intent(this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


}