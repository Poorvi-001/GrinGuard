package com.example.gringuard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class HealthActivity extends AppCompatActivity {

    Button btnTips, btnFollowPlan, btnGoals, btnSeverity;
    SharedPreferences preferences;

    private static final String PREF_NAME = "DentalData";
    private static final String SEV_PREF_NAME = "SeverityPrefs";
    private static final String KEY_LAST_CLICK = "lastClickTime";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health);

        btnTips = findViewById(R.id.btnTips);
        btnFollowPlan = findViewById(R.id.btnFollowPlan);
        btnGoals = findViewById(R.id.btnGoals);
        btnSeverity = findViewById(R.id.btnSeverity);

        preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        checkSeverityAvailability();

        btnTips.setOnClickListener(v -> {
            String disease = preferences.getString("detectedDisease", "");
            String severity = preferences.getString("severity", "");

            if (disease.isEmpty() || severity.isEmpty()) {
                Toast.makeText(this, "Please check severity first!", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = null;

            if (disease.equalsIgnoreCase("Gingivitis")) {
                if (severity.equals("low")) intent = new Intent(this, GingitivitisLowActivity.class);
                else if (severity.equals("medium")) intent = new Intent(this, GingivitisActivityMedium.class);
            } else if (disease.equalsIgnoreCase("Cavity") || disease.equalsIgnoreCase("Caries")) {
                if (severity.equals("low")) intent = new Intent(this, CavityLowActivity.class);
                else if (severity.equals("medium")) intent = new Intent(this, CavityMediumactivity.class);
            } else if (disease.equalsIgnoreCase("Fractured")) {
                if (severity.equals("low")) intent = new Intent(this, FracturedLowActivity.class);
                else if (severity.equals("medium")) intent = new Intent(this, FracturedMediumActivity.class);
            }

            if (intent != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, "No specific tips for high severity. Please see a dentist.", Toast.LENGTH_SHORT).show();
            }
        });

        btnFollowPlan.setOnClickListener(v -> {
            String disease = preferences.getString("detectedDisease", "");
            String severity = preferences.getString("severity", "");

            if (disease.isEmpty() || severity.isEmpty()) {
                Toast.makeText(this, "Please check severity first!", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = null;

            if (disease.equalsIgnoreCase("Gingivitis")) {
                if (severity.equals("low")) intent = new Intent(this, FollowPlanGingivitisLow.class);
            } else if (disease.equalsIgnoreCase("Cavity") || disease.equalsIgnoreCase("Caries")) {
                if (severity.equals("low")) intent = new Intent(this, FollowPlanCavityLow.class);
                else if (severity.equals("medium")) intent = new Intent(this, FollowPlanCavityMedium.class);
            } else if (disease.equalsIgnoreCase("Fractured")) {
                if (severity.equals("low")) intent = new Intent(this, FollowPlanFracturedLow.class);
                else if (severity.equals("medium")) intent = new Intent(this, FollowPlanFracturedMedium.class);
            }

            if (intent != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, "Follow plan not available for this severity.", Toast.LENGTH_SHORT).show();
            }
        });

        btnGoals.setOnClickListener(v ->
                Toast.makeText(this, "Opening Track Goals", Toast.LENGTH_SHORT).show());

        btnSeverity.setOnClickListener(v -> {
            String disease = preferences.getString("detectedDisease", "");
            Intent intent;
            if (disease.equalsIgnoreCase("Gingivitis")) {
                intent = new Intent(this, Gingivitis_Activity.class);
            } else if (disease.equalsIgnoreCase("Fractured")) {
                intent = new Intent(this, Fractured_Teeth_Activity.class);
            } else {
                intent = new Intent(this, Cavity_Activity.class);
            }
            startActivity(intent);
        });
    }

    private void checkSeverityAvailability() {
        SharedPreferences sevPrefs = getSharedPreferences(SEV_PREF_NAME, MODE_PRIVATE);
        long lastClickTime = sevPrefs.getLong(KEY_LAST_CLICK, 0);

        if (lastClickTime == 0) {
            btnSeverity.setEnabled(true);
            return;
        }

        long currentTime = System.currentTimeMillis();
        long oneWeekMillis = 7L * 24 * 60 * 60 * 1000;
        long nextAllowedTime = lastClickTime + oneWeekMillis;

        if (currentTime >= nextAllowedTime) {
            btnSeverity.setEnabled(true);
        } else {
            btnSeverity.setEnabled(true); // Enabled for testing
        }
    }
}
