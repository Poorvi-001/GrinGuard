package com.example.gringuard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class DashBoardActivity extends AppCompatActivity {

    private ImageView imagePreview;
    private CardView previewCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage1);

        // Header Profile Icon
        View profileBtn = findViewById(R.id.profileClickArea);
        profileBtn.setOnClickListener(v -> {
            Intent intent = new Intent(DashBoardActivity.this, EditActivity.class);
            startActivity(intent);
        });

        // 3-line Menu Click → Open About Us
        ImageView menuAboutUs = findViewById(R.id.menuAboutUs);
        menuAboutUs.setOnClickListener(v -> {
            Intent intent = new Intent(DashBoardActivity.this, AboutUsActivity.class);
            startActivity(intent);
        });

        // Health Tracker Card → Open HealthActivity
        CardView healthTrackerCard = findViewById(R.id.healthTrackerCard);
        healthTrackerCard.setOnClickListener(v -> {
            Intent intent = new Intent(DashBoardActivity.this, HealthActivity.class);
            startActivity(intent);
        });

        // Virtual Assistant Click → Open Chatbot
        CardView virtualAssistantCard = findViewById(R.id.virtualAssistantCard);
        virtualAssistantCard.setOnClickListener(v -> {
            Intent intent = new Intent(DashBoardActivity.this, ChatActivity.class);
            startActivity(intent);
        });

        // Recommended Dentist Card → Open DentistRecommendationActivity
        CardView dentistCard = findViewById(R.id.dentistCard);
        dentistCard.setOnClickListener(v -> {
            Intent intent = new Intent(DashBoardActivity.this, DentistRecommendationActivity.class);
            startActivity(intent);
        });

        // Help FAB
        ImageView menuHelp = findViewById(R.id.fabHelp);
        menuHelp.setOnClickListener(v -> {
            Intent intent = new Intent(DashBoardActivity.this, HelpSupportActivity.class);
            startActivity(intent);
        });

        // Image Upload Logic
        CardView heroCard = findViewById(R.id.heroCard);
        imagePreview = findViewById(R.id.imagePreview);
        previewCard = findViewById(R.id.previewCard);

        ActivityResultLauncher<String> getContent = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        previewCard.setVisibility(View.VISIBLE);
                        imagePreview.setImageURI(uri);
                    }
                });

        heroCard.setOnClickListener(v -> getContent.launch("image/*"));
    }
}
