package com.example.gringuard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class Caries_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.caries_severity);

        Button btnAnalyze = findViewById(R.id.btnAnalyze);
        TextView tvResult = findViewById(R.id.tvCariesResult);

        RadioGroup rgVisual = findViewById(R.id.rgVisual);
        RadioGroup rgSweets = findViewById(R.id.rgSweets);
        RadioGroup rgTemp = findViewById(R.id.rgTemp);
        RadioGroup rgNight = findViewById(R.id.rgNight);
        RadioGroup rgFood = findViewById(R.id.rgFood);
        RadioGroup rgGums = findViewById(R.id.rgGums);

        btnAnalyze.setOnClickListener(v -> {
            if (rgVisual.getCheckedRadioButtonId() == -1 || rgSweets.getCheckedRadioButtonId() == -1 ||
                    rgTemp.getCheckedRadioButtonId() == -1 || rgNight.getCheckedRadioButtonId() == -1 ||
                    rgFood.getCheckedRadioButtonId() == -1 || rgGums.getCheckedRadioButtonId() == -1) {
                Toast.makeText(this, "Please answer all 6 questions", Toast.LENGTH_SHORT).show();
                return;
            }

            int[] scores = {getScore(rgVisual), getScore(rgSweets), getScore(rgTemp),
                    getScore(rgNight), getScore(rgFood), getScore(rgGums)};

            int maxSeverity = 0;
            for(int s : scores) if (s > maxSeverity) maxSeverity = s;

            String message;
            int color;
            String severity = "high";

            if (maxSeverity == 3 || scores[2] == 3) {
                message = "HIGH SEVERITY: Nerve Involvement\nPain to heat or night pain indicates the caries has reached the nerve. Root canal likely needed.";
                color = 0xFFD81B60;
                severity = "high";
            } else if (maxSeverity == 2) {
                message = "MEDIUM SEVERITY: Dentin Decay\nThe decay has reached the sensitive layer. Needs a filling immediately to avoid a root canal.";
                color = 0xFFF4511E;
                severity = "medium";
            } else {
                message = "LOW SEVERITY: Enamel Decay\nEarly stage decay. May be reversible with fluoride treatment or a simple filling.";
                color = 0xFF2E7D32;
                severity = "low";
            }

            tvResult.setText(message);
            tvResult.setTextColor(color);

            // Store the severity and disease type for HealthTracker
            SharedPreferences prefs = getSharedPreferences("DentalData", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("detectedDisease", "Cavity");
            editor.putString("severity", severity);
            editor.apply();

            // Initialize 21-day program state for HealthActivity
            SharedPreferences sevPrefs = getSharedPreferences("SeverityPrefs", MODE_PRIVATE);
            long startTime = sevPrefs.getLong("startTime", 0);
            long currentTime = System.currentTimeMillis();
            if (startTime == 0) {
                sevPrefs.edit().putLong("startTime", currentTime).apply();
                startTime = currentTime;
            }
            int currentDay = (int) ((currentTime - startTime) / (24 * 60 * 60 * 1000)) + 1;
            sevPrefs.edit().putInt("lastCheckDay", currentDay).apply();

            if (severity.equals("high")) {
                showHighSeverityPopup();
            } else {
                Toast.makeText(this, "Severity Checked: " + severity.toUpperCase(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showHighSeverityPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_high_severity, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false); // Prevents closing on outside click

        Button okBtn = dialogView.findViewById(R.id.okBtn);
        okBtn.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(Caries_Activity.this, DashBoardActivity.class);
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
    // Inside your QuestionnaireActivity, where you finish the scan/quiz:

}
