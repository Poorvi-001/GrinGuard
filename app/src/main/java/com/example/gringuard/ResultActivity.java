package com.example.gringuard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    ImageView resultImage;
    TextView resultText;
    Button severityBtn;

    String disease;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        resultImage = findViewById(R.id.resultImage);
        resultText = findViewById(R.id.resultText);
        severityBtn = findViewById(R.id.severityBtn);

        // Receive data from previous activity
        Intent intent = getIntent();

        disease = intent.getStringExtra("disease");
        String imageUri = intent.getStringExtra("imageUri");

        // Prevent null crash
        if (disease == null) {
            disease = "Unknown";
        }

        // Set disease result
        resultText.setText("Result: " + disease);

        // Show uploaded image
        if (imageUri != null) {
            Uri uri = Uri.parse(imageUri);
            resultImage.setImageURI(uri);
        }

        // Severity button logic
        severityBtn.setOnClickListener(v -> {

            Intent nextIntent = null;

            if (disease.equalsIgnoreCase("Caries") ||
                    disease.equalsIgnoreCase("decaycavity") ||
                    disease.equalsIgnoreCase("earlydecay")) {

                nextIntent = new Intent(ResultActivity.this, Caries_Activity.class);

            }
            else if (disease.equalsIgnoreCase("Gingivitis")) {

                nextIntent = new Intent(ResultActivity.this, Gingivitis_Activity.class);

            }
            else if (disease.equalsIgnoreCase("Fractured Teeth")) {

                nextIntent = new Intent(ResultActivity.this, Fractured_Teeth_Activity.class);
            }

            if (nextIntent != null) {
                startActivity(nextIntent);
            }

        });

    }
}