package com.example.gringuard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DashBoardActivity extends AppCompatActivity {

    private Interpreter tflite;
    private List<String> labelList;
    private ActivityResultLauncher<String> getContent;
    private ImageLabeler labeler;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private boolean isProcessing = false;

    private static final String SEV_PREF_PREFIX = "SeverityPrefs_";
    private static final String KEY_START_TIME = "startTime";
    private static final String KEY_COMPLETION_SHOWN = "completionShown";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage1);

        labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);

        // Load Model in background
        executorService.execute(() -> {
            try {
                MappedByteBuffer modelFile = loadModelFile();
                List<String> labels = loadLabelList();
                runOnUiThread(() -> {
                    tflite = new Interpreter(modelFile);
                    labelList = labels;
                });
            } catch (Exception e) { e.printStackTrace(); }
        });

        // Click Listeners
        findViewById(R.id.profileClickArea).setOnClickListener(v -> startActivity(new Intent(this, EditActivity.class)));
        findViewById(R.id.menuAboutUs).setOnClickListener(v -> startActivity(new Intent(this, AboutUsActivity.class)));
        findViewById(R.id.exportReportCard).setOnClickListener(v -> startActivity(new Intent(this, ReportActivity.class)));
        findViewById(R.id.virtualAssistantCard).setOnClickListener(v -> startActivity(new Intent(this, ChatActivity.class)));
        findViewById(R.id.dentistCard).setOnClickListener(v -> startActivity(new Intent(this, DentistRecommendationActivity.class)));
        findViewById(R.id.fabHelp).setOnClickListener(v -> startActivity(new Intent(this, HelpSupportActivity.class)));

        // Health Tracker Logic
        findViewById(R.id.healthTrackerCard).setOnClickListener(v -> {
            if (isProcessing) return;
            if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            SharedPreferences dentalPrefs = getSharedPreferences("DentalData_" + uid, MODE_PRIVATE);
            String localSeverity = dentalPrefs.getString("severity", "");

            // Immediate check for High Severity via Local Prefs
            if ("high".equalsIgnoreCase(localSeverity)) {
                showHighSeverityPopup();
                return;
            }

            SharedPreferences sevPrefs = getSharedPreferences(SEV_PREF_PREFIX + uid, MODE_PRIVATE);
            long startTime = sevPrefs.getLong(KEY_START_TIME, 0);

            if (startTime != 0) {
                long currentTime = System.currentTimeMillis();
                int currentDay = (int) ((currentTime - startTime) / (24 * 60 * 60 * 1000)) + 1;
                if (currentDay >= 22) { showCompletionPopup(sevPrefs); return; }
            }

            // Sync/Verify with Firebase
            isProcessing = true;
            FirebaseDatabase.getInstance().getReference("Users").child(uid).child("CurrentTreatment")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot s) {
                            isProcessing = false;
                            if (s.exists()) {
                                String fbSeverity = s.child("severity").getValue(String.class);
                                if ("high".equalsIgnoreCase(fbSeverity)) {
                                    // Sync local pref if Firebase says high
                                    dentalPrefs.edit().putString("severity", "high").apply();
                                    showHighSeverityPopup();
                                } else {
                                    startActivity(new Intent(DashBoardActivity.this, HealthActivity.class));
                                }
                            } else {
                                Toast.makeText(DashBoardActivity.this, "Scan an image first!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override public void onCancelled(@NonNull DatabaseError e) { isProcessing = false; }
                    });
        });

        // Image Selection
        getContent = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                Toast.makeText(this, "Processing image...", Toast.LENGTH_SHORT).show();
                validateAndProcessImage(uri);
            }
        });

        findViewById(R.id.heroCard).setOnClickListener(v -> {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("CurrentTreatment")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot s) {
                            if (s.exists()) showResetWarningDialog();
                            else {
                                Toast.makeText(DashBoardActivity.this, "Opening the gallery...", Toast.LENGTH_SHORT).show();
                                getContent.launch("image/*");
                            }
                        }
                        @Override public void onCancelled(@NonNull DatabaseError e) {
                            Toast.makeText(DashBoardActivity.this, "Opening the gallery...", Toast.LENGTH_SHORT).show();
                            getContent.launch("image/*");
                        }
                    });
        });

        checkPlanCompletion();
    }

    private void validateAndProcessImage(Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            labeler.process(InputImage.fromBitmap(bitmap, 0))
                    .addOnSuccessListener(labels -> {
                        boolean valid = false;
                        for (ImageLabel l : labels) {
                            String t = l.getText().toLowerCase();
                            if ((t.contains("tooth") || t.contains("teeth") || t.contains("mouth") || t.contains("smile") || t.contains("face")) && l.getConfidence() > 0.3f) {
                                valid = true; break;
                            }
                        }
                        if (valid) runInferenceAndGoToResult(bitmap, uri);
                        else Toast.makeText(this, "Please upload a clear tooth image.", Toast.LENGTH_LONG).show();
                    })
                    .addOnFailureListener(e -> runInferenceAndGoToResult(bitmap, uri));
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void runInferenceAndGoToResult(Bitmap bitmap, Uri uri) {
        if (tflite == null) { Toast.makeText(this, "AI is loading, try again in a second.", Toast.LENGTH_SHORT).show(); return; }
        executorService.execute(() -> {
            try {
                Bitmap resized = Bitmap.createScaledBitmap(bitmap, 224, 224, true);
                ByteBuffer input = ByteBuffer.allocateDirect(224 * 224 * 3 * 4);
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
                int max = 0;
                for (int i = 1; i < output[0].length; i++) if (output[0][i] > output[0][max]) max = i;
                String disease = labelList.get(max);
                runOnUiThread(() -> {
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    getSharedPreferences("DentalData_" + uid, MODE_PRIVATE).edit().putString("detectedDisease", disease).apply();
                    startActivity(new Intent(this, ResultActivity.class).putExtra("disease", disease).putExtra("imageUri", uri.toString()));
                });
            } catch (Exception e) { e.printStackTrace(); }
        });
    }

    private void resetAllData() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        getSharedPreferences("DentalData_" + uid, MODE_PRIVATE).edit().clear().apply();
        getSharedPreferences(SEV_PREF_PREFIX + uid, MODE_PRIVATE).edit().clear().apply();
        getSharedPreferences("SeverityHistory_" + uid, MODE_PRIVATE).edit().clear().apply();
        getSharedPreferences("GringuardPrefs_" + uid, MODE_PRIVATE).edit().clear().apply();
        getSharedPreferences("CalendarProgress_" + uid, MODE_PRIVATE).edit().clear().apply();
        FirebaseDatabase.getInstance().getReference("Users").child(uid).child("FollowPlan").removeValue();
        FirebaseDatabase.getInstance().getReference("Users").child(uid).child("CurrentTreatment").removeValue();
        FirebaseDatabase.getInstance().getReference("Users").child(uid).child("DetectedDiseases").removeValue();
        FirebaseDatabase.getInstance().getReference("Users").child(uid).child("SeverityHistory").removeValue();
        Toast.makeText(this, "Data Reset. Timeline starts at Day 1.", Toast.LENGTH_SHORT).show();
    }

    private void checkPlanCompletion() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        SharedPreferences sevPrefs = getSharedPreferences(SEV_PREF_PREFIX + uid, MODE_PRIVATE);
        long start = sevPrefs.getLong(KEY_START_TIME, 0);
        if (start != 0 && !sevPrefs.getBoolean(KEY_COMPLETION_SHOWN, false)) {
            if (((System.currentTimeMillis() - start) / (24 * 60 * 60 * 1000)) + 1 >= 22) showCompletionPopup(sevPrefs);
        }
    }

    private void showCompletionPopup(SharedPreferences sp) {
        View v = getLayoutInflater().inflate(R.layout.popup_completion, null);
        AlertDialog d = new AlertDialog.Builder(this).setView(v).setCancelable(false).create();
        if (d.getWindow() != null) d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        v.findViewById(R.id.btnFinishCelebration).setOnClickListener(view -> {
            sp.edit().putBoolean(KEY_COMPLETION_SHOWN, true).apply();
            d.dismiss();
        });
        d.show();
    }

    private void showResetWarningDialog() {
        new AlertDialog.Builder(this).setTitle("Reset Data?").setMessage("New upload will clear existing plan data.")
                .setPositiveButton("Yes", (d, w) -> {
                    resetAllData();
                    Toast.makeText(this, "Opening the gallery...", Toast.LENGTH_SHORT).show();
                    getContent.launch("image/*");
                })
                .setNegativeButton("No", null).show();
    }

    private void showHighSeverityPopup() {
        View v = getLayoutInflater().inflate(R.layout.popup, null);
        AlertDialog d = new AlertDialog.Builder(this).setView(v).create();
        v.setOnClickListener(view -> d.dismiss());
        d.show();
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fd = getAssets().openFd("best_float16.tflite");
        return new FileInputStream(fd.getFileDescriptor()).getChannel().map(FileChannel.MapMode.READ_ONLY, fd.getStartOffset(), fd.getDeclaredLength());
    }

    private List<String> loadLabelList() throws IOException {
        List<String> list = new ArrayList<>();
        BufferedReader r = new BufferedReader(new InputStreamReader(getAssets().open("labels.txt")));
        String line;
        while ((line = r.readLine()) != null) list.add(line);
        return list;
    }

    @Override protected void onDestroy() { super.onDestroy(); executorService.shutdown(); if (tflite != null) tflite.close(); }
}
