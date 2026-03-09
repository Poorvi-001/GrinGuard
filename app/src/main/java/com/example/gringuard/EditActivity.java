package com.example.gringuard;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.HashMap;
import java.util.Map;

public class EditActivity extends AppCompatActivity {

    private EditText firstNameInput, lastNameInput, ageInput;
    private RadioGroup genderGroup;
    private RadioButton genderMale, genderFemale;
    private Button saveBtn;
    private TextView logoutBtn;

    private DatabaseReference dbRef;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        ageInput = findViewById(R.id.ageInput);
        genderGroup = findViewById(R.id.genderGroup);
        genderMale = findViewById(R.id.genderMale);
        genderFemale = findViewById(R.id.genderFemale);
        saveBtn = findViewById(R.id.saveBtn);
        logoutBtn = findViewById(R.id.logoutBtn);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        String uid = currentUser.getUid();
        dbRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);

        // Load user data
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    User user = snapshot.getValue(User.class);

                    if (user != null) {
                        firstNameInput.setText(user.fName);
                        lastNameInput.setText(user.lName);
                        ageInput.setText(user.age);

                        if ("Male".equalsIgnoreCase(user.gender)) {
                            genderMale.setChecked(true);
                        }
                        else if ("Female".equalsIgnoreCase(user.gender)) {
                            genderFemale.setChecked(true);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("Firebase", error.getMessage());
            }
        });

        saveBtn.setOnClickListener(v -> {

            String fName = firstNameInput.getText().toString().trim();
            String lName = lastNameInput.getText().toString().trim();
            String age = ageInput.getText().toString().trim();

            int selectedId = genderGroup.getCheckedRadioButtonId();
            RadioButton rb = findViewById(selectedId);
            String gender = (rb != null) ? rb.getText().toString() : "";

            if (TextUtils.isEmpty(fName) || TextUtils.isEmpty(lName) ||
                    TextUtils.isEmpty(age) || TextUtils.isEmpty(gender)) {

                Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
                return;
            }

            saveBtn.setEnabled(false);
            saveBtn.setText("Saving...");

            Map<String, Object> updates = new HashMap<>();
            updates.put("firstName", fName);
            updates.put("lastName", lName);
            updates.put("age", age);
            updates.put("gender", gender);
            updates.put("email", currentUser.getEmail());

            dbRef.updateChildren(updates)
                    .addOnSuccessListener(aVoid -> {

                        saveBtn.setEnabled(true);
                        saveBtn.setText("Save Changes");

                        Toast.makeText(EditActivity.this,
                                "Profile Updated!", Toast.LENGTH_SHORT).show();

                        finish();
                    })
                    .addOnFailureListener(e -> {

                        saveBtn.setEnabled(true);
                        saveBtn.setText("Save Changes");

                        Toast.makeText(EditActivity.this,
                                "Update Failed: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    });
        });

        logoutBtn.setOnClickListener(v -> {

            FirebaseAuth.getInstance().signOut();

            startActivity(new Intent(EditActivity.this, LoginActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));

            finish();
        });
    }
}