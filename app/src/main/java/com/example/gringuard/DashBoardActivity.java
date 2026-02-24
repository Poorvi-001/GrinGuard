package com.example.gringuard;




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

        findViewById(R.id.virtualAssistantCard).setOnClickListener(v ->
                startActivity(new Intent(this, ChatActivity.class)));


        View profileBtn = findViewById(R.id.profileClickArea);
        profileBtn.setOnClickListener(v -> {
            Intent intent = new Intent(DashBoardActivity.this, EditActivity.class);
            startActivity(intent);
        });

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

        ImageView menuAboutUs = findViewById(R.id.menuAboutUs);

        menuAboutUs.setOnClickListener(v -> {
            Intent intent = new Intent(DashBoardActivity.this, AboutUsActivity.class);
            startActivity(intent);
        });


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

        CardView dentistCard = findViewById(R.id.dentistCard);

        dentistCard.setOnClickListener(v -> {
            Intent intent = new Intent(DashBoardActivity.this, DentistRecommendationActivity.class);
            startActivity(intent);
        });



        heroCard.setOnClickListener(v -> getContent.launch("image/*"));
    }
}