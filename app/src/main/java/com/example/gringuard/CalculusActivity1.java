package com.example.gringuard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class CalculusActivity1 extends AppCompatActivity {

    private boolean fromHealthTracker = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calculus1);

        fromHealthTracker = getIntent().getBooleanExtra("FROM_HEALTH_TRACKER", false);

        RadioGroup rgVisual = findViewById(R.id.rgVisual);
        RadioGroup rgColor = findViewById(R.id.rgColor);
        RadioGroup rgSensitivity = findViewById(R.id.rgSensitivity);
        RadioGroup rgSwelling = findViewById(R.id.rgSwelling);
        RadioGroup rgCoverage = findViewById(R.id.rgCoverage);
        RadioGroup rgBreath = findViewById(R.id.rgBreath);

        Button btnCalculate = findViewById(R.id.btnCalculate);

        btnCalculate.setOnClickListener(v -> {
            if (rgVisual.getCheckedRadioButtonId() == -1 || rgColor.getCheckedRadioButtonId() == -1 ||
                    rgSensitivity.getCheckedRadioButtonId() == -1 || rgSwelling.getCheckedRadioButtonId() == -1 ||
                    rgCoverage.getCheckedRadioButtonId() == -1 || rgBreath.getCheckedRadioButtonId() == -1) {
                Toast.makeText(this, "Please answer all 6 questions", Toast.LENGTH_SHORT).show();
                return;
            }

            int vScore = getScore(rgVisual);
            int cScore = getScore(rgColor);
            int sScore = getScore(rgSensitivity);
            int swScore = getScore(rgSwelling);
            int coScore = getScore(rgCoverage);
            int bScore = getScore(rgBreath);

            int maxSeverity = Math.max(vScore, Math.max(cScore, Math.max(sScore,
                    Math.max(swScore, Math.max(coScore, bScore)))));

            int mediumCount = 0;
            int healthyCount = 0;
            int[] scores = {vScore, cScore, sScore, swScore, coScore, bScore};
            for (int s : scores) {
                if (s == 2) mediumCount++;
                if (s == 0) healthyCount++;
            }

            String resultText;
            String severity;

            // --- FIXED LOGIC ---
            if (healthyCount == 6) {
                resultText = "EXCELLENT: No Calculus Detected\nYour teeth are free of visible tartar. Maintain this with regular brushing and flossing!";
                severity = "healthy";
            } else if (maxSeverity == 3 || (maxSeverity == 2 && mediumCount >= 3)) {
                resultText = "HIGH SEVERITY: Severe Calculus\nExtensive buildup and gum distress. Requires professional scaling and root planing.";
                severity = "high";
            } else if (maxSeverity == 2) {
                resultText = "MEDIUM SEVERITY: Moderate Calculus\nSignificant buildup present. Professional cleaning is necessary.";
                severity = "medium";
            } else {
                resultText = "LOW SEVERITY: Mild Calculus\nEarly stage buildup. A professional cleaning is recommended to remove existing tartar.";
                severity = "low";
            }

            // Save basic dental data
            SharedPreferences prefs = getSharedPreferences("DentalData", MODE_PRIVATE);
            prefs.edit().putString("detectedDisease", "Calculus").putString("severity", severity).apply();

            // Handle 21-day tracker logic
            SharedPreferences sevPrefs = getSharedPreferences("SeverityPrefs", MODE_PRIVATE);
            long startTime = sevPrefs.getLong("startTime", 0);
            long currentTime = System.currentTimeMillis();
            if (startTime == 0) {
                startTime = currentTime;
                sevPrefs.edit().putLong("startTime", startTime).apply();
            }
            int currentDay = (int) ((currentTime - startTime) / (24 * 60 * 60 * 1000)) + 1;
            sevPrefs.edit().putInt("lastCheckDay", currentDay).apply();

            // Save to history for the Graph (Very important for TrackGoalsActivity)
            SharedPreferences historyPrefs = getSharedPreferences("SeverityHistory", MODE_PRIVATE);
            historyPrefs.edit().putString(String.valueOf(currentDay), severity).apply();

            showSeverityPopup(resultText, severity);
        });
    }

    private void showSeverityPopup(String resultMessage, String severity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.severity_popup, null);
        builder.setView(dialogView);

        AlertDialog severityDialog = builder.create();
        severityDialog.setCancelable(false);

        TextView tvSeverityValue = dialogView.findViewById(R.id.tvSeverityValue);
        Button btnOkResult = dialogView.findViewById(R.id.btnOkResult);

        tvSeverityValue.setText(resultMessage);

        // UI Color Coding
        if (severity.equals("healthy")) {
            tvSeverityValue.setTextColor(Color.parseColor("#2E7D32")); // Dark Green
        } else if (severity.equals("low")) {
            tvSeverityValue.setTextColor(Color.parseColor("#4CAF50")); // Green
        } else if (severity.equals("medium")) {
            tvSeverityValue.setTextColor(Color.parseColor("#FBC02D")); // Yellow
        } else if (severity.equals("high")) {
            tvSeverityValue.setTextColor(Color.parseColor("#F44336")); // Red
        }

        btnOkResult.setOnClickListener(v -> {
            severityDialog.dismiss();
            if (severity.equals("high")) {
                showHighSeverityPopup();
            } else {
                if (fromHealthTracker) {
                    Intent intent = new Intent(CalculusActivity1.this, HealthActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(CalculusActivity1.this, plan_fo_21_days.class);
                    intent.putExtra("DISEASE_KEY", "Calculus");
                    intent.putExtra("SEVERITY_KEY", severity);
                    startActivity(intent);
                    finish();
                }
            }
        });

        severityDialog.show();
    }

    private void showHighSeverityPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.popup_high_severity, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);

        Button okBtn = dialogView.findViewById(R.id.okBtn);
        okBtn.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(CalculusActivity1.this, DashBoardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        dialog.show();
    }

    private int getScore(RadioGroup rg) {
        int id = rg.getCheckedRadioButtonId();
        if (id == -1) return 1;
        String name = getResources().getResourceEntryName(id);

        if (name.endsWith("_healthy")) return 0;
        if (name.endsWith("_low")) return 1;
        if (name.endsWith("_med")) return 2;
        if (name.endsWith("_high")) return 3;
        return 1;
    }
}