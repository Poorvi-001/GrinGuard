package com.example.gringuard;

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

        btnTips.setOnClickListener(v ->
                Toast.makeText(this, "Opening Personalised Tips", Toast.LENGTH_SHORT).show());

        btnFollowPlan.setOnClickListener(v ->
                Toast.makeText(this, "Opening Follow Plan", Toast.LENGTH_SHORT).show());

        btnGoals.setOnClickListener(v ->
                Toast.makeText(this, "Opening Track Goals", Toast.LENGTH_SHORT).show());

        btnSeverity.setOnClickListener(v -> {
            long currentTime = System.currentTimeMillis();

            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong(KEY_LAST_CLICK, currentTime);
            editor.apply();

            Toast.makeText(this, "Severity Check Started", Toast.LENGTH_SHORT).show();

            btnSeverity.setEnabled(false);
        });
    }

    private void checkSeverityAvailability() {

        long lastClickTime = preferences.getLong(KEY_LAST_CLICK, 0);

        if (lastClickTime == 0) {
            // First time user → allow click
            btnSeverity.setEnabled(true);
            return;
        }

        long currentTime = System.currentTimeMillis();

        long oneWeekMillis = 7L * 24 * 60 * 60 * 1000;

        long nextAllowedTime = lastClickTime + oneWeekMillis;

        // Enable ONLY if today is same day (within 24 hours window)
        if (currentTime >= nextAllowedTime &&
                currentTime < nextAllowedTime + (24L * 60 * 60 * 1000)) {

            btnSeverity.setEnabled(true);

        } else {
            btnSeverity.setEnabled(false);
        }
    }
}