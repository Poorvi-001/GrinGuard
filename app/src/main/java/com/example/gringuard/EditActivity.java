package com.example.gringuard;



import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EditActivity extends AppCompatActivity {

    private boolean isEmailVerified = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        final ImageView profileImg = findViewById(R.id.profileImg);
        final TextView removePhoto = findViewById(R.id.removePhotoBtn);
        final TextView forgotPassword = findViewById(R.id.forgotPasswordBtn);
        final EditText emailInput = findViewById(R.id.emailInput);
        final Button verifyBtn = findViewById(R.id.verifyBtn);
        final TextView verifiedBadge = findViewById(R.id.verifiedBadge);
        final Button saveBtn = findViewById(R.id.saveBtn);

        // Remove Photo Logic
        removePhoto.setOnClickListener(v -> {
            profileImg.setImageResource(android.R.drawable.ic_menu_gallery);
            Toast.makeText(this, "Photo Removed", Toast.LENGTH_SHORT).show();
        });

        // Forgot Password Logic
        forgotPassword.setOnClickListener(v -> {
            Toast.makeText(this, "Redirecting to Reset Password...", Toast.LENGTH_SHORT).show();
        });

        // Email Verification Logic
        verifyBtn.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            if (email.contains("@")) {
                isEmailVerified = true;
                verifiedBadge.setVisibility(View.VISIBLE);
                verifyBtn.setVisibility(View.GONE);
            } else {
                Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            }
        });

        // Save Button Logic
        saveBtn.setOnClickListener(v -> {
            if (!isEmailVerified) {
                Toast.makeText(this, "Please verify email first", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Profile Saved Successfully!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
