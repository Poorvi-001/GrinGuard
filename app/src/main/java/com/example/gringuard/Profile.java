package com.example.gringuard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Profile extends AppCompatActivity {

    EditText emailInput, passwordInput, firstNameInput, lastNameInput, ageInput;
    RadioGroup genderGroup;
    Button saveBtn;

    DatabaseReference dbRef;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        mAuth = FirebaseAuth.getInstance();

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

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) ||
                fName.isEmpty() || lName.isEmpty() ||
                age.isEmpty() || gender.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            passwordInput.setError("Password must be at least 6 characters");
            return;
        }

        saveBtn.setEnabled(false);
        saveBtn.setText("Creating Account...");

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String uid = mAuth.getCurrentUser().getUid();
                        dbRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);

                        User user = new User(fName, lName, age, gender, email);

                        dbRef.setValue(user).addOnSuccessListener(aVoid -> {
                            // START 21-DAY PROGRAM HERE (Registration Day = Day 1)
                            SharedPreferences sevPrefs = getSharedPreferences("SeverityPrefs", MODE_PRIVATE);
                            sevPrefs.edit().putLong("startTime", System.currentTimeMillis()).apply();

                            Toast.makeText(Profile.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Profile.this, DashBoardActivity.class));
                            finish();
                        }).addOnFailureListener(e -> {
                            saveBtn.setEnabled(true);
                            saveBtn.setText("Save");
                            Toast.makeText(Profile.this, "Database Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        saveBtn.setEnabled(true);
                        saveBtn.setText("Save");
                        String error = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                        Toast.makeText(Profile.this, "Signup Failed: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
