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
import com.google.firebase.auth.FirebaseAuth;

public class HealthActivity extends AppCompatActivity {

    Button btnTips, btnFollowPlan, btnGoals, btnSeverity;
    SharedPreferences preferences;
    private String uid;

    private static final String PREF_PREFIX = "DentalData_";
    private static final String SEV_PREF_PREFIX = "SeverityPrefs_";
    private static final String KEY_START_TIME = "startTime";
    private static final String KEY_LAST_CHECK_DAY = "lastCheckDay";
    private static final String KEY_COMPLETION_SHOWN = "completionShown";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
            return;
        }
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        btnTips = findViewById(R.id.btnTips);
        btnFollowPlan = findViewById(R.id.btnFollowPlan);
        btnGoals = findViewById(R.id.btnGoals);
        btnSeverity = findViewById(R.id.btnSeverity);

        preferences = getSharedPreferences(PREF_PREFIX + uid, MODE_PRIVATE);

        updateSeverityButtonStatus();
        checkAndShowCompletionPopup();

        // 1. Logic for Tips
        btnTips.setOnClickListener(v -> {
            String disease = preferences.getString("detectedDisease", "");
            String severity = preferences.getString("severity", "");

            if (disease.isEmpty() || severity.isEmpty()) {
                Toast.makeText(this, "Please check severity first!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (severity.equalsIgnoreCase("healthy")) {
                Toast.makeText(this, "You do not require tips for healthy teeth.", Toast.LENGTH_LONG).show();
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
            SharedPreferences sevPrefs = getSharedPreferences(SEV_PREF_PREFIX + uid, MODE_PRIVATE);
            long startTime = sevPrefs.getLong(KEY_START_TIME, 0);
            if (startTime != 0) {
                long currentTime = System.currentTimeMillis();
                int currentDay = (int) ((currentTime - startTime) / (24 * 60 * 60 * 1000)) + 1;
                if (currentDay >= 22) {
                    showCelebrationPopup(sevPrefs);
                    return;
                }
            }

            String disease = preferences.getString("detectedDisease", "");
            String severity = preferences.getString("severity", "");

            if (disease.isEmpty() || severity.isEmpty()) {
                Toast.makeText(this, "Please check severity first!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (severity.equalsIgnoreCase("healthy")) {
                Toast.makeText(this, "You do not require a follow plan for healthy teeth.", Toast.LENGTH_LONG).show();
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

        // 3. Logic for Weekly Re-Check
        btnSeverity.setOnClickListener(v -> {
            SharedPreferences sevPrefs = getSharedPreferences(SEV_PREF_PREFIX + uid, MODE_PRIVATE);
            long startTime = sevPrefs.getLong(KEY_START_TIME, 0);
            long currentTime = System.currentTimeMillis();

            int currentDay = (int) ((currentTime - startTime) / (24 * 60 * 60 * 1000)) + 1;
            int lastCheckDay = sevPrefs.getInt(KEY_LAST_CHECK_DAY, 0);

            boolean isCheckDay = (currentDay == 7 || currentDay == 14 || currentDay == 21);

            if (isCheckDay && lastCheckDay != currentDay) {
                String disease = preferences.getString("detectedDisease", "");
                Intent intent;
                String d = disease.toLowerCase();

                if (d.contains("gingivitis")) {
                    intent = new Intent(this, Gingivitis1_Activity.class);
                } else if (d.contains("fractured") || d.contains("fracture")) {
                    intent = new Intent(this, Fractured_Teeth_Activity1.class);
                } else if (d.contains("calculus")) {
                    intent = new Intent(this, CalculusActivity1.class);
                } else {
                    intent = new Intent(this, CariesActivity1.class);
                }

                intent.putExtra("FROM_HEALTH_TRACKER", true);
                startActivity(intent);
            } else {
                showDisabledPopup(currentDay);
            }
        });
    }

    private void checkAndShowCompletionPopup() {
        SharedPreferences sevPrefs = getSharedPreferences(SEV_PREF_PREFIX + uid, MODE_PRIVATE);
        long startTime = sevPrefs.getLong(KEY_START_TIME, 0);
        if (startTime == 0) return;

        long currentTime = System.currentTimeMillis();
        int currentDay = (int) ((currentTime - startTime) / (24 * 60 * 60 * 1000)) + 1;
        boolean alreadyShown = sevPrefs.getBoolean(KEY_COMPLETION_SHOWN, false);

        if (currentDay >= 22 && !alreadyShown) {
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
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        Button btnFinish = dialogView.findViewById(R.id.btnFinishCelebration);
        btnFinish.setOnClickListener(v -> {
            sevPrefs.edit().putBoolean(KEY_COMPLETION_SHOWN, true).apply();
            dialog.dismiss();
            Toast.makeText(this, "Your 21 days plan is over", Toast.LENGTH_LONG).show();
        });

        dialog.show();
    }

    private void updateSeverityButtonStatus() {
        SharedPreferences sevPrefs = getSharedPreferences(SEV_PREF_PREFIX + uid, MODE_PRIVATE);
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
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            preferences = getSharedPreferences(PREF_PREFIX + uid, MODE_PRIVATE);
        }
        updateSeverityButtonStatus();
        checkAndShowCompletionPopup();
    }
}
