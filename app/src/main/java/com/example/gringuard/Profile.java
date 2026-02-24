package com.example.gringuard;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Profile extends AppCompatActivity {

    EditText emailInput, passwordInput;
    EditText firstNameInput, lastNameInput, ageInput;
    RadioGroup genderGroup;
    Button saveBtn;

    FirebaseAuth mAuth;
    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize Views
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        ageInput = findViewById(R.id.ageInput);
        genderGroup = findViewById(R.id.genderGroup);
        saveBtn = findViewById(R.id.saveBtn);

        saveBtn.setOnClickListener(v -> registerAndSaveProfile());
    }

    private void registerAndSaveProfile() {

        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String fName = firstNameInput.getText().toString().trim();
        String lName = lastNameInput.getText().toString().trim();
        String age = ageInput.getText().toString().trim();

        int selectedId = genderGroup.getCheckedRadioButtonId();
        RadioButton rb = findViewById(selectedId);
        String gender = (rb != null) ? rb.getText().toString() : "";

        // ================= VALIDATION =================
        if (TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(password) ||
                TextUtils.isEmpty(fName) ||
                TextUtils.isEmpty(lName) ||
                TextUtils.isEmpty(age) ||
                TextUtils.isEmpty(gender)) {

            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            passwordInput.setError("Password must be at least 6 characters");
            return;
        }

        saveBtn.setEnabled(false);
        saveBtn.setText("Saving...");

        // ================= CREATE ACCOUNT =================
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        String uid = mAuth.getCurrentUser().getUid();
                        dbRef = FirebaseDatabase.getInstance()
                                .getReference("Users")
                                .child(uid);

                        User user = new User(fName, lName, age, gender);

                        dbRef.setValue(user)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(Profile.this,
                                            "Profile Saved Successfully!",
                                            Toast.LENGTH_SHORT).show();

                                    startActivity(new Intent(Profile.this,
                                            DashBoardActivity.class));
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    saveBtn.setEnabled(true);
                                    saveBtn.setText("Save");
                                    Toast.makeText(Profile.this,
                                            "Database Error: " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                });

                    } else {
                        saveBtn.setEnabled(true);
                        saveBtn.setText("Save");
                        Toast.makeText(Profile.this,
                                "Auth Error: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}