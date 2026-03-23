package com.example.gringuard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText emailBox, passBox;
    private Button btnLogin;
    private TextView btnSignup, btnForgotPass;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        emailBox     = findViewById(R.id.emailBox);
        passBox      = findViewById(R.id.passBox);
        btnLogin     = findViewById(R.id.btnLogin);
        btnSignup    = findViewById(R.id.btnSignup);
        btnForgotPass = findViewById(R.id.btnForgotPass);

        btnLogin.setOnClickListener(v -> {
            String email    = emailBox.getText().toString().trim();
            String password = passBox.getText().toString().trim();

            if (validateInputs(email, password)) {
                btnLogin.setEnabled(false);
                btnLogin.setText("Checking...");

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            btnLogin.setEnabled(true);
                            btnLogin.setText("SignIn");
                            if (task.isSuccessful()) {
                                // ✅ Clear any leftover prefs from previous session
                                String newUid = mAuth.getCurrentUser().getUid();
                                getSharedPreferences("DentalData_" + newUid, MODE_PRIVATE).edit().clear().apply();
                                getSharedPreferences("SeverityPrefs_" + newUid, MODE_PRIVATE).edit().clear().apply();

                                startActivity(new Intent(LoginActivity.this, DashBoardActivity.class));
                                finish();
                            } else {
                                String error = task.getException() != null ?
                                        task.getException().getMessage() : "Unknown error";
                                showPopup("Login Failed", error);
                            }
                        });
            }
        });

        btnSignup.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, Profile.class)));

        btnForgotPass.setOnClickListener(v -> {
            String email = emailBox.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                showPopup("Forgot Password", "Please enter your email in the box first so we know where to send the link.");
            } else {
                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showPopup("Email Sent", "Check your inbox for the password reset link.");
                    } else {
                        String error = task.getException() != null ?
                                task.getException().getMessage() : "Unknown error";
                        showPopup("Error", error);
                    }
                });
            }
        });
    }

    private boolean validateInputs(String email, String password) {
        if (TextUtils.isEmpty(email))    { emailBox.setError("Email required"); return false; }
        if (TextUtils.isEmpty(password)) { passBox.setError("Password required"); return false; }
        return true;
    }

    private void showPopup(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title).setMessage(message)
                .setPositiveButton("OK", null).show();
    }
}