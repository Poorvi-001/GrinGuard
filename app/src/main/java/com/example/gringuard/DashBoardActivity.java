package com.example.gringuard;

 // Make sure this matches your project package name



import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.view.View;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DashBoardActivity extends AppCompatActivity {

    private ImageView imagePreview;
    private CardView previewCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage1);

        // Open your Chatbot
        findViewById(R.id.virtualAssistantCard).setOnClickListener(v ->
                startActivity(new Intent(this, ChatActivity.class)));


        // 1. Link Header Profile Icon
        View profileBtn = findViewById(R.id.profileClickArea);
        profileBtn.setOnClickListener(v -> {
            Intent intent = new Intent(DashBoardActivity.this, EditActivity.class);
            startActivity(intent);
        });

        // 2. Link Upload Logic
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

        // 3-line Menu Click → Open About Us
        ImageView menuAboutUs = findViewById(R.id.menuAboutUs);

        menuAboutUs.setOnClickListener(v -> {
            Intent intent = new Intent(DashBoardActivity.this, AboutUsActivity.class);
            startActivity(intent);
        });


        // 4. Virtual Assistant Click → Open Chatbot
        CardView virtualAssistantCard = findViewById(R.id.virtualAssistantCard);

        virtualAssistantCard.setOnClickListener(v -> {
            Intent intent = new Intent(DashBoardActivity.this, ChatActivity.class);
            startActivity(intent);
        });

        ImageView menuHelp = findViewById(R.id.fabHelp);

        menuHelp.setOnClickListener(v -> {
            Intent intent = new Intent(DashBoardActivity.this, HelpSupportActivity.class);
            startActivity(intent);
        });


        heroCard.setOnClickListener(v -> getContent.launch("image/*"));
    }
}