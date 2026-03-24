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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_21_day_plan);

        finalDisease = getIntent().getStringExtra("DISEASE_KEY");
        finalSeverity = getIntent().getStringExtra("SEVERITY_KEY");

        Log.d("DEBUG_DATA", "Disease: " + finalDisease + " | Severity: " + finalSeverity);

        if (finalDisease == null || finalSeverity == null) {
            Log.e("DEBUG_DATA", "❌ Disease or Severity is NULL — going back");
            Toast.makeText(this, "Error: missing data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Button btnYes = findViewById(R.id.btnYes);
        Button btnNo  = findViewById(R.id.btnNo);

        if (btnYes == null) {
            Log.e("DEBUG_DATA", "❌ btnYes is NULL — check your layout ID");
            return;
        }

        btnYes.setOnClickListener(v -> saveToFirebase(finalDisease, finalSeverity));

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

        String uid = user.getUid();
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        long timestamp   = System.currentTimeMillis();

        // ── Day number for severity graph ──
        long startTime = getSharedPreferences("SeverityPrefs", MODE_PRIVATE).getLong("startTime", 0);
        if (startTime == 0) {
            startTime = timestamp;
            getSharedPreferences("SeverityPrefs", MODE_PRIVATE)
                    .edit().putLong("startTime", startTime).apply();
        }
        int currentDay = (int) ((timestamp - startTime) / (24 * 60 * 60 * 1000)) + 1;

        // ── FIX #1: Always keep "DentalData" in sync so HealthActivity can read it ──
        getSharedPreferences("DentalData", MODE_PRIVATE)
                .edit()
                .putString("detectedDisease", disease)
                .putString("severity", severity)
                .apply();

        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(uid);

        // SAVE 1 — CurrentTreatment
        HashMap<String, Object> treatmentData = new HashMap<>();
        treatmentData.put("disease",   disease);
        treatmentData.put("severity",  severity);
        treatmentData.put("timestamp", timestamp);

        userRef.child("CurrentTreatment").updateChildren(treatmentData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("DEBUG", "✅ CurrentTreatment saved");

                    // SAVE 2 — DetectedDiseases
                    DatabaseReference diseasesRef = userRef.child("DetectedDiseases");
                    String diseaseId = diseasesRef.push().getKey();

                    HashMap<String, Object> diseaseData = new HashMap<>();
                    diseaseData.put("diseaseName",   disease);
                    diseaseData.put("severity",      severity);
                    diseaseData.put("detectedDate",  todayDate);
                    diseaseData.put("timestamp",     timestamp);

                    diseasesRef.child(diseaseId).setValue(diseaseData)
                            .addOnSuccessListener(aVoid2 -> {
                                Log.d("DEBUG", "✅ DetectedDiseases saved | ID: " + diseaseId);

                                // SAVE 3 — SeverityHistory week_1
                                HashMap<String, Object> severityData = new HashMap<>();
                                severityData.put("severity",    severity);
                                severityData.put("checkedDate", todayDate);
                                severityData.put("weekNumber",  1);
                                severityData.put("timestamp",   timestamp);

                                userRef.child("SeverityHistory")
                                        .child(diseaseId)
                                        .child("week_1")
                                        .setValue(severityData)
                                        .addOnSuccessListener(aVoid3 -> {
                                            Log.d("DEBUG", "✅ SeverityHistory week_1 saved");

                                            // SAVE 4 — SeverityGraph (FIX #2: restored)
                                            HashMap<String, Object> graphData = new HashMap<>();
                                            graphData.put("day",      currentDay);
                                            graphData.put("severity", severity);
                                            graphData.put("date",     todayDate);

                                            userRef.child("SeverityGraph")
                                                    .child(String.valueOf(currentDay))
                                                    .setValue(graphData)
                                                    .addOnSuccessListener(aVoid4 ->
                                                            Log.d("DEBUG", "✅ SeverityGraph saved for day " + currentDay))
                                                    .addOnFailureListener(e ->
                                                            Log.e("DEBUG", "❌ SeverityGraph failed: " + e.getMessage()));

                                            // FIX #3: also mirror to local SeverityHistory SharedPrefs
                                            // so TrackGoalsActivity graph works without extra Firebase call
                                            getSharedPreferences("SeverityHistory", MODE_PRIVATE)
                                                    .edit()
                                                    .putString(String.valueOf(currentDay), severity)
                                                    .apply();

                                            // Save diseaseId to SharedPrefs (UID-scoped)
                                            getSharedPreferences("GringuardPrefs_" + uid, MODE_PRIVATE)
                                                    .edit()
                                                    .putString("activeDiseaseKey",  diseaseId)
                                                    .putString("activeDiseaseName", disease)
                                                    .putString("activeSeverity",    severity)
                                                    .apply();
                                            String uidPref = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                            getSharedPreferences("DentalData_" + uidPref, MODE_PRIVATE)
                                                    .edit()
                                                    .putString("detectedDisease", disease)
                                                    .putString("severity", severity)
                                                    .apply();

                                            navigateToHealth(diseaseId, disease);
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("DEBUG", "❌ SeverityHistory failed: " + e.getMessage());

                                            getSharedPreferences("GringuardPrefs_" + uid, MODE_PRIVATE)
                                                    .edit()
                                                    .putString("activeDiseaseKey",  diseaseId)
                                                    .putString("activeDiseaseName", disease)
                                                    .putString("activeSeverity",    severity)
                                                    .apply();

                                            navigateToHealth(diseaseId, disease);
                                        });
                            })
                            .addOnFailureListener(e -> {
                                Log.e("DEBUG", "❌ DetectedDiseases failed: " + e.getMessage());
                                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("DEBUG", "❌ CurrentTreatment failed: " + e.getMessage());
                    Toast.makeText(this, "Save Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void navigateToHealth(String diseaseId, String disease) {
        Intent intent = new Intent(plan_fo_21_days.this, HealthActivity.class);
        intent.putExtra("DISEASE_ID_KEY",   diseaseId);
        intent.putExtra("DISEASE_NAME_KEY", disease);
        startActivity(intent);
        finish();
    }
}