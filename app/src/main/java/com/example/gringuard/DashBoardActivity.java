package com.example.gringuard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
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

        // Header Profile Icon
        View profileBtn = findViewById(R.id.profileClickArea);
        profileBtn.setOnClickListener(v -> startActivity(new Intent(DashBoardActivity.this, EditActivity.class)));

        // 3-line Menu Click
        ImageView menuAboutUs = findViewById(R.id.menuAboutUs);
        menuAboutUs.setOnClickListener(v -> startActivity(new Intent(DashBoardActivity.this, AboutUsActivity.class)));

        // Health Tracker Card
        CardView healthTrackerCard = findViewById(R.id.healthTrackerCard);
        healthTrackerCard.setOnClickListener(v -> startActivity(new Intent(DashBoardActivity.this, HealthActivity.class)));

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

        ActivityResultLauncher<String> getContent = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        // Instead of showing on dashboard, we process and go to ResultActivity
                        runInferenceAndGoToResult(uri);
                    }
                });

        heroCard.setOnClickListener(v -> getContent.launch("image/*"));
    }

    private void runInferenceAndGoToResult(Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
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
            SharedPreferences prefs = getSharedPreferences("DentalData", MODE_PRIVATE);
            prefs.edit().putString("detectedDisease", detectedDisease).apply();

            // Open ResultActivity with the data
            Intent intent = new Intent(DashBoardActivity.this, ResultActivity.class);
            intent.putExtra("disease", detectedDisease);
            intent.putExtra("imageUri", uri.toString());
            startActivity(intent);

        } catch (IOException e) { e.printStackTrace(); }
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
