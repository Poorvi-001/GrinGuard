
package com.example.gringuard;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Profile extends AppCompatActivity {

    EditText firstNameInput, lastNameInput, ageInput;
    RadioGroup genderGroup;
    Button saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile); // XML file name

        // Initialize views
        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        ageInput = findViewById(R.id.ageInput);
        genderGroup = findViewById(R.id.genderGroup);
        saveBtn = findViewById(R.id.saveBtn);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String firstName = firstNameInput.getText().toString().trim();
                String lastName = lastNameInput.getText().toString().trim();
                String age = ageInput.getText().toString().trim();

                int selectedGenderId = genderGroup.getCheckedRadioButtonId();

                // Validation
                if (firstName.isEmpty()) {
                    firstNameInput.setError("Enter first name");
                    return;
                }

                if (lastName.isEmpty()) {
                    lastNameInput.setError("Enter last name");
                    return;
                }

                if (age.isEmpty()) {
                    ageInput.setError("Enter age");
                    return;
                }

                if (selectedGenderId == -1) {
                    Toast.makeText(Profile.this,
                            "Please select gender", Toast.LENGTH_SHORT).show();
                    return;
                }

                RadioButton selectedGender = findViewById(selectedGenderId);
                String gender = selectedGender.getText().toString();

                // Success message (later you can save to DB / SharedPreferences)
                Toast.makeText(
                        Profile.this,
                        "Profile Saved\n" +
                                firstName + " " + lastName + ", " +
                                age + ", " + gender,
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }
}
