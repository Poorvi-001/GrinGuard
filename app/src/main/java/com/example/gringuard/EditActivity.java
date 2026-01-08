package com.example.gringuard;
import com.example.gringuard.R;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

// IMPORTANT: If you still see red, make sure you have

// at the top, matching your package name.

public class EditActivity extends AppCompatActivity {

    private ImageView profileImg;
    private TextView changePhotoBtn, removePhotoBtn, logoutBtn;
    private EditText firstNameInput, lastNameInput, emailInput, ageInput;
    private RadioGroup genderGroup;
    private RadioButton genderMale, genderFemale;
    private Button saveBtn;

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    profileImg.setImageURI(selectedImageUri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage1);
        //All these IDs are defined in the XML I provided previously
        profileImg = findViewById(R.id.profileImg);
        changePhotoBtn = findViewById(R.id.changePhotoBtn);
        removePhotoBtn = findViewById(R.id.removePhotoBtn);
        //logoutBtn = findViewById(R.id.logoutBtn);
        //firstNameInput = findViewById(R.id.firstNameInput);
        //lastNameInput = findViewById(R.id.lastNameInput);
        //emailInput = findViewById(R.id.emailInput);
        //ageInput = findViewById(R.id.ageInput);
        //genderGroup = findViewById(R.id.genderGroup);
        //genderMale = findViewById(R.id.genderMale);
        //genderFemale = findViewById(R.id.genderFemale);
        //saveBtn = findViewById(R.id.saveBtn);

        changePhotoBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(intent);
        });

        logoutBtn.setOnClickListener(v -> {
            // This will work ONLY if you have an Activity named LoginActivity
            try {
                Intent intent = new Intent(EditActivity.this,
                        Class.forName("com.example.gringuard.LoginActivity"));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } catch (ClassNotFoundException e) {
                Toast.makeText(this, "LoginActivity not found!", Toast.LENGTH_SHORT).show();
            }
        });

        saveBtn.setOnClickListener(v -> Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show());
    }
}