package com.example.gringuard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.imageview.ShapeableImageView;

public class EditProfileActivity extends AppCompatActivity {

    private ShapeableImageView profileImg;
    private EditText etFirstName, etLastName, etEmail;
    private Button btnChangePhoto, btnRemovePhoto, btnVerify, btnSave;
    private TextView tvVerifiedBadge;

    // Handles picking an image from the gallery
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
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
        setContentView(R.layout.activity_edit_profile);

        profileImg = findViewById(R.id.profileImg);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        btnChangePhoto = findViewById(R.id.btnChangePhoto);
        btnRemovePhoto = findViewById(R.id.btnRemovePhoto);
        btnVerify = findViewById(R.id.btnVerify);
        btnSave = findViewById(R.id.btnSave);
        tvVerifiedBadge = findViewById(R.id.tvVerifiedBadge);

        btnChangePhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        btnRemovePhoto.setOnClickListener(v -> profileImg.setImageResource(R.drawable.placeholder_user));

        btnVerify.setOnClickListener(v -> {
            if (etEmail.getText().toString().contains("@")) {
                tvVerifiedBadge.setVisibility(View.VISIBLE);
                btnVerify.setVisibility(View.GONE);
            }
        });

        btnSave.setOnClickListener(v -> Toast.makeText(this, "Profile Saved!", Toast.LENGTH_SHORT).show());
    }
}