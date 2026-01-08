package com.example.gringuard;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class EditActivity extends AppCompatActivity {

    // 1. Declare variables
    private ImageView profileImg;
    private TextView changePhotoBtn, removePhotoBtn, logoutBtn;
    private EditText firstNameInput, lastNameInput, emailInput, ageInput;
    private RadioGroup genderGroup;
    private RadioButton genderMale, genderFemale;
    private Button saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage1);

        // 2. Call the custom initialization method
        initViews();

        // 3. Setup listeners
        setupClickListeners();
    }

    private void initViews() {
        // We put all the linking here to keep onCreate clean
        profileImg = findViewById(R.id.profileImg);
        changePhotoBtn = findViewById(R.id.changePhotoBtn);
        removePhotoBtn = findViewById(R.id.removePhotoBtn);
        logoutBtn = findViewById(R.id.logoutBtn);
        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        emailInput = findViewById(R.id.emailInput);
        ageInput = findViewById(R.id.ageInput);
        genderGroup = findViewById(R.id.genderGroup);
        genderMale = findViewById(R.id.genderMale);
        genderFemale = findViewById(R.id.genderFemale);
        saveBtn = findViewById(R.id.saveBtn);
    }

    private void setupClickListeners() {
        saveBtn.setOnClickListener(v -> {
            Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
        });

        logoutBtn.setOnClickListener(v -> finish());
    }
}