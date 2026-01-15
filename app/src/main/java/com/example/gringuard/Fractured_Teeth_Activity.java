package com.example.gringuard;

import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Fractured_Teeth_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fractured_teeth_severity);

        RadioGroup rgVisual = findViewById(R.id.rgVisual);
        RadioGroup rgCold = findViewById(R.id.rgCold);
        RadioGroup rgBite = findViewById(R.id.rgBite);
        RadioGroup rgStability = findViewById(R.id.rgStability);
        RadioGroup rgSpontaneous = findViewById(R.id.rgSpontaneous);
        RadioGroup rgGums = findViewById(R.id.rgGums);

        Button btnCalculate = findViewById(R.id.btnCalculate);
        TextView tvResult = findViewById(R.id.tvResult);

        btnCalculate.setOnClickListener(v -> {
            if (rgVisual.getCheckedRadioButtonId() == -1 || rgCold.getCheckedRadioButtonId() == -1 ||
                    rgBite.getCheckedRadioButtonId() == -1 || rgStability.getCheckedRadioButtonId() == -1 ||
                    rgSpontaneous.getCheckedRadioButtonId() == -1 || rgGums.getCheckedRadioButtonId() == -1) {
                Toast.makeText(this, "Please answer all 6 questions", Toast.LENGTH_SHORT).show();
                return;
            }

            int vScore = getScore(rgVisual);
            int cScore = getScore(rgCold);
            int bScore = getScore(rgBite);
            int stScore = getScore(rgStability);
            int spScore = getScore(rgSpontaneous);
            int gScore = getScore(rgGums);

            int maxSeverity = Math.max(vScore, Math.max(cScore, Math.max(bScore,
                    Math.max(stScore, Math.max(spScore, gScore)))));

            int mediumCount = 0;
            int[] scores = {vScore, cScore, bScore, stScore, spScore, gScore};
            for (int s : scores) if (s == 2) mediumCount++;

            String resultText;
            int resultColor;

            if (vScore >= 2 && cScore == 1) {
                resultText = "HIGH SEVERITY: Possible Nerve Death\nDeep damage with zero sensitivity often indicates necrotic pulp. Seek dental care.";
                resultColor = 0xFFD81B60;
            }
            else if (maxSeverity == 3 || (maxSeverity == 2 && mediumCount >= 3)) {
                resultText = "HIGH SEVERITY: Emergency\nIndicates pulp exposure or structural fracture. Seek immediate dental care.";
                resultColor = 0xFFD81B60;
            }
            else if (maxSeverity == 2) {
                resultText = "MEDIUM SEVERITY: Urgent\nDentin is likely exposed. Visit a dentist within 24 hours to prevent infection.";
                resultColor = 0xFFF4511E;
            }
            else {
                resultText = "LOW SEVERITY: Routine\nLikely a minor enamel chip. Schedule a follow-up visit soon.";
                resultColor = 0xFF2E7D32;
            }

            tvResult.setText(resultText);
            tvResult.setTextColor(resultColor);
        });
    }

    private int getScore(RadioGroup rg) {
        int id = rg.getCheckedRadioButtonId();
        String name = getResources().getResourceEntryName(id);
        if (name.endsWith("_low")) return 1;
        if (name.endsWith("_med")) return 2;
        if (name.endsWith("_high")) return 3;
        return 1;
    }
}