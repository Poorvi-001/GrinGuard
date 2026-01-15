package com.example.gringuard;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText emailBox, passBox;
    private Button btnLogin, btnSignup;
    private TextView btnForgotPass;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        emailBox = findViewById(R.id.emailBox);
        passBox = findViewById(R.id.passBox);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);
        btnForgotPass = findViewById(R.id.btnForgotPass);

        // --- 1. LOGIN LOGIC ---
        btnLogin.setOnClickListener(v -> {
            String email = emailBox.getText().toString().trim();
            String password = passBox.getText().toString().trim();

            if (validateInputs(email, password)) {
                btnLogin.setEnabled(false);
                btnLogin.setText("Checking...");

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            btnLogin.setEnabled(true);
                            btnLogin.setText("SignIn");
                            if (task.isSuccessful()) {
                                startActivity(new Intent(LoginActivity.this, DashBoardActivity.class));
                                finish();
                            } else {
                                showPopup("Login Failed", task.getException().getMessage());
                            }
                        });
            }
        });

        // --- 2. SIGNUP LOGIC (Integrated) ---
        btnSignup.setOnClickListener(v -> {
            String email = emailBox.getText().toString().trim();
            String password = passBox.getText().toString().trim();

            if (validateInputs(email, password)) {
                if (password.length() < 6) {
                    passBox.setError("Password must be at least 6 characters");
                    return;
                }

                btnSignup.setEnabled(false);
                btnSignup.setText("Creating...");

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            btnSignup.setEnabled(true);
                            btnSignup.setText("SignUp");
                            if (task.isSuccessful()) {
                                // Success Popup
                                showPopup("Account Created", "Welcome to Gringuard! You can now sign in or go to your profile.");
                                // Automatically take them to Profile setup since it's a new user
                                startActivity(new Intent(LoginActivity.this, Profile.class));
                            } else {
                                showPopup("Signup Error", task.getException().getMessage());
                            }
                        });
            }
        });

        // --- 3. FORGOT PASSWORD (with Popup) ---
        btnForgotPass.setOnClickListener(v -> {
            String email = emailBox.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                showPopup("Forgot Password", "Please enter your email in the box first so we know where to send the link.");
            } else {
                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showPopup("Email Sent", "Check your inbox for the password reset link.");
                    } else {
                        showPopup("Error", task.getException().getMessage());
                    }
                });
            }
        });
    }

    // Helper to validate fields
    private boolean validateInputs(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            emailBox.setError("Email required");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            passBox.setError("Password required");
            return false;
        }
        return true;
    }

    // Custom Popup Function
    private void showPopup(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
}