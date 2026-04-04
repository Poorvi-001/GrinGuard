package com.example.gringuard;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FollowPlanFracturedMedium extends AppCompatActivity {

    CheckBox checkHardFood, checkBrush, checkRinse, checkColdFood, checkMonitor;
    Button savePlanBtn;

    SharedPreferences sharedPreferences;
    DatabaseReference db;
    String uid, diseaseKey, today;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.follow_plan_fractured_medium);

        checkHardFood = findViewById(R.id.checkHardFood);
        checkBrush    = findViewById(R.id.checkBrush);
        checkRinse    = findViewById(R.id.checkRinse);
        checkColdFood = findViewById(R.id.checkColdFood);
        checkMonitor  = findViewById(R.id.checkMonitor);
        savePlanBtn   = findViewById(R.id.savePlanBtn);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db  = FirebaseDatabase.getInstance().getReference();

        sharedPreferences = getSharedPreferences("GringuardPrefs_" + uid, MODE_PRIVATE);
        diseaseKey = sharedPreferences.getString("activeDiseaseKey", "");

        today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        checkAndResetIfNewDay();
        loadProgressFromFirebase();

        savePlanBtn.setOnClickListener(v -> saveProgress());
    }

    // Enable or disable all inputs
    private void setInputsEnabled(boolean enabled) {
        checkHardFood.setEnabled(enabled);
        checkBrush.setEnabled(enabled);
        checkRinse.setEnabled(enabled);
        checkColdFood.setEnabled(enabled);
        checkMonitor.setEnabled(enabled);
        savePlanBtn.setEnabled(enabled);
        savePlanBtn.setAlpha(enabled ? 1.0f : 0.5f);
        savePlanBtn.setText(enabled ? "Save Progress" : "Already saved today ✓");
    }

    //Reset checkboxes if it's a new day, else disable
    private void checkAndResetIfNewDay() {
        String lastSavedDate = sharedPreferences.getString("lastSavedDate_fractured_medium", "");
        if (!today.equals(lastSavedDate)) {
            checkHardFood.setChecked(false);
            checkBrush.setChecked(false);
            checkRinse.setChecked(false);
            checkColdFood.setChecked(false);
            checkMonitor.setChecked(false);
            setInputsEnabled(true);
        } else {
            setInputsEnabled(false);
        }
    }

    // Load today's progress from Firebase
    private void loadProgressFromFirebase() {
        if (diseaseKey.isEmpty()) return;

        String dayKey = "day_" + today;
        db.child("Users").child(uid)
                .child("FollowPlan").child(diseaseKey)
                .child(dayKey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String savedDate = snapshot.child("date").getValue(String.class);
                            if (today.equals(savedDate)) {
                                Boolean b1 = snapshot.child("cb1").getValue(Boolean.class);
                                Boolean b2 = snapshot.child("cb2").getValue(Boolean.class);
                                Boolean b3 = snapshot.child("cb3").getValue(Boolean.class);
                                Boolean b4 = snapshot.child("cb4").getValue(Boolean.class);
                                Boolean b5 = snapshot.child("cb5").getValue(Boolean.class);
                                checkHardFood.setChecked(b1 != null && b1);
                                checkBrush.setChecked(b2 != null && b2);
                                checkRinse.setChecked(b3 != null && b3);
                                checkColdFood.setChecked(b4 != null && b4);
                                checkMonitor.setChecked(b5 != null && b5);
                                setInputsEnabled(false);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError error) {}
                });
    }

    //  Save progress to Firebase
    private void saveProgress() {
        if (diseaseKey.isEmpty()) {
            Toast.makeText(this, "No active disease found. Please scan first.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean cb1 = checkHardFood.isChecked();
        boolean cb2 = checkBrush.isChecked();
        boolean cb3 = checkRinse.isChecked();
        boolean cb4 = checkColdFood.isChecked();
        boolean cb5 = checkMonitor.isChecked();

        int completed = 0;
        if (cb1) completed++;
        if (cb2) completed++;
        if (cb3) completed++;
        if (cb4) completed++;
        if (cb5) completed++;
        int percentage = (completed * 100) / 5;

        Map<String, Object> dayData = new HashMap<>();
        dayData.put("date", today);
        dayData.put("cb1", cb1);
        dayData.put("cb2", cb2);
        dayData.put("cb3", cb3);
        dayData.put("cb4", cb4);
        dayData.put("cb5", cb5);
        dayData.put("percentage", percentage);
        dayData.put("planType", "fractured_medium");

        String dayKey = "day_" + today;

        db.child("Users").child(uid)
                .child("FollowPlan").child(diseaseKey)
                .child(dayKey).setValue(dayData)
                .addOnSuccessListener(unused -> {

                    getSharedPreferences("CalendarProgress_" + uid, MODE_PRIVATE)
                            .edit().putInt(today, percentage).apply();

                    sharedPreferences.edit()
                            .putString("lastSavedDate_fractured_medium", today)
                            .apply();

                    setInputsEnabled(false);

                    Toast.makeText(this,
                            "Progress saved! " + percentage + "% completed today.",
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to save: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show());
    }
}