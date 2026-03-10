package com.example.gringuard;
import static android.content.Intent.getIntent;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class plan_fo_21_days extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_21_day_plan);

        // Retrieve the data passed from your Questionnaire activity
        String finalDisease = getIntent().getStringExtra("DISEASE_KEY");
        String finalSeverity = getIntent().getStringExtra("SEVERITY_KEY");

        findViewById(R.id.btnYes).setOnClickListener(v -> {
            saveToFirebase(finalDisease, finalSeverity);

        });
        Log.d("DEBUG_DATA", "Disease: " + getIntent().getStringExtra("DISEASE_KEY") +
                " Severity: " + getIntent().getStringExtra("SEVERITY_KEY"));
    }

    private void saveToFirebase(String disease, String severity) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // This creates the clean structure your router needs
        Treatment plan = new Treatment(disease, severity, System.currentTimeMillis());

        FirebaseDatabase.getInstance().getReference("Users")
                .child(uid).child("CurrentTreatment")
                .setValue(plan)
                .addOnSuccessListener(a -> {
                    // Now launch the plan activity
                    Intent intent = new Intent(this, HealthActivity.class);
                    startActivity(intent);
                    finish();
                });
    }
}