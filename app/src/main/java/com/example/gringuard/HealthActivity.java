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

    private static final String PREF_NAME = "SeverityPrefs";
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
            Intent intent = new Intent(HealthActivity.this, GingitivitisLowActivity.class);
            startActivity(intent);
        });

        btnFollowPlan.setOnClickListener(v ->
                Toast.makeText(this, "Opening Follow Plan", Toast.LENGTH_SHORT).show());

        btnGoals.setOnClickListener(v ->
                Toast.makeText(this, "Opening Track Goals", Toast.LENGTH_SHORT).show());

        btnSeverity.setOnClickListener(v -> {
            // Updated to open Gingivitis_Activity
            Intent intent = new Intent(HealthActivity.this, Gingivitis_Activity.class);
            startActivity(intent);

            // Keep the severity check timing logic if needed
            long currentTime = System.currentTimeMillis();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong(KEY_LAST_CLICK, currentTime);
            editor.apply();
        });
    }

    private void checkSeverityAvailability() {
        long lastClickTime = preferences.getLong(KEY_LAST_CLICK, 0);

        if (lastClickTime == 0) {
            btnSeverity.setEnabled(true);
            return;
        }

        long currentTime = System.currentTimeMillis();
        long oneWeekMillis = 7L * 24 * 60 * 60 * 1000;
        long nextAllowedTime = lastClickTime + oneWeekMillis;

        if (currentTime >= nextAllowedTime &&
                currentTime < nextAllowedTime + (24L * 60 * 60 * 1000)) {
            btnSeverity.setEnabled(true);
        } else {
            // Keep the button enabled for now so user can test the transition
            btnSeverity.setEnabled(true); 
        }
    }
}
