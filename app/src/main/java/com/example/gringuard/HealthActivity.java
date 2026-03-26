package com.example.gringuard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class HealthActivity extends AppCompatActivity {

    Button btnTips, btnFollowPlan, btnGoals, btnSeverity;
    SharedPreferences preferences;

    private static final String PREF_NAME = "DentalData";
    private static final String SEV_PREF_NAME = "SeverityPrefs";
    private static final String KEY_START_TIME = "startTime";
    private static final String KEY_LAST_CHECK_DAY = "lastCheckDay";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health);

        btnTips = findViewById(R.id.btnTips);
        btnFollowPlan = findViewById(R.id.btnFollowPlan);
        btnGoals = findViewById(R.id.btnGoals);
        btnSeverity = findViewById(R.id.btnSeverity);

        preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        updateSeverityButtonStatus();

        // 1. Logic for Tips
        btnTips.setOnClickListener(v -> {
            String disease = preferences.getString("detectedDisease", "");
            String severity = preferences.getString("severity", "");

            if (disease.isEmpty() || severity.isEmpty()) {
                Toast.makeText(this, "Please check severity first!", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = null;
            String d = disease.toLowerCase();

            if (d.contains("gingivitis")) {
                if (severity.equals("low")) intent = new Intent(this, GingitivitisLowActivity.class);
                else if (severity.equals("medium")) intent = new Intent(this, GingivitisActivityMedium.class);
            } else if (d.contains("cavity") || d.contains("caries") || d.contains("decay")) {
                if (severity.equals("low")) intent = new Intent(this, CariesLowActivity.class);
                else if (severity.equals("medium")) intent = new Intent(this, CariesMediumactivity.class);
            } else if (d.contains("fractured")) {
                if (severity.equals("low")) intent = new Intent(this, FracturedLowActivity.class);
                else if (severity.equals("medium")) intent = new Intent(this, FracturedMediumActivity.class);
            } else if (d.contains("calculus")) {
                if (severity.equals("low")) intent = new Intent(this, CalculusActivityLow.class);
                else if (severity.equals("medium")) intent = new Intent(this, CalculusActivityMedium.class);
            }

            if (intent != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, "No specific tips for high severity. Please see a dentist.", Toast.LENGTH_SHORT).show();
            }
        });

        // 2. Logic for 21-Day Follow Plan
        btnFollowPlan.setOnClickListener(v -> {
            String disease = preferences.getString("detectedDisease", "");
            String severity = preferences.getString("severity", "");

            if (disease.isEmpty() || severity.isEmpty()) {
                Toast.makeText(this, "Please check severity first!", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = null;
            String d = disease.toLowerCase();

            if (d.contains("gingivitis")) {
                if (severity.equals("low")) intent = new Intent(this, FollowPlanGingivitisLow.class);
                else if (severity.equals("medium")) intent = new Intent(this, FollowPlanGingivitisMedium.class);
            } else if (d.contains("cavity") || d.contains("caries") || d.contains("decay")) {
                if (severity.equals("low")) intent = new Intent(this, FollowPlanCariesLow.class);
                else if (severity.equals("medium")) intent = new Intent(this, FollowPlanCariesMedium.class);
            } else if (d.contains("fractured")) {
                if (severity.equals("low")) intent = new Intent(this, FollowPlanFracturedLow.class);
                else if (severity.equals("medium")) intent = new Intent(this, FollowPlanFracturedMedium.class);
            } else if (d.contains("calculus")) {
                if (severity.equals("low")) intent = new Intent(this, FollowPlanCalculusLow.class);
                else if (severity.equals("medium")) intent = new Intent(this, FollowPlanCalulusMedium.class);
            }

            if (intent != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, "Follow plan not available for this severity.", Toast.LENGTH_SHORT).show();
            }
        });

        btnGoals.setOnClickListener(v -> {
            startActivity(new Intent(this, TrackGoalsActivity.class));
        });

        // 3. Logic for Weekly Re-Check (Navigates to your NEW files)
        btnSeverity.setOnClickListener(v -> {
            SharedPreferences sevPrefs = getSharedPreferences(SEV_PREF_NAME, MODE_PRIVATE);
            long startTime = sevPrefs.getLong(KEY_START_TIME, 0);
            long currentTime = System.currentTimeMillis();

            int currentDay = (int) ((currentTime - startTime) / (24 * 60 * 60 * 1000)) + 1;
            int lastCheckDay = sevPrefs.getInt(KEY_LAST_CHECK_DAY, 0);

            boolean isCheckDay = (currentDay == 7 || currentDay == 14 || currentDay == 21);

            if (isCheckDay && lastCheckDay != currentDay) {
                String disease = preferences.getString("detectedDisease", "");
                Intent intent;
                String d = disease.toLowerCase();

                // Routing to your NEW file names
                if (d.contains("gingivitis")) {
                    intent = new Intent(this, Gingivitis1_Activity.class);
                } else if (d.contains("fractured") || d.contains("fracture")) {
                    intent = new Intent(this, Fractured_Teeth_Activity1.class);
                } else if (d.contains("calculus")) {
                    intent = new Intent(this, CalculusActivity1.class);
                } else {
                    intent = new Intent(this, Caries_Activity.class);
                }

                intent.putExtra("FROM_HEALTH_TRACKER", true);
                startActivity(intent);
            } else {
                showDisabledPopup(currentDay);
            }
        });
    }

    private void updateSeverityButtonStatus() {
        SharedPreferences sevPrefs = getSharedPreferences(SEV_PREF_NAME, MODE_PRIVATE);
        long startTime = sevPrefs.getLong(KEY_START_TIME, 0);

        if (startTime == 0) {
            disableSeverityButton();
            return;
        }

        long currentTime = System.currentTimeMillis();
        int currentDay = (int) ((currentTime - startTime) / (24 * 60 * 60 * 1000)) + 1;
        int lastCheckDay = sevPrefs.getInt(KEY_LAST_CHECK_DAY, 0);

        if (currentDay > 21) {
            btnSeverity.setText("Program Complete");
            disableSeverityButton();
            return;
        }

        boolean isCheckDay = (currentDay == 7 || currentDay == 14 || currentDay == 21);

        if (isCheckDay && lastCheckDay != currentDay) {
            enableSeverityButton();
        } else {
            disableSeverityButton();
        }
    }

    private void enableSeverityButton() {
        btnSeverity.setBackgroundColor(Color.parseColor("#EC407A"));
        btnSeverity.setText("Check Weekly Severity");
        btnSeverity.setEnabled(true);
    }

    private void disableSeverityButton() {
        btnSeverity.setBackgroundColor(Color.parseColor("#9E9E9E"));
        btnSeverity.setEnabled(true);
    }

    private void showDisabledPopup(int currentDay) {
        String message;
        if (currentDay < 7) {
            message = "First re-check available on Day 7. Current Day: " + currentDay;
        } else if (currentDay > 21) {
            message = "The 21-day tracking period has ended.";
        } else {
            message = "Weekly re-checks are available on Day 7, 14, and 21. Current Day: " + currentDay;
        }

        new AlertDialog.Builder(this)
                .setTitle("Check Locked")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSeverityButtonStatus();
    }
}