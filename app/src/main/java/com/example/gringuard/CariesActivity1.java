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
import com.google.firebase.auth.FirebaseAuth;

public class CariesActivity1 extends AppCompatActivity {

    private boolean fromHealthTracker = false;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.caries_severity1);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
            return;
        }
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        fromHealthTracker = getIntent().getBooleanExtra("FROM_HEALTH_TRACKER", false);

        Button btnAnalyze = findViewById(R.id.btnAnalyze);

        // Initializing all 7 RadioGroups
        RadioGroup rgVisual = findViewById(R.id.rgVisual);
        RadioGroup rgSweets = findViewById(R.id.rgSweets);
        RadioGroup rgTemp = findViewById(R.id.rgTemp);
        RadioGroup rgNight = findViewById(R.id.rgNight);
        RadioGroup rgFood = findViewById(R.id.rgFood);
        RadioGroup rgGums = findViewById(R.id.rgGums);
        RadioGroup rgTexture = findViewById(R.id.rgTexture); 

        btnAnalyze.setOnClickListener(v -> {
            // Validation: Ensure all 7 questions are answered
            if (rgVisual.getCheckedRadioButtonId() == -1 || rgSweets.getCheckedRadioButtonId() == -1 ||
                    rgTemp.getCheckedRadioButtonId() == -1 || rgNight.getCheckedRadioButtonId() == -1 ||
                    rgFood.getCheckedRadioButtonId() == -1 || rgGums.getCheckedRadioButtonId() == -1 ||
                    rgTexture.getCheckedRadioButtonId() == -1) {
                Toast.makeText(this, "Please answer all 7 questions", Toast.LENGTH_SHORT).show();
                return;
            }

            int vScore = getScore(rgVisual);
            int sScore = getScore(rgSweets);
            int tScore = getScore(rgTemp);
            int nScore = getScore(rgNight);
            int fScore = getScore(rgFood);
            int gScore = getScore(rgGums);
            int txScore = getScore(rgTexture);

            int[] scores = {vScore, sScore, tScore, nScore, fScore, gScore, txScore};

            int maxSeverity = 0;
            int healthyCount = 0;
            for (int s : scores) {
                if (s > maxSeverity) maxSeverity = s;
                if (s == 0) healthyCount++;
            }

            String message;
            String severity;

            // --- SEVERITY LOGIC ---
            if (healthyCount == 7) {
                message = "EXCELLENT: Healthy Teeth\nNo signs of decay. Keep up the great hygiene and limit sugar intake!";
                severity = "healthy";
            } else if (maxSeverity == 3 || tScore == 3 || nScore == 3) {
                message = "HIGH SEVERITY: Nerve Involvement\nPain to heat or night pain indicates the caries has reached the nerve. Root canal likely needed.";
                severity = "high";
            } else if (maxSeverity == 2) {
                message = "MEDIUM SEVERITY: Dentin Decay\nThe decay has reached the sensitive layer. Needs a filling immediately.";
                severity = "medium";
            } else {
                message = "LOW SEVERITY: Enamel Decay\nEarly stage decay. May be reversible with fluoride treatment.";
                severity = "low";
            }

            // --- STORAGE LOGIC (UID scoped) ---

            // 1. Calculate the tracking day
            SharedPreferences sevPrefs = getSharedPreferences("SeverityPrefs_" + uid, MODE_PRIVATE);
            long startTime = sevPrefs.getLong("startTime", 0);
            long currentTime = System.currentTimeMillis();
            if (startTime == 0) {
                startTime = currentTime;
                sevPrefs.edit().putLong("startTime", startTime).apply();
            }
            int currentDay = (int) ((currentTime - startTime) / (24 * 60 * 60 * 1000)) + 1;
            sevPrefs.edit().putInt("lastCheckDay", currentDay).apply();

            // 2. Save history for Graph (UID scoped)
            SharedPreferences historyPrefs = getSharedPreferences("SeverityHistory_" + uid, MODE_PRIVATE);
            historyPrefs.edit().putString(String.valueOf(currentDay), severity).apply();

            // 3. Save current result (UID scoped)
            SharedPreferences prefs = getSharedPreferences("DentalData_" + uid, MODE_PRIVATE);
            prefs.edit().putString("detectedDisease", "Caries").putString("severity", severity).apply();

            showSeverityPopup(message, severity);
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

        // Color coding results
        if (severity.equals("healthy")) {
            tvSeverityValue.setTextColor(Color.parseColor("#2E7D32")); // Deep Green
        } else if (severity.equals("low")) {
            tvSeverityValue.setTextColor(Color.parseColor("#4CAF50")); // Green
        } else if (severity.equals("medium")) {
            tvSeverityValue.setTextColor(Color.parseColor("#FBC02D")); // Yellow/Amber
        } else if (severity.equals("high")) {
            tvSeverityValue.setTextColor(Color.parseColor("#F44336")); // Red
        }

        btnOkResult.setOnClickListener(v -> {
            severityDialog.dismiss();
            if (severity.equals("high")) {
                showHighSeverityPopup();
            } else {
                Intent intent;
                if (fromHealthTracker) {
                    intent = new Intent(CariesActivity1.this, HealthActivity.class);
                } else {
                    intent = new Intent(CariesActivity1.this, plan_fo_21_days.class);
                    intent.putExtra("DISEASE_KEY", "Caries");
                    intent.putExtra("SEVERITY_KEY", severity);
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
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
        dialogView.findViewById(R.id.okBtn).setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(this, DashBoardActivity.class);
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
