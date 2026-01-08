package com.example.gringuard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class EditActivity extends AppCompatActivity {

    private ImageView profileImg;
    private TextView changePhotoBtn, removePhotoBtn, logoutBtn;
    private EditText firstNameInput, lastNameInput, emailInput, ageInput;
    private RadioGroup genderGroup;
    private RadioButton genderMale, genderFemale;
    private Button saveBtn;

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    profileImg.setImageURI(imageUri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage1);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        profileImg = findViewById(R.id.profileImg);
        changePhotoBtn = findViewById(R.id.changePhotoBtn);
        removePhotoBtn = findViewById(R.id.removePhotoBtn);
        logoutBtn = findViewById(R.id.logoutBtn);
        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        emailInput = findViewById(R.id.emailInput);
        ageInput = findViewById(R.id.ageInput);

        // Linking Gender Group
        genderGroup = findViewById(R.id.genderGroup);
        genderMale = findViewById(R.id.genderMale);
        genderFemale = findViewById(R.id.genderFemale);

        saveBtn = findViewById(R.id.saveBtn);
    }

    private void setupClickListeners() {
        changePhotoBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(intent);
        });

        removePhotoBtn.setOnClickListener(v -> {
            profileImg.setImageResource(android.R.drawable.ic_menu_gallery);
            Toast.makeText(this, "Photo Removed", Toast.LENGTH_SHORT).show();
        });

        saveBtn.setOnClickListener(v -> {
            Toast.makeText(this, "Profile Saved", Toast.LENGTH_SHORT).show();
        });

        logoutBtn.setOnClickListener(v -> finish());
    }
}