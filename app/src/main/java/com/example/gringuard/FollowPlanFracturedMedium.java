package com.example.gringuard;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class FollowPlanFracturedMedium extends AppCompatActivity {

    CheckBox checkHardFood, checkBrush, checkRinse, checkColdFood, checkMonitor;
    Button savePlanBtn;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.follow_plan_fractured_medium);

        // Link UI elements
        checkHardFood = findViewById(R.id.checkHardFood);
        checkBrush = findViewById(R.id.checkBrush);
        checkRinse = findViewById(R.id.checkRinse);
        checkColdFood = findViewById(R.id.checkColdFood);
        checkMonitor = findViewById(R.id.checkMonitor);
        savePlanBtn = findViewById(R.id.savePlanBtn);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("FollowPlanFracturedMedium", MODE_PRIVATE);

        // Load saved progress when activity opens
        loadProgress();

        // Save button click
        savePlanBtn.setOnClickListener(v -> saveProgress());
    }

    private void saveProgress() {

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("hardFood_fm", checkHardFood.isChecked());
        editor.putBoolean("brush_fm", checkBrush.isChecked());
        editor.putBoolean("rinse_fm", checkRinse.isChecked());
        editor.putBoolean("coldFood_fm", checkColdFood.isChecked());
        editor.putBoolean("monitor_fm", checkMonitor.isChecked());

        editor.apply();

        int completed = 0;

        if (checkHardFood.isChecked()) completed++;
        if (checkBrush.isChecked()) completed++;
        if (checkRinse.isChecked()) completed++;
        if (checkColdFood.isChecked()) completed++;
        if (checkMonitor.isChecked()) completed++;

        Toast.makeText(
                this,
                "Progress saved! You completed " + completed + " out of 5 tasks today.",
                Toast.LENGTH_LONG
        ).show();
    }

    private void loadProgress() {
        // Correctly load progress using specific keys to avoid overlap
        checkHardFood.setChecked(sharedPreferences.getBoolean("hardFood_fm", false));
        checkBrush.setChecked(sharedPreferences.getBoolean("brush_fm", false));
        checkRinse.setChecked(sharedPreferences.getBoolean("rinse_fm", false));
        checkColdFood.setChecked(sharedPreferences.getBoolean("coldFood_fm", false));
        checkMonitor.setChecked(sharedPreferences.getBoolean("monitor_fm", false));
    }
}
