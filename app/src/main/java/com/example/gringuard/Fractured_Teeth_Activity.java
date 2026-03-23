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

public class Fractured_Teeth_Activity extends AppCompatActivity {

    private boolean fromHealthTracker = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fractured_teeth_severity);

        fromHealthTracker = getIntent().getBooleanExtra("FROM_HEALTH_TRACKER", false);

        RadioGroup rgVisual = findViewById(R.id.rgVisual);
        RadioGroup rgCold = findViewById(R.id.rgCold);
        RadioGroup rgBite = findViewById(R.id.rgBite);
        RadioGroup rgStability = findViewById(R.id.rgStability);
        RadioGroup rgSpontaneous = findViewById(R.id.rgSpontaneous);
        RadioGroup rgGums = findViewById(R.id.rgGums);

        Button btnCalculate = findViewById(R.id.btnCalculate);
        TextView tvResult = findViewById(R.id.tvResult);

        btnCalculate.setOnClickListener(v -> {
            if (rgVisual.getCheckedRadioButtonId() == -1 || rgCold.getCheckedRadioButtonId() == -1 ||
                    rgBite.getCheckedRadioButtonId() == -1 || rgStability.getCheckedRadioButtonId() == -1 ||
                    rgSpontaneous.getCheckedRadioButtonId() == -1 || rgGums.getCheckedRadioButtonId() == -1) {
                Toast.makeText(this, "Please answer all 6 questions", Toast.LENGTH_SHORT).show();
                return;
            }

            int vScore = getScore(rgVisual);
            int cScore = getScore(rgCold);
            int bScore = getScore(rgBite);
            int sScore = getScore(rgStability);
            int spScore = getScore(rgSpontaneous);
            int gScore = getScore(rgGums);

            int maxSeverity = Math.max(vScore, Math.max(cScore, Math.max(bScore,
                    Math.max(sScore, Math.max(spScore, gScore)))));

            String resultText;
            String severity = "high";

            if (maxSeverity == 3 || sScore == 3 || spScore == 3) {
                resultText = "HIGH SEVERITY: Vertical Fracture\nVisible line extending below gum or mobility indicates a non-restorable crack. Emergency extraction likely.";
                severity = "high";
            }
            else if (maxSeverity == 2) {
                resultText = "MEDIUM SEVERITY: Cracked Tooth\nDeep enamel/dentin crack. Causes pain on release of bite. Requires a crown to prevent complete split.";
                severity = "medium";
            }
            else {
                resultText = "LOW SEVERITY: Craze Lines\nMicroscopic cracks in enamel only. Mostly aesthetic; no immediate danger. Avoid biting hard objects.";
                severity = "low";
            }

            // Store the severity and disease type for HealthTracker
            SharedPreferences prefs = getSharedPreferences("DentalData", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("detectedDisease", "Fractured");
            editor.putString("severity", severity);
            editor.apply();

            // Initialize 21-day program state for HealthActivity
            SharedPreferences sevPrefs = getSharedPreferences("SeverityPrefs", MODE_PRIVATE);
            long startTime = sevPrefs.getLong("startTime", 0);
            long currentTime = System.currentTimeMillis();
            if (startTime == 0) {
                startTime = currentTime;
                sevPrefs.edit().putLong("startTime", startTime).apply();
            }
            int currentDay = (int) ((currentTime - startTime) / (24 * 60 * 60 * 1000)) + 1;
            sevPrefs.edit().putInt("lastCheckDay", currentDay).apply();

            // Save history for Graphical Analysis
            SharedPreferences historyPrefs = getSharedPreferences("SeverityHistory", MODE_PRIVATE);
            historyPrefs.edit().putString(String.valueOf(currentDay), severity).apply();

            showSeverityPopup(resultText, severity);
        });
    }

    private void showSeverityPopup(String resultMessage, String severity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.severity_popup, null);
        builder.setView(dialogView);

        AlertDialog severityDialog = builder.create();
        severityDialog.setCancelable(false);

        TextView tvSeverityValue = dialogView.findViewById(R.id.tvSeverityValue);
        Button btnOkResult = dialogView.findViewById(R.id.btnOkResult);

        tvSeverityValue.setText(resultMessage);
        
        if (severity.equals("low")) {
            tvSeverityValue.setTextColor(Color.parseColor("#4CAF50")); // Green
        } else if (severity.equals("medium")) {
            tvSeverityValue.setTextColor(Color.parseColor("#FBC02D")); // Dark Yellow
        } else if (severity.equals("high")) {
            tvSeverityValue.setTextColor(Color.parseColor("#F44336")); // Red
        }

        btnOkResult.setOnClickListener(v -> {
            severityDialog.dismiss();
            if (severity.equals("high")) {
                showHighSeverityPopup();
            } else {
                if (fromHealthTracker) {
                    Intent intent = new Intent(Fractured_Teeth_Activity.this, HealthActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    show21DayPlanPopup(severity);
                }
            }
        });

        severityDialog.show();
    }

    private void showHighSeverityPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_high_severity, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);

        Button okBtn = dialogView.findViewById(R.id.okBtn);
        okBtn.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(Fractured_Teeth_Activity.this, DashBoardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        dialog.show();
    }

    private void show21DayPlanPopup(String severity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_21_day_plan, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);

        Button btnYes = dialogView.findViewById(R.id.btnYes);
        Button btnNo = dialogView.findViewById(R.id.btnNo);

        btnYes.setOnClickListener(v -> {
            dialog.dismiss();

            Intent intent = new Intent(Fractured_Teeth_Activity.this, plan_fo_21_days.class);
            intent.putExtra("DISEASE_KEY", "Fractured Teeth");
            intent.putExtra("SEVERITY_KEY", severity);
            startActivity(intent);
            finish();
        });

        btnNo.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(Fractured_Teeth_Activity.this, DashBoardActivity.class);
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
        if (name.endsWith("_low")) return 1;
        if (name.endsWith("_med")) return 2;
        if (name.endsWith("_high")) return 3;
        return 1;
    }
}
