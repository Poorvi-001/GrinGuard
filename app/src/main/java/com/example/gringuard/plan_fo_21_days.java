package com.example.gringuard;
import static android.content.Intent.getIntent;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
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
        // 1. Get the current authenticated user
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // 2. Point to the base "Users" node, then the specific UID
        // This ensures it lands exactly where your other data is stored
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);

        // 3. Create the Treatment object
        Treatment plan = new Treatment(disease, severity, System.currentTimeMillis());

        // 4. Use .child("CurrentTreatment") to create it as a sibling to 'email' and 'fname'
        userRef.child("CurrentTreatment").setValue(plan)
                .addOnSuccessListener(aVoid -> {
                    Log.d("DEBUG", "Data successfully saved as a sibling to your user profile!");
                    startActivity(new Intent(this, HealthActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> Log.e("DEBUG", "Write failed", e));
    }
}