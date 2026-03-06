package com.example.gringuard;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class FollowPlanCavityMedium extends AppCompatActivity {

    CheckBox checkPain, checkSensitive, checkToothpaste, checkMouthwash, checkDentist;
    Button savePlanBtn;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.follow_plan_cavity_medium);

        // Link UI
        checkPain = findViewById(R.id.checkPain);
        checkSensitive = findViewById(R.id.checkSensitive);
        checkToothpaste = findViewById(R.id.checkToothpaste);
        checkMouthwash = findViewById(R.id.checkMouthwash);
        checkDentist = findViewById(R.id.checkDentist);
        savePlanBtn = findViewById(R.id.savePlanBtn);

        // SharedPreferences initialization
        sharedPreferences = getSharedPreferences("FollowPlanMedium", MODE_PRIVATE);

        // Load saved states
        loadProgress();

        // Save button action
        savePlanBtn.setOnClickListener(v -> saveProgress());
    }

    private void saveProgress() {

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("pain", checkPain.isChecked());
        editor.putBoolean("sensitive", checkSensitive.isChecked());
        editor.putBoolean("toothpaste", checkToothpaste.isChecked());
        editor.putBoolean("mouthwash", checkMouthwash.isChecked());
        editor.putBoolean("dentist", checkDentist.isChecked());

        editor.apply();

        int completed = 0;

        if (checkPain.isChecked()) completed++;
        if (checkSensitive.isChecked()) completed++;
        if (checkToothpaste.isChecked()) completed++;
        if (checkMouthwash.isChecked()) completed++;
        if (checkDentist.isChecked()) completed++;

        Toast.makeText(
                this,
                "Progress saved! You completed " + completed + " out of 5 tasks today.",
                Toast.LENGTH_LONG
        ).show();
    }

    private void loadProgress() {

        checkPain.setChecked(sharedPreferences.getBoolean("pain", false));
        checkSensitive.setChecked(sharedPreferences.getBoolean("sensitive", false));
        checkToothpaste.setChecked(sharedPreferences.getBoolean("toothpaste", false));
        checkMouthwash.setChecked(sharedPreferences.getBoolean("mouthwash", false));
        checkDentist.setChecked(sharedPreferences.getBoolean("dentist", false));
    }
}