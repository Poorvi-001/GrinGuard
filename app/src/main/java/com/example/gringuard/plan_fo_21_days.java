package com.example.gringuard; // Ensure this matches your package name

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class plan_fo_21_days extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_21_day_plan); // Replace with your current layout name

        // Trigger the popup automatically (or attach to a button click)
        showPlanDialog();
    }

    private void showPlanDialog() {
        // 1. Create the dialog instance
        final Dialog dialog = new Dialog(this);

        // 2. Set the custom layout we created earlier
        dialog.setContentView(R.layout.popup_21_day_plan); // Replace with your actual XML filename

        // 3. Make background transparent to show custom shape
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // 4. Initialize buttons from your XML
        Button btnYes = dialog.findViewById(R.id.btnYes);
        Button btnNo = dialog.findViewById(R.id.btnNo);

        // 5. YES Click: Go to Health Tracker
        // Inside your showPlanDialog() method, modify the btnYes listener:

        btnYes.setOnClickListener(v -> {
            // 1. Get your data (Assuming you have these available from previous steps)
            String detectedDisease = "Gingivitis"; // Replace with your variable
            int severityScore = 75;               // Replace with your variable

            // 2. Save to Firebase
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference planRef = FirebaseDatabase.getInstance()
                    .getReference("Users").child(uid).child("ActiveTask");

            // Using the Treatment class we created
            Treatment newTreatment = new Treatment(detectedDisease);
            newTreatment.maxSeverity = severityScore; // Ensure Treatment class has this field

            planRef.setValue(newTreatment).addOnSuccessListener(aVoid -> {
                // 3. Now redirect to Health Tracker
                Intent intent = new Intent(getApplicationContext(), HealthActivity.class);
                startActivity(intent);
                dialog.dismiss();
                finish();
            });
        });

        // 6. NO Click: Go to Dashboard
        btnNo.setOnClickListener(v -> {
            Intent intent = new Intent(plan_fo_21_days.this, DashBoardActivity.class);
            // Clear back stack to make Dashboard the home screen
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            dialog.dismiss();
            finish(); // Close this activity
        });

        dialog.show();
    }
}