package com.example.gringuard;

import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CalculusActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calculus);

        RadioGroup rgVisual = findViewById(R.id.rgVisual);
        RadioGroup rgColor = findViewById(R.id.rgColor);
        RadioGroup rgSensitivity = findViewById(R.id.rgSensitivity);
        RadioGroup rgSwelling = findViewById(R.id.rgSwelling);
        RadioGroup rgCoverage = findViewById(R.id.rgCoverage);
        RadioGroup rgBreath = findViewById(R.id.rgBreath);

        Button btnCalculate = findViewById(R.id.btnCalculate);
        TextView tvResult = findViewById(R.id.tvResult);

        btnCalculate.setOnClickListener(v -> {
            if (rgVisual.getCheckedRadioButtonId() == -1 || rgColor.getCheckedRadioButtonId() == -1 ||
                    rgSensitivity.getCheckedRadioButtonId() == -1 || rgSwelling.getCheckedRadioButtonId() == -1 ||
                    rgCoverage.getCheckedRadioButtonId() == -1 || rgBreath.getCheckedRadioButtonId() == -1) {
                Toast.makeText(this, "Please answer all 6 questions", Toast.LENGTH_SHORT).show();
                return;
            }

            int vScore = getScore(rgVisual);
            int cScore = getScore(rgColor);
            int sScore = getScore(rgSensitivity);
            int swScore = getScore(rgSwelling);
            int coScore = getScore(rgCoverage);
            int bScore = getScore(rgBreath);

            int maxSeverity = Math.max(vScore, Math.max(cScore, Math.max(sScore,
                    Math.max(swScore, Math.max(coScore, bScore)))));


            int mediumCount = 0;
            int[] scores = {vScore, cScore, sScore, swScore, coScore, bScore};
            for (int s : scores) if (s == 2) mediumCount++;

            String resultText;
            int resultColor;

            if (vScore == 3 && sScore == 1) {
                resultText = "HIGH SEVERITY: Chronic Infection\nSpontaneous bleeding without pain can indicate advanced gum disease (Periodontitis). Seek a professional deep cleaning.";
                resultColor = 0xFFD81B60;
            }
            else if (maxSeverity == 3 || (maxSeverity == 2 && mediumCount >= 3)) {
                resultText = "HIGH SEVERITY: Severe Gingivitis\nSignificant inflammation and tissue distress. Requires professional dental intervention to prevent tooth loss.";
                resultColor = 0xFFD81B60;
            }
            else if (maxSeverity == 2) {
                resultText = "MEDIUM SEVERITY: Moderate\nGums are infected. Improved hygiene is needed along with a professional cleaning to reverse the damage.";
                resultColor = 0xFFF4511E;
            }
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
