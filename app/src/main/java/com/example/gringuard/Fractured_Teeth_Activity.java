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

public class Fractured_Teeth_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fractured_teeth_severity);

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
            int resultColor;
            String severity = "high";

            if (maxSeverity == 3 || sScore == 3 || spScore == 3) {
                resultText = "HIGH SEVERITY: Vertical Fracture\nVisible line extending below gum or mobility indicates a non-restorable crack. Emergency extraction likely.";
                resultColor = 0xFFD81B60;
                severity = "high";
            }
            else if (maxSeverity == 2) {
                resultText = "MEDIUM SEVERITY: Cracked Tooth\nDeep enamel/dentin crack. Causes pain on release of bite. Requires a crown to prevent complete split.";
                resultColor = 0xFFF4511E;
                severity = "medium";
            }
            else {
                resultText = "LOW SEVERITY: Craze Lines\nMicroscopic cracks in enamel only. Mostly aesthetic; no immediate danger. Avoid biting hard objects.";
                resultColor = 0xFF2E7D32;
                severity = "low";
            }

            tvResult.setText(resultText);
            tvResult.setTextColor(resultColor);

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

    private int getScore(RadioGroup rg) {
        int id = rg.getCheckedRadioButtonId();
        String name = getResources().getResourceEntryName(id);
        if (name.endsWith("_low")) return 1;
        if (name.endsWith("_med")) return 2;
        if (name.endsWith("_high")) return 3;
        return 1;
    }
}
