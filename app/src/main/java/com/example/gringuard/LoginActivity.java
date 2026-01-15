package com.example.gringuard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
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

        btnLogin.setOnClickListener(v -> {
            String email = emailBox.getText().toString().trim();
            String password = passBox.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "SignIn Successful!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, DashBoardActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        btnSignup.setOnClickListener(v -> {
            String email = emailBox.getText().toString().trim();
            String password = passBox.getText().toString().trim();

            if (email.isEmpty() || password.length() < 6) {
                Toast.makeText(this, "Valid email and 6-char password required", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "SignUp Successful!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, DashBoardActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Signup Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        btnForgotPass.setOnClickListener(v -> {
            String email = emailBox.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(this, "Enter your email to receive a reset link", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Reset link sent to " + email, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }
}