package com.example.gringuard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class Gingivitis_Activity extends AppCompatActivity {

    private boolean fromHealthTracker = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gingivitis);

        fromHealthTracker = getIntent().getBooleanExtra("FROM_HEALTH_TRACKER", false);

        RadioGroup rgVisual = findViewById(R.id.rgVisual);
        RadioGroup rgColor = findViewById(R.id.rgColor);
        RadioGroup rgSensitivity = findViewById(R.id.rgSensitivity);
        RadioGroup rgSwelling = findViewById(R.id.rgSwelling);
        RadioGroup rgCoverage = findViewById(R.id.rgCoverage);
        RadioGroup rgBreath = findViewById(R.id.rgBreath);

        Button btnCalculate = findViewById(R.id.btnCalculate);
        TextView tvResult = findViewById(R.id.tvResult);

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
            int[] scores = {vScore, cScore, sScore, swScore, coScore, bScore};
            for (int s : scores) if (s == 2) mediumCount++;

            String resultText;
            String severity;

            if (vScore == 3 && sScore == 1) {
                resultText = "HIGH SEVERITY: Chronic Infection\nSpontaneous bleeding without pain can indicate advanced gum disease (Periodontitis). Seek a professional deep cleaning.";
                severity = "high";
            } else if (maxSeverity == 3 || (maxSeverity == 2 && mediumCount >= 3)) {
                resultText = "HIGH SEVERITY: Severe Gingivitis\nSignificant inflammation and tissue distress. Requires professional dental intervention to prevent tooth loss.";
                severity = "high";
            } else if (maxSeverity == 2) {
                resultText = "MEDIUM SEVERITY: Moderate\nGums are infected. Improved hygiene is needed along with a professional cleaning to reverse the damage.";
                severity = "medium";
            } else {
                resultText = "LOW SEVERITY: Mild\nEarly stage inflammation. Increase flossing and use an antiseptic mouthwash to reverse symptoms at home.";
                severity = "low";
            }

            SharedPreferences prefs = getSharedPreferences("DentalData", MODE_PRIVATE);
            prefs.edit().putString("detectedDisease", "Gingivitis").putString("severity", severity).apply();

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
        View dialogView = getLayoutInflater().inflate(R.layout.severity_popup, null);
        builder.setView(dialogView);

        AlertDialog severityDialog = builder.create();
        severityDialog.setCancelable(false);

        TextView tvSeverityValue = dialogView.findViewById(R.id.tvSeverityValue);
        Button btnOkResult = dialogView.findViewById(R.id.btnOkResult);

        tvSeverityValue.setText(resultMessage);

        if (severity.equals("low")) {
            tvSeverityValue.setTextColor(Color.parseColor("#4CAF50"));
        } else if (severity.equals("medium")) {
            tvSeverityValue.setTextColor(Color.parseColor("#FBC02D"));
        } else if (severity.equals("high")) {
            tvSeverityValue.setTextColor(Color.parseColor("#F44336"));
        }

        btnOkResult.setOnClickListener(v -> {
            severityDialog.dismiss();
            if (severity.equals("high")) {
                showHighSeverityPopup();
            } else {
                if (fromHealthTracker) {
                    Intent intent = new Intent(Gingivitis_Activity.this, HealthActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    // Start plan_fo_21_days activity directly to avoid double dialog
                    Intent intent = new Intent(Gingivitis_Activity.this, plan_fo_21_days.class);
                    intent.putExtra("DISEASE_KEY", "Gingivitis");
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
            Intent intent = new Intent(Gingivitis_Activity.this, DashBoardActivity.class);
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
