package com.example.gringuard;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FollowPlanGingivitisLow extends AppCompatActivity {

    CheckBox checkHardFood, checkBrush, checkRinse, checkColdFood, checkMonitor;
    Button savePlanBtn;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.follow_plan_gingivitis_low);

        checkHardFood = findViewById(R.id.checkHardFood);
        checkBrush = findViewById(R.id.checkBrush);
        checkRinse = findViewById(R.id.checkRinse);
        checkColdFood = findViewById(R.id.checkColdFood);
        checkMonitor = findViewById(R.id.checkMonitor);

        savePlanBtn = findViewById(R.id.savePlanBtn);

        sharedPreferences = getSharedPreferences("GingivitisLowPlan", MODE_PRIVATE);

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

        int completed = 0;

        if (checkHardFood.isChecked()) completed++;
        if (checkBrush.isChecked()) completed++;
        if (checkRinse.isChecked()) completed++;
        if (checkColdFood.isChecked()) completed++;
        if (checkMonitor.isChecked()) completed++;

        int percentage = (completed * 100) / 5;

        // Save to central CalendarProgress
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        SharedPreferences calPrefs = getSharedPreferences("CalendarProgress", MODE_PRIVATE);
        calPrefs.edit().putInt(today, percentage).apply();

        Toast.makeText(this, "Progress saved! " + percentage + "% completed today.", Toast.LENGTH_SHORT).show();
    }

    private void loadProgress() {

        checkHardFood.setChecked(sharedPreferences.getBoolean("check1", false));
        checkBrush.setChecked(sharedPreferences.getBoolean("check2", false));
        checkRinse.setChecked(sharedPreferences.getBoolean("check3", false));
        checkColdFood.setChecked(sharedPreferences.getBoolean("check4", false));
        checkMonitor.setChecked(sharedPreferences.getBoolean("check5", false));
    }
}
