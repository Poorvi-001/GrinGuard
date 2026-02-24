package com.example.gringuard;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText emailBox, passBox;
    private Button btnLogin;
    private TextView btnForgotPass, txtSignupRedirect;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        emailBox = findViewById(R.id.emailBox);
        passBox = findViewById(R.id.passBox);
        btnLogin = findViewById(R.id.btnLogin);
        btnForgotPass = findViewById(R.id.btnForgotPass);
        txtSignupRedirect = findViewById(R.id.txtSignupRedirect);

        // ================= LOGIN LOGIC =================
        btnLogin.setOnClickListener(v -> {
            String email = emailBox.getText().toString().trim();
            String password = passBox.getText().toString().trim();

            if (validateInputs(email, password)) {
                btnLogin.setEnabled(false);
                btnLogin.setText("Checking...");

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            btnLogin.setEnabled(true);
                            btnLogin.setText("Sign In");

                            if (task.isSuccessful()) {
                                startActivity(new Intent(LoginActivity.this, DashBoardActivity.class));
                                finish();
                            } else {
                                showPopup("Login Failed", task.getException().getMessage());
                            }
                        });
            }
        });

        // ================= CLICKABLE SIGNUP TEXT =================
        String fullText = "Don't have an account? SignUp";
        SpannableString spannableString = new SpannableString(fullText);

        int startIndex = fullText.indexOf("SignUp");
        int endIndex = startIndex + "SignUp".length();

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {

                String email = emailBox.getText().toString().trim();
                String password = passBox.getText().toString().trim();

                if (validateInputs(email, password)) {

                    if (password.length() < 6) {
                        passBox.setError("Password must be at least 6 characters");
                        return;
                    }

                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    showPopup("Account Created",
                                            "Welcome to Gringuard! Your account has been created successfully.");
                                    startActivity(new Intent(LoginActivity.this, Profile.class));
                                } else {
                                    showPopup("Signup Error",
                                            task.getException().getMessage());
                                }
                            });
                }
            }
        };

        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        txtSignupRedirect.setText(spannableString);
        txtSignupRedirect.setMovementMethod(LinkMovementMethod.getInstance());
        txtSignupRedirect.setHighlightColor(Color.TRANSPARENT);

        // ================= FORGOT PASSWORD =================
        btnForgotPass.setOnClickListener(v -> {
            String email = emailBox.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                showPopup("Forgot Password",
                        "Please enter your email first.");
            } else {
                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                showPopup("Email Sent",
                                        "Check your inbox for reset link.");
                            } else {
                                showPopup("Error",
                                        task.getException().getMessage());
                            }
                        });
            }
        });
    }

    // ================= VALIDATION =================
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

    // ================= POPUP =================
    private void showPopup(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
}