package com.example.gringuard;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EditActivity extends AppCompatActivity {

    private ImageView profileImg;
    private TextView changePhotoBtn, removePhotoBtn, logoutBtn;
    private EditText firstNameInput, lastNameInput, emailInput, ageInput;
    private CheckBox genderMale, genderFemale;
    private Button saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage1);

        // Initialize Views
        profileImg = findViewById(R.id.profileImg);
        changePhotoBtn = findViewById(R.id.changePhotoBtn);
        removePhotoBtn = findViewById(R.id.removePhotoBtn);
        logoutBtn = findViewById(R.id.logoutBtn);
        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        emailInput = findViewById(R.id.emailInput);
        ageInput = findViewById(R.id.ageInput);
        genderMale = findViewById(R.id.genderMale);
        genderFemale = findViewById(R.id.genderFemale);
        saveBtn = findViewById(R.id.saveBtn);

        // Checkbox logic for single selection
        genderMale.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) genderFemale.setChecked(false);
        });
        genderFemale.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) genderMale.setChecked(false);
        });

        // Save Button logic
        saveBtn.setOnClickListener(v -> {
            validateAndSave();
        });

        // Logout Button logic
        logoutBtn.setOnClickListener(v -> {
            // Add your logout logic here (e.g., clearing session, navigating to LoginActivity)
            Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();
            finish(); // Closes current activity
        });
    }

    private void validateAndSave() {
        String fName = firstNameInput.getText().toString();
        if (fName.isEmpty()) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Profile Saved Successfully", Toast.LENGTH_SHORT).show();
        }
    }
}