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
import com.google.firebase.auth.FirebaseAuth;

public class Caries_Activity extends AppCompatActivity {

    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.caries_severity);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Button btnAnalyze  = findViewById(R.id.btnAnalyze);
        RadioGroup rgVisual = findViewById(R.id.rgVisual);
        RadioGroup rgSweets = findViewById(R.id.rgSweets);
        RadioGroup rgTemp   = findViewById(R.id.rgTemp);
        RadioGroup rgNight  = findViewById(R.id.rgNight);
        RadioGroup rgFood   = findViewById(R.id.rgFood);
        RadioGroup rgGums   = findViewById(R.id.rgGums);

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
            for (int s : scores) if (s > maxSeverity) maxSeverity = s;

            String message;
            String severity;

            if (maxSeverity == 3 || scores[2] == 3) {
                message  = "HIGH SEVERITY: Nerve Involvement\nPain to heat or night pain indicates the caries has reached the nerve. Root canal likely needed.";
                severity = "high";
            } else if (maxSeverity == 2) {
                message  = "MEDIUM SEVERITY: Dentin Decay\nThe decay has reached the sensitive layer. Needs a filling immediately to avoid a root canal.";
                severity = "medium";
            } else {
                message  = "LOW SEVERITY: Enamel Decay\nEarly stage decay. May be reversible with fluoride treatment or a simple filling.";
                severity = "low";
            }

            // ✅ UID-scoped prefs
            getSharedPreferences("DentalData_" + uid, MODE_PRIVATE)
                    .edit().putString("detectedDisease", "Cavity")
                    .putString("severity", severity).apply();

            getSharedPreferences("SeverityPrefs_" + uid, MODE_PRIVATE)
                    .edit().putLong("startTime", System.currentTimeMillis())
                    .putInt("lastCheckDay", 1).apply();

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
        Button btnOkResult       = dialogView.findViewById(R.id.btnOkResult);
        tvSeverityValue.setText(resultMessage);

        if (severity.equals("low"))         tvSeverityValue.setTextColor(Color.parseColor("#4CAF50"));
        else if (severity.equals("medium")) tvSeverityValue.setTextColor(Color.parseColor("#FFEB3B"));
        else if (severity.equals("high"))   tvSeverityValue.setTextColor(Color.parseColor("#F44336"));

        btnOkResult.setOnClickListener(v -> {
            severityDialog.dismiss();
            if (severity.equals("high")) showHighSeverityPopup();
            else show21DayPlanPopup(severity);
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
            Intent intent = new Intent(Caries_Activity.this, DashBoardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        dialog.show();
    }

    private void show21DayPlanPopup(String severity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.popup_21_day_plan, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);

        Button btnYes = dialogView.findViewById(R.id.btnYes);
        Button btnNo  = dialogView.findViewById(R.id.btnNo);

        btnYes.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(Caries_Activity.this, plan_fo_21_days.class);
            intent.putExtra("DISEASE_KEY", "Caries");
            intent.putExtra("SEVERITY_KEY", severity);
            startActivity(intent);
            finish();
        });
        btnNo.setOnClickListener(v -> {
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
        if (id == -1) return 1;
        String name = getResources().getResourceEntryName(id);
        if (name.endsWith("_low"))  return 1;
        if (name.endsWith("_med"))  return 2;
        if (name.endsWith("_high")) return 3;
        return 1;
    }
}