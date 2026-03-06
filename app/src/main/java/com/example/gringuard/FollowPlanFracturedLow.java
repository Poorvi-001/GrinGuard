package com.example.gringuard;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class FollowPlanFracturedLow extends AppCompatActivity {

    CheckBox checkHardFood, checkBrush, checkRinse, checkColdFood, checkMonitor;
    Button savePlanBtn;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.follow_plan_fractured_low);

        checkHardFood = findViewById(R.id.checkHardFood);
        checkBrush = findViewById(R.id.checkBrush);
        checkRinse = findViewById(R.id.checkRinse);
        checkColdFood = findViewById(R.id.checkColdFood);
        checkMonitor = findViewById(R.id.checkMonitor);
        savePlanBtn = findViewById(R.id.savePlanBtn);

        sharedPreferences = getSharedPreferences("FollowPlanFracturedLow", MODE_PRIVATE);

        loadProgress();

        savePlanBtn.setOnClickListener(v -> saveProgress());
    }

    private void saveProgress() {

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("hardFood", checkHardFood.isChecked());
        editor.putBoolean("brush", checkBrush.isChecked());
        editor.putBoolean("rinse", checkRinse.isChecked());
        editor.putBoolean("coldFood", checkColdFood.isChecked());
        editor.putBoolean("monitor", checkMonitor.isChecked());

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

        checkHardFood.setChecked(sharedPreferences.getBoolean("hardFood", false));
        checkBrush.setChecked(sharedPreferences.getBoolean("brush", false));
        checkRinse.setChecked(sharedPreferences.getBoolean("rinse", false));
        checkColdFood.setChecked(sharedPreferences.getBoolean("coldFood", false));
        checkMonitor.setChecked(sharedPreferences.getBoolean("monitor", false));
    }
}