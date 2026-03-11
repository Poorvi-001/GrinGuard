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

public class Gingivitis_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gingivitis);

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
            String severity = "high";

            if (vScore == 3 && sScore == 1) {
                resultText = "HIGH SEVERITY: Chronic Infection\nSpontaneous bleeding without pain can indicate advanced gum disease (Periodontitis). Seek a professional deep cleaning.";
                severity = "high";
            }
            else if (maxSeverity == 3 || (maxSeverity == 2 && mediumCount >= 3)) {
                resultText = "HIGH SEVERITY: Severe Gingivitis\nSignificant inflammation and tissue distress. Requires professional dental intervention to prevent tooth loss.";
                severity = "high";
            }
            else if (maxSeverity == 2) {
                resultText = "MEDIUM SEVERITY: Moderate\nGums are infected. Improved hygiene is needed along with a professional cleaning to reverse the damage.";
                severity = "medium";
            }
            else {
                resultText = "LOW SEVERITY: Mild\nEarly stage inflammation. Increase flossing and use an antiseptic mouthwash to reverse symptoms at home.";
                severity = "low";
            }

            // Store the severity and disease type for HealthTracker
            SharedPreferences prefs = getSharedPreferences("DentalData", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("detectedDisease", "Gingivitis");
            editor.putString("severity", severity);
            editor.apply();

            // Initialize 21-day program state for HealthActivity
            SharedPreferences sevPrefs = getSharedPreferences("SeverityPrefs", MODE_PRIVATE);
            long startTime = sevPrefs.getLong("startTime", 0);
            long currentTime = System.currentTimeMillis();
            if (startTime == 0) {
                sevPrefs.edit().putLong("startTime", currentTime).apply();
            }
            int currentDay = (int) ((currentTime - (startTime == 0 ? currentTime : startTime)) / (24 * 60 * 60 * 1000)) + 1;
            sevPrefs.edit().putInt("lastCheckDay", currentDay).apply();

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
                show21DayPlanPopup();
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
            Intent intent = new Intent(Gingivitis_Activity.this, DashBoardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        dialog.show();
    }

    private void show21DayPlanPopup() {
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
            Intent intent = new Intent(Gingivitis_Activity.this, HealthActivity.class);
            startActivity(intent);
            finish();
        });

        btnNo.setOnClickListener(v -> {
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
        String name = getResources().getResourceEntryName(id);
        if (name.endsWith("_low")) return 1;
        if (name.endsWith("_med")) return 2;
        if (name.endsWith("_high")) return 3;
        return 1;
    }
}
