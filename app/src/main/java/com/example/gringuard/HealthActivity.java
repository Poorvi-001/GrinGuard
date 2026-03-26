package com.example.gringuard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
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
    private static final String KEY_COMPLETION_SHOWN = "completionShown";

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
        checkAndShowCompletionPopup();

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

        btnSeverity.setOnClickListener(v -> {
            SharedPreferences sevPrefs = getSharedPreferences(SEV_PREF_NAME, MODE_PRIVATE);
            long startTime = sevPrefs.getLong(KEY_START_TIME, 0);
            long currentTime = System.currentTimeMillis();

            if (startTime == 0) {
                startTime = currentTime;
                sevPrefs.edit().putLong(KEY_START_TIME, startTime).apply();
            }

            int currentDay = (int) ((currentTime - startTime) / (24 * 60 * 60 * 1000)) + 1;
            int lastCheckDay = sevPrefs.getInt(KEY_LAST_CHECK_DAY, 0);
            
            // Enabled only on day 7, 14, and 21
            boolean isCheckDay = (currentDay == 7 || currentDay == 14 || currentDay == 21);

            if (isCheckDay && lastCheckDay != currentDay) {
                sevPrefs.edit().putInt(KEY_LAST_CHECK_DAY, currentDay).apply();

                String disease = preferences.getString("detectedDisease", "");
                Intent intent;
                String d = disease.toLowerCase();
                if (d.contains("gingivitis")) {
                    intent = new Intent(this, Gingivitis_Activity.class);
                } else if (d.contains("fractured")) {
                    intent = new Intent(this, Fractured_Teeth_Activity.class);
                } else if (d.contains("calculus")) {
                    intent = new Intent(this, CalculusActivity.class);
                } else {
                    intent = new Intent(this, Caries_Activity.class);
                }
                
                // Add flag to indicate this is a re-check from health tracker
                intent.putExtra("FROM_HEALTH_TRACKER", true);
                startActivity(intent);
            } else {
                showDisabledPopup();
            }
        });
    }

    private void checkAndShowCompletionPopup() {
        SharedPreferences sevPrefs = getSharedPreferences(SEV_PREF_NAME, MODE_PRIVATE);
        long startTime = sevPrefs.getLong(KEY_START_TIME, 0);
        if (startTime == 0) return;

        long currentTime = System.currentTimeMillis();
        int currentDay = (int) ((currentTime - startTime) / (24 * 60 * 60 * 1000)) + 1;
        boolean alreadyShown = sevPrefs.getBoolean(KEY_COMPLETION_SHOWN, false);

        if (currentDay >= 21 && !alreadyShown) {
            showCelebrationPopup(sevPrefs);
        }
    }

    private void showCelebrationPopup(SharedPreferences sevPrefs) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_completion, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Button btnFinish = dialogView.findViewById(R.id.btnFinishCelebration);
        btnFinish.setOnClickListener(v -> {
            sevPrefs.edit().putBoolean(KEY_COMPLETION_SHOWN, true).apply();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void updateSeverityButtonStatus() {
        SharedPreferences sevPrefs = getSharedPreferences(SEV_PREF_NAME, MODE_PRIVATE);
        long startTime = sevPrefs.getLong(KEY_START_TIME, 0);
        
        if (startTime == 0) {
            // If tracking hasn't started, disable it until day 7
            disableSeverityButton();
            return;
        }

        long currentTime = System.currentTimeMillis();
        int currentDay = (int) ((currentTime - startTime) / (24 * 60 * 60 * 1000)) + 1;
        int lastCheckDay = sevPrefs.getInt(KEY_LAST_CHECK_DAY, 0);

        if (currentDay > 21) {
            disableSeverityButton();
            return;
        }

        // Enabled only on day 7, 14, and 21
        boolean isCheckDay = (currentDay == 7 || currentDay == 14 || currentDay == 21);
        
        if (isCheckDay && lastCheckDay != currentDay) {
            enableSeverityButton();
        } else {
            disableSeverityButton();
        }
    }

    private void enableSeverityButton() {
        btnSeverity.setBackgroundColor(Color.parseColor("#EC407A"));
        btnSeverity.setEnabled(true);
    }

    private void disableSeverityButton() {
        btnSeverity.setBackgroundColor(Color.parseColor("#9E9E9E"));
        btnSeverity.setEnabled(true); // Enabled for click to show popup explanation
    }

    private void showDisabledPopup() {
        new AlertDialog.Builder(this)
                .setTitle("Check Disabled")
                .setMessage("Enabled at every 7th day (Day 7, 14, 21)")
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSeverityButtonStatus();
        checkAndShowCompletionPopup();
    }
}
