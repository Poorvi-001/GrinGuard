package com.example.gringuard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Profile extends AppCompatActivity {
    EditText firstNameInput, lastNameInput, ageInput;
    RadioGroup genderGroup;
    Button saveBtn;
    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        ageInput = findViewById(R.id.ageInput);
        genderGroup = findViewById(R.id.genderGroup);
        saveBtn = findViewById(R.id.saveBtn);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            // Using the dynamic instance to avoid URL typos
            // USE THIS VERSION TO FORCE THE CONNECTION
            // Firebase will look inside your new JSON file to find the URL automatically
            dbRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        }

        saveBtn.setOnClickListener(v -> saveProfile());
    }

    private void saveProfile() {
        String fName = firstNameInput.getText().toString().trim();
        String lName = lastNameInput.getText().toString().trim();
        String age = ageInput.getText().toString().trim();

        int selectedId = genderGroup.getCheckedRadioButtonId();
        RadioButton rb = findViewById(selectedId);
        String gender = (rb != null) ? rb.getText().toString() : "";

        if (fName.isEmpty() || lName.isEmpty() || age.isEmpty() || gender.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = new User(fName, lName, age, gender);
        saveBtn.setEnabled(false); // Prevent multiple clicks

        dbRef.setValue(user).addOnSuccessListener(aVoid -> {
            Toast.makeText(Profile.this, "Profile Saved Successfully!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Profile.this, DashBoardActivity.class));
            finish();
        }).addOnFailureListener(e -> {
            saveBtn.setEnabled(true);
            Toast.makeText(Profile.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}