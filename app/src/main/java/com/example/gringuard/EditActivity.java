package com.example.gringuard;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gringuard.R;

public class EditActivity extends AppCompatActivity {

    private EditText firstNameInput, lastNameInput, emailInput, ageInput;
    private RadioGroup genderGroup;
    private Button saveBtn;
    private TextView logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        // Initialize Views
        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        emailInput = findViewById(R.id.emailInput);
        ageInput = findViewById(R.id.ageInput);
        genderGroup = findViewById(R.id.genderGroup);
        saveBtn = findViewById(R.id.saveBtn);
        logoutBtn = findViewById(R.id.logoutBtn);

        // Save Button Logic
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfileData();
            }
        });

        // Logout Button Logic
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EditActivity.this, "Logging out...", Toast.LENGTH_SHORT).show();
                // Add your logout navigation logic here
                finish();
            }
        });
    }

    private void saveProfileData() {
        String firstName = firstNameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();

        // Basic Validation
        if (firstName.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please fill in required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get selected gender
        int selectedId = genderGroup.getCheckedRadioButtonId();
        String gender = "";
        if (selectedId != -1) {
            RadioButton selectedRadioButton = findViewById(selectedId);
            gender = selectedRadioButton.getText().toString();
        }

        // Logic to save data (e.g., to Firebase or SQLite)
        Toast.makeText(this, "Profile Updated Successfully!", Toast.LENGTH_SHORT).show();
    }
}