package com.example.dentassist;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.dentassist.utils.SessionManager;
import com.example.dentassist.R;

import java.util.LinkedHashMap;
import java.util.Map;

public class MyTeeth extends BaseDrawerActivity {

    // 🔧 Change this to your actual server URL
    private static final String BASE_URL = "http://10.0.2.2:8000/";

    private WebView webToothChart;
    private ProgressBar progressChart;
    private LinearLayout layoutLegend;

    private static final Map<String, Integer> STATUS_COLORS = new LinkedHashMap<String, Integer>() {{
        put("healthy", android.graphics.Color.parseColor("#28a745"));
        put("cavity", android.graphics.Color.parseColor("#fd7e14"));
        put("filled", android.graphics.Color.parseColor("#007bff"));
        put("crown", android.graphics.Color.parseColor("#6f42c1"));
        put("root-canal", android.graphics.Color.parseColor("#ffc107"));
        put("missing", android.graphics.Color.parseColor("#6c757d"));
        put("implant", android.graphics.Color.parseColor("#20c997"));
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupDrawer(R.layout.my_teeth);
        if (getSupportActionBar() != null) getSupportActionBar().setTitle("My Teeth");

        SessionManager session = new SessionManager(this);
        if (!session.isLoggedIn() || session.getToken() == null) {
            startActivity(new Intent(this, Login.class));
            finish();
            return;
        }

        webToothChart = findViewById(R.id.webToothChart);
        progressChart = findViewById(R.id.progressChart);
        layoutLegend = findViewById(R.id.layoutLegend);

        // WebView settings
        webToothChart.getSettings().setJavaScriptEnabled(true);
        webToothChart.getSettings().setDomStorageEnabled(true);
        webToothChart.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webToothChart.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                progressChart.setVisibility(View.GONE);
                webToothChart.setVisibility(View.VISIBLE);
                // Hide website header/sidebar (optional)
                view.evaluateJavascript(
                        "javascript:(function() { " +
                                "  var el = document.querySelector('.bills-queue-header'); if(el) el.style.display='none'; " +
                                "  el = document.querySelector('.col-lg-3'); if(el) el.style.display='none'; " +
                                "})()", null);
            }
        });

        buildLegend();

        // Get token from SessionManager, strip "Bearer " if present
        String token = session.getToken();
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        loadToothChartFromWeb(token);
    }

    private void loadToothChartFromWeb(String token) {
        String url = BASE_URL + "patient/teeth.php?api_token=" + token;
        webToothChart.loadUrl(url);
    }

    private void buildLegend() {
        layoutLegend.removeAllViews();
        String[] labels = {"Healthy", "Cavity", "Filled", "Crown", "Root C.", "Missing", "Implant"};
        String[] keys   = STATUS_COLORS.keySet().toArray(new String[0]);

        for (int i = 0; i < keys.length; i++) {
            LinearLayout item = new LinearLayout(this);
            item.setOrientation(LinearLayout.HORIZONTAL);
            item.setGravity(Gravity.CENTER_VERTICAL);
            item.setPadding(6, 4, 12, 4);

            View colorView = new View(this);
            colorView.setLayoutParams(new LinearLayout.LayoutParams(20, 20));
            colorView.setBackgroundColor(STATUS_COLORS.get(keys[i]));
            item.addView(colorView);

            TextView label = new TextView(this);
            label.setText(labels[i]);
            label.setTextSize(10);
            label.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            label.setPadding(6, 0, 0, 0);
            item.addView(label);

            layoutLegend.addView(item);
        }
    }
}