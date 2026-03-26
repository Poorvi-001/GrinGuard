package com.example.gringuard;

import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class Fractured_Teeth_Activity extends AppCompatActivity {

    RadioGroup rgVisual, rgCold, rgBite, rgStability, rgSpontaneous, rgGums;
    Button btnCalculate;
    TextView tvResult;

    boolean isPopupShowing = false; // 🔴 important fix

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fractured_teeth_severity);

        // 🔹 Initialize views
        rgVisual = findViewById(R.id.rgVisual);
        rgCold = findViewById(R.id.rgCold);
        rgBite = findViewById(R.id.rgBite);
        rgStability = findViewById(R.id.rgStability);
        rgSpontaneous = findViewById(R.id.rgSpontaneous);
        rgGums = findViewById(R.id.rgGums);

        btnCalculate = findViewById(R.id.btnCalculate);
        tvResult = findViewById(R.id.tvResult);

        // ✅ SINGLE CLICK LISTENER (NO DUPLICATION)
        btnCalculate.setOnClickListener(v -> {

            if (isPopupShowing) return;

            int score = calculateScore();

            String severity;
            if (score <= 6) {
                severity = "LOW";
            } else if (score <= 12) {
                severity = "MEDIUM";
            } else {
                severity = "HIGH";
            }

            tvResult.setText("Severity: " + severity);

            showSeverityPopup(severity);
        });
    }

    // 🔹 Calculate score from all questions
    private int calculateScore() {
        int score = 0;

        score += getScore(rgVisual);
        score += getScore(rgCold);
        score += getScore(rgBite);
        score += getScore(rgStability);
        score += getScore(rgSpontaneous);
        score += getScore(rgGums);

        return score;
    }

    // 🔹 Assign score based on selected option
    private int getScore(RadioGroup rg) {
        int selectedId = rg.getCheckedRadioButtonId();

        if (selectedId == -1) return 0;

        if (selectedId == R.id.q1_low || selectedId == R.id.q2_low ||
                selectedId == R.id.q3_low || selectedId == R.id.q4_low ||
                selectedId == R.id.q5_low || selectedId == R.id.q6_low) {
            return 1;
        }

        if (selectedId == R.id.q1_med || selectedId == R.id.q2_med ||
                selectedId == R.id.q3_med || selectedId == R.id.q4_med ||
                selectedId == R.id.q5_med || selectedId == R.id.q6_med) {
            return 2;
        }

        return 3; // high
    }

    // 🔴 FIRST POPUP (Severity)
    private void showSeverityPopup(String severity) {

        if (isPopupShowing) return;
        isPopupShowing = true;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Severity Result");
        builder.setMessage("Your severity is: " + severity);

        builder.setCancelable(false);

        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();

            // ✅ ONLY ONE PLACE CALL
            show21DayPlanPopup();
        });

        builder.show();
    }

    // 🔴 SECOND POPUP (21-day plan)
    private void show21DayPlanPopup() {

        Log.d("DEBUG", "21 DAY POPUP CALLED"); // debug check

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Follow Plan");
        builder.setMessage("Do you want to follow a 21 days plan?");

        builder.setCancelable(false);

        builder.setPositiveButton("Yes", (dialog, which) -> {
            dialog.dismiss();
            isPopupShowing = false;
            Toast.makeText(this, "Plan Started", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.dismiss();
            isPopupShowing = false;
            Toast.makeText(this, "Plan Skipped", Toast.LENGTH_SHORT).show();
        });

        builder.show();
    }
}