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

public class FollowPlanCariesLow extends AppCompatActivity {

    CheckBox checkBrush, checkFloss, checkSugar, checkWater, checkMouthwash;
    Button savePlanBtn;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.follow_plan_activity_caries_low);

        checkBrush = findViewById(R.id.checkBrush);
        checkFloss = findViewById(R.id.checkFloss);
        checkSugar = findViewById(R.id.checkSugar);
        checkWater = findViewById(R.id.checkWater);
        checkMouthwash = findViewById(R.id.checkMouthwash);
        savePlanBtn = findViewById(R.id.savePlanBtn);

        sharedPreferences = getSharedPreferences("FollowPlanLow", MODE_PRIVATE);
        loadProgress();

        savePlanBtn.setOnClickListener(v -> saveProgress());
    }

    private void saveProgress() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("brush", checkBrush.isChecked());
        editor.putBoolean("floss", checkFloss.isChecked());
        editor.putBoolean("sugar", checkSugar.isChecked());
        editor.putBoolean("water", checkWater.isChecked());
        editor.putBoolean("mouthwash", checkMouthwash.isChecked());
        editor.apply();

        int completed = 0;
        if (checkBrush.isChecked()) completed++;
        if (checkFloss.isChecked()) completed++;
        if (checkSugar.isChecked()) completed++;
        if (checkWater.isChecked()) completed++;
        if (checkMouthwash.isChecked()) completed++;

        int percentage = (completed * 100) / 5;

        // Save to central CalendarProgress
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        SharedPreferences calPrefs = getSharedPreferences("CalendarProgress", MODE_PRIVATE);
        calPrefs.edit().putInt(today, percentage).apply();

        Toast.makeText(this, "Progress saved! " + percentage + "% completed today.", Toast.LENGTH_SHORT).show();
    }

    private void loadProgress() {
        checkBrush.setChecked(sharedPreferences.getBoolean("brush", false));
        checkFloss.setChecked(sharedPreferences.getBoolean("floss", false));
        checkSugar.setChecked(sharedPreferences.getBoolean("sugar", false));
        checkWater.setChecked(sharedPreferences.getBoolean("water", false));
        checkMouthwash.setChecked(sharedPreferences.getBoolean("mouthwash", false));
    }
}
