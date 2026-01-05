package com.example.gringuard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gringuard.R;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText emailBox, passBox;
    private Button btnLogin, btnSignup;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 1. Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // 2. Link Java variables to XML IDs
        emailBox = findViewById(R.id.emailBox);
        passBox = findViewById(R.id.passBox);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);

        // 3. Login Button Logic
        btnLogin.setOnClickListener(v -> {
            String username = emailBox.getText().toString().trim(); // This is your username
            String password = passBox.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
                return;
            }

            // Firebase needs an email format, so we turn "Apoorva" into "Apoorva@gringuard.com"
            String fakeEmail = username + "@gringuard.com";

            mAuth.signInWithEmailAndPassword(fakeEmail, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Success! Hello " + username, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, DashBoardActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        // 4. Signup Button Logic (Redirects to a Signup page or handles it here)
        btnSignup.setOnClickListener(v -> {
            String username = emailBox.getText().toString().trim();
            String password = passBox.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter username and password to sign up", Toast.LENGTH_SHORT).show();
                return;
            }

            // Convert username to the "fake" email format for Firebase
            String fakeEmail = username + "@gringuard.com";

            // Create the user in Firebase
            mAuth.createUserWithEmailAndPassword(fakeEmail, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // If account creation is successful
                            Toast.makeText(LoginActivity.this, "Sign up successful! You can now Log In.", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(LoginActivity.this, DashBoardActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If there's an error (e.g., user already exists, or password too short)
                            Toast.makeText(LoginActivity.this, "Sign up failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }
}