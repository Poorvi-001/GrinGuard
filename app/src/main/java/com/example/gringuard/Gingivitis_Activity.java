package com.example.gringuard;

import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Gingivitis_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fractured_teeth_severity);


        RadioGroup rgVisual = findViewById(R.id.rgVisual);      // Bleeding
        RadioGroup rgColor = findViewById(R.id.rgCold);        // Color
        RadioGroup rgSensitivity = findViewById(R.id.rgBite);  // Sensitivity
        RadioGroup rgSwelling = findViewById(R.id.rgStability);// Swelling
        RadioGroup rgSpread = findViewById(R.id.rgSpontaneous);// Coverage
        RadioGroup rgBreath = findViewById(R.id.rgGums);       // Breath

        Button btnCalculate = findViewById(R.id.btnCalculate);
        TextView tvResult = findViewById(R.id.tvResult);

        btnCalculate.setOnClickListener(v -> {
            // Validate all selections
            if (rgVisual.getCheckedRadioButtonId() == -1 || rgColor.getCheckedRadioButtonId() == -1 ||
                    rgSensitivity.getCheckedRadioButtonId() == -1 || rgSwelling.getCheckedRadioButtonId() == -1 ||
                    rgSpread.getCheckedRadioButtonId() == -1 || rgBreath.getCheckedRadioButtonId() == -1) {
                Toast.makeText(this, "Please answer all 6 questions", Toast.LENGTH_SHORT).show();
                return;
            }

            int vScore = getScore(rgVisual);
            int cScore = getScore(rgColor);
            int sScore = getScore(rgSensitivity);
            int swScore = getScore(rgSwelling);
            int spScore = getScore(rgSpread);
            int bScore = getScore(rgBreath);

            // HIGHEST THRESHOLD LOGIC
            int maxSeverity = Math.max(vScore, Math.max(cScore, Math.max(sScore,
                    Math.max(swScore, Math.max(spScore, bScore)))));

            // Cumulative check for Medium Severity escalation
            int mediumCount = 0;
            int[] scores = {vScore, cScore, sScore, swScore, spScore, bScore};
            for (int s : scores) if (s == 2) mediumCount++;

            String resultText;
            int resultColor;

            // 1. Clinical Edge Case: Possible Periodontitis
            // If bleeding is spontaneous (high) but there is no pain (low), it might indicate deep chronic infection.
            if (vScore == 3 && sScore == 1) {
                resultText = "HIGH SEVERITY: Chronic Infection\nSpontaneous bleeding without pain can indicate advanced gum disease (Periodontitis). Seek a professional deep cleaning.";
                resultColor = 0xFFD81B60;
            }
            // 2. High Severity: Severe Gingivitis
            else if (maxSeverity == 3 || (maxSeverity == 2 && mediumCount >= 3)) {
                resultText = "HIGH SEVERITY: Severe Gingivitis\nSignificant inflammation and tissue distress. Requires professional dental intervention to prevent tooth loss.";
                resultColor = 0xFFD81B60;
            }
            // 3. Medium Severity: Moderate Gingivitis
            else if (maxSeverity == 2) {
                resultText = "MEDIUM SEVERITY: Moderate\nGums are infected. Improved hygiene is needed along with a professional cleaning to reverse the damage.";
                resultColor = 0xFFF4511E;
            }
            // 4. Low Severity: Mild Gingivitis
            else {
                resultText = "LOW SEVERITY: Mild\nEarly stage inflammation. Increase flossing and use an antiseptic mouthwash to reverse symptoms at home.";
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