package com.example.gringuard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class plan_fo_21_days extends AppCompatActivity {

    private String finalDisease;
    private String finalSeverity;

    // ✅ FIX: prevent double execution
    private boolean isProcessed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ✅ FIX: prevent activity opening twice
        if (savedInstanceState != null) {
            finish();
            return;
        }

        setContentView(R.layout.popup_21_day_plan);

        finalDisease  = getIntent().getStringExtra("DISEASE_KEY");
        finalSeverity = getIntent().getStringExtra("SEVERITY_KEY");

        if (finalDisease == null || finalSeverity == null) {
            Toast.makeText(this, "Error: missing data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Button btnYes = findViewById(R.id.btnYes);
        Button btnNo  = findViewById(R.id.btnNo);

        btnYes.setOnClickListener(v -> {

            // 🚨 STOP double click
            if (isProcessed) return;
            isProcessed = true;

            v.setEnabled(false);

            saveToFirebase(finalDisease, finalSeverity);
        });

        btnNo.setOnClickListener(v -> {
            Intent intent = new Intent(plan_fo_21_days.this, DashBoardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void saveToFirebase(String disease, String severity) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid       = user.getUid();
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        long timestamp   = System.currentTimeMillis();

        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("Users").child(uid);

        HashMap<String, Object> treatmentData = new HashMap<>();
        treatmentData.put("disease", disease);
        treatmentData.put("severity", severity);
        treatmentData.put("timestamp", timestamp);

        userRef.child("CurrentTreatment").updateChildren(treatmentData)
                .addOnSuccessListener(aVoid -> {

                    DatabaseReference diseasesRef = userRef.child("DetectedDiseases");
                    String diseaseId = diseasesRef.push().getKey();

                    HashMap<String, Object> diseaseData = new HashMap<>();
                    diseaseData.put("diseaseName", disease);
                    diseaseData.put("severity", severity);
                    diseaseData.put("detectedDate", todayDate);
                    diseaseData.put("timestamp", timestamp);

                    diseasesRef.child(diseaseId).setValue(diseaseData)
                            .addOnSuccessListener(aVoid2 -> {

                                HashMap<String, Object> severityData = new HashMap<>();
                                severityData.put("severity", severity);
                                severityData.put("checkedDate", todayDate);
                                severityData.put("weekNumber", 1);
                                severityData.put("timestamp", timestamp);

                                userRef.child("SeverityHistory")
                                        .child(diseaseId)
                                        .child("week_1")
                                        .setValue(severityData)
                                        .addOnSuccessListener(aVoid3 -> {

                                            getSharedPreferences("GringuardPrefs_" + uid, MODE_PRIVATE)
                                                    .edit()
                                                    .putString("activeDiseaseKey", diseaseId)
                                                    .putString("activeDiseaseName", disease)
                                                    .putString("activeSeverity", severity)
                                                    .apply();

                                            navigateToHealth(diseaseId, disease);
                                        });
                            });
                });
    }

    private void navigateToHealth(String diseaseId, String disease) {
        Intent intent = new Intent(plan_fo_21_days.this, HealthActivity.class);
        intent.putExtra("DISEASE_ID_KEY", diseaseId);
        intent.putExtra("DISEASE_NAME_KEY", disease);
        startActivity(intent);
        finish();
    }
}