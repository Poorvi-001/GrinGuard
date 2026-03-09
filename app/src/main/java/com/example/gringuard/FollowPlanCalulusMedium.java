package com.example.gringuard;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class FollowPlanCalulusMedium extends AppCompatActivity {

    CheckBox checkHardFood, checkBrush, checkRinse, checkColdFood, checkMonitor;
    Button savePlanBtn;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.follow_plan_calculus_medium);

        checkHardFood = findViewById(R.id.checkHardFood);
        checkBrush = findViewById(R.id.checkBrush);
        checkRinse = findViewById(R.id.checkRinse);
        checkColdFood = findViewById(R.id.checkColdFood);
        checkMonitor = findViewById(R.id.checkMonitor);

        savePlanBtn = findViewById(R.id.savePlanBtn);

        sharedPreferences = getSharedPreferences("CalculusMediumPlan", MODE_PRIVATE);

        loadProgress();

        savePlanBtn.setOnClickListener(v -> saveProgress());
    }

    private void saveProgress() {

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("check1", checkHardFood.isChecked());
        editor.putBoolean("check2", checkBrush.isChecked());
        editor.putBoolean("check3", checkRinse.isChecked());
        editor.putBoolean("check4", checkColdFood.isChecked());
        editor.putBoolean("check5", checkMonitor.isChecked());

        editor.apply();

        Toast.makeText(this, "Progress Saved Successfully!", Toast.LENGTH_SHORT).show();
    }

    private void loadProgress() {

        checkHardFood.setChecked(sharedPreferences.getBoolean("check1", false));
        checkBrush.setChecked(sharedPreferences.getBoolean("check2", false));
        checkRinse.setChecked(sharedPreferences.getBoolean("check3", false));
        checkColdFood.setChecked(sharedPreferences.getBoolean("check4", false));
        checkMonitor.setChecked(sharedPreferences.getBoolean("check5", false));
    }
}

