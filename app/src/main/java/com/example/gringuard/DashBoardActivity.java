package com.example.gringuard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.client.generativeai.type.GenerationConfig;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.lite.Interpreter;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class DashBoardActivity extends AppCompatActivity {

    private ImageView imagePreview;
    private CardView previewCard;

    // TFLite fields
    private Interpreter tflite;
    private List<String> labelList;

    private ActivityResultLauncher<String> getContent;

    // Gemini Validation
    private GenerativeModelFutures model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.homepage1);

        // --- MODEL INITIALIZATION ---
        try {
            tflite = new Interpreter(loadModelFile());
            labelList = loadLabelList();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Initialize Gemini for validation
        GenerationConfig.Builder configBuilder = new GenerationConfig.Builder();
        GenerationConfig config = configBuilder.build();
        
        GenerativeModel gm = new GenerativeModel(
                "gemini-3-flash-preview",
                "AIzaSyDT-KMwwpu5ieI8WubnzGHaZ7LBads2p1s",
                config
        );
        model = GenerativeModelFutures.from(gm);

        // Header Profile Icon
        View profileBtn = findViewById(R.id.profileClickArea);
        profileBtn.setOnClickListener(v -> startActivity(new Intent(DashBoardActivity.this, EditActivity.class)));

        // 3-line Menu Click
        ImageView menuAboutUs = findViewById(R.id.menuAboutUs);
        menuAboutUs.setOnClickListener(v -> startActivity(new Intent(DashBoardActivity.this, AboutUsActivity.class)));

        // Health Tracker Card
        CardView healthTrackerCard = findViewById(R.id.healthTrackerCard);

        healthTrackerCard.setOnClickListener(v -> {

            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference("Users")
                    .child(uid)
                    .child("CurrentTreatment");

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {

                    if (snapshot.exists()) {

                        String severity = snapshot.child("severity").getValue(String.class);

                        if ("high".equalsIgnoreCase(severity)) {
                            showHighSeverityPopup();
                        } else {
                            startActivity(new Intent(DashBoardActivity.this, HealthActivity.class));
                        }

                    } else {
                        Toast.makeText(DashBoardActivity.this, "No data found. Please scan first.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {}
            });
        });

        // Export Report Card
        CardView exportReportCard = findViewById(R.id.exportReportCard);
        exportReportCard.setOnClickListener(v -> startActivity(new Intent(DashBoardActivity.this, ReportActivity.class)));

        // Virtual Assistant Click
        CardView virtualAssistantCard = findViewById(R.id.virtualAssistantCard);
        virtualAssistantCard.setOnClickListener(v -> startActivity(new Intent(DashBoardActivity.this, ChatActivity.class)));

        // Recommended Dentist Card
        CardView dentistCard = findViewById(R.id.dentistCard);
        dentistCard.setOnClickListener(v -> startActivity(new Intent(DashBoardActivity.this, DentistRecommendationActivity.class)));

        // Help FAB
        ImageView menuHelp = findViewById(R.id.fabHelp);
        menuHelp.setOnClickListener(v -> startActivity(new Intent(DashBoardActivity.this, HelpSupportActivity.class)));

        // Image Upload Logic
        CardView heroCard = findViewById(R.id.heroCard);
        imagePreview = findViewById(R.id.imagePreview);
        previewCard = findViewById(R.id.previewCard);

        getContent = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        validateAndRunInference(uri);
                    }
                });

        heroCard.setOnClickListener(v -> {

            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference("Users")
                    .child(uid)
                    .child("CurrentTreatment");

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {

                    if (snapshot.exists()) {
                        showResetWarningDialog();
                    } else {
                        getContent.launch("image/*");
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    getContent.launch("image/*");
                }
            });
        });

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(uid)
                .child("CurrentTreatment");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    String uidLocal = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    getSharedPreferences("DentalData_" + uidLocal, MODE_PRIVATE).edit().clear().apply();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    private void showResetWarningDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Reset Data?")
                .setMessage("If you want to upload a new image, then your previous data will be lost.")
                .setPositiveButton("Yes", (dialog, which) -> {
                    resetAllData();
                    getContent.launch("image/*");
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void resetAllData() {
        // 1. Clear ALL local SharedPreferences
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        getSharedPreferences("DentalData_" + uid, MODE_PRIVATE).edit().clear().apply();
        getSharedPreferences("SeverityPrefs",    MODE_PRIVATE).edit().clear().apply();
        getSharedPreferences("SeverityHistory",  MODE_PRIVATE).edit().clear().apply();


        getSharedPreferences("GringuardPrefs_" + uid,    MODE_PRIVATE).edit().clear().apply();
        getSharedPreferences("CalendarProgress_" + uid,  MODE_PRIVATE).edit().clear().apply();

        // 2. Clear ALL relevant Firebase nodes (not just FollowPlan)
        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("Users").child(uid);

        userRef.child("FollowPlan").removeValue();
        userRef.child("CurrentTreatment").removeValue();
        userRef.child("DetectedDiseases").removeValue();
        userRef.child("SeverityHistory").removeValue();
        userRef.child("SeverityGraph").removeValue()
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this, "Previous data reset successful", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Reset failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void showHighSeverityPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        
        dialogView.setOnClickListener(v -> dialog.dismiss());
        
        dialog.show();
    }

    private void validateAndRunInference(Uri uri) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Validating image...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            
            String prompt = "Analyze the provided image and determine whether it contains teeth " +
                    "that are appropriate for dental disease detection. " +
                    "The image should clearly show teeth (e.g. an intraoral photo, dental X-ray, " +
                    "or a close-up of teeth). " +
                    "Respond ONLY with valid JSON in this exact format, nothing else:\n" +
                    "{\"result\": \"yes\"}\n" +
                    "or\n" +
                    "{\"result\": \"no\"}";

            Content content = new Content.Builder()
                    .addImage(bitmap)
                    .addText(prompt)
                    .build();

            ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

            Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                @Override
                public void onSuccess(GenerateContentResponse result) {
                    progressDialog.dismiss();
                    String text = result.getText();
                    if (text == null) {
                        showInvalidImageDialog();
                        return;
                    }
                    
                    // Sanitize potential markdown from response
                    text = text.replace("```json", "").replace("```", "").trim();
                    
                    try {
                        JSONObject json = new JSONObject(text);
                        String validationResult = json.getString("result");
                        if ("yes".equalsIgnoreCase(validationResult)) {
                            runInferenceAndGoToResult(uri, bitmap);
                        } else {
                            showInvalidImageDialog();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        // Fallback: search for "yes" in the text if JSON parsing fails
                        if (text.toLowerCase().contains("\"result\": \"yes\"") || text.toLowerCase().contains("\"result\":\"yes\"")) {
                            runInferenceAndGoToResult(uri, bitmap);
                        } else {
                            showInvalidImageDialog();
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    progressDialog.dismiss();
                    Log.e("GeminiValidation", "Error: " + t.getMessage(), t);
                    Toast.makeText(DashBoardActivity.this, "Error please try again", Toast.LENGTH_SHORT).show();
                }
            }, ContextCompat.getMainExecutor(this));

        } catch (IOException e) {
            progressDialog.dismiss();
            e.printStackTrace();
        }
    }

    private void showInvalidImageDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Invalid Image")
                .setMessage("The uploaded image does not appear to contain teeth suitable for dental disease detection. Please try again with a clear photo of your teeth.")
                .setPositiveButton("Try Again", (dialog, which) -> getContent.launch("image/*"))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void runInferenceAndGoToResult(Uri uri, Bitmap bitmap) {
        try {
            Bitmap resized = Bitmap.createScaledBitmap(bitmap, 224, 224, true);

            ByteBuffer input = ByteBuffer.allocateDirect(1 * 224 * 224 * 3 * 4);
            input.order(ByteOrder.nativeOrder());
            for (int y = 0; y < 224; y++) {
                for (int x = 0; x < 224; x++) {
                    int px = resized.getPixel(x, y);
                    input.putFloat(((px >> 16) & 0xFF) / 255.0f);
                    input.putFloat(((px >> 8) & 0xFF) / 255.0f);
                    input.putFloat((px & 0xFF) / 255.0f);
                }
            }

            float[][] output = new float[1][labelList.size()];
            tflite.run(input, output);

            int maxIdx = 0;
            for (int i = 1; i < output[0].length; i++) if (output[0][i] > output[0][maxIdx]) maxIdx = i;

            String detectedDisease = labelList.get(maxIdx);

            // Save for Health Tracker tips
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            SharedPreferences prefs = getSharedPreferences("DentalData_" + uid, MODE_PRIVATE);
            prefs.edit().putString("detectedDisease", detectedDisease).apply();

            // Open ResultActivity with the data
            Intent intent = new Intent(DashBoardActivity.this, ResultActivity.class);
            intent.putExtra("disease", detectedDisease);
            intent.putExtra("imageUri", uri.toString());
            startActivity(intent);

        } catch (Exception e) { e.printStackTrace(); }
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fd = this.getAssets().openFd("best_float16.tflite");
        FileInputStream is = new FileInputStream(fd.getFileDescriptor());
        return is.getChannel().map(FileChannel.MapMode.READ_ONLY, fd.getStartOffset(), fd.getDeclaredLength());
    }

    private List<String> loadLabelList() throws IOException {
        List<String> labels = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(this.getAssets().open("labels.txt")));
        String line;
        while ((line = reader.readLine()) != null) labels.add(line);
        reader.close();
        return labels;
    }
}
