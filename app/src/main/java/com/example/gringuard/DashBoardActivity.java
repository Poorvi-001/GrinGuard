package com.example.gringuard;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
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

    // TFLite variables
    private Interpreter tflite;
    private List<String> labelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage1);

        // 1. Initialize TFLite
        try {
            tflite = new Interpreter(loadModelFile());
            labelList = loadLabelList();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Model failed to load", Toast.LENGTH_SHORT).show();
        }

        // --- Your Existing UI Code ---
        findViewById(R.id.virtualAssistantCard).setOnClickListener(v -> startActivity(new Intent(this, ChatActivity.class)));
        findViewById(R.id.profileClickArea).setOnClickListener(v -> startActivity(new Intent(DashBoardActivity.this, EditActivity.class)));

        CardView heroCard = findViewById(R.id.heroCard);
        imagePreview = findViewById(R.id.imagePreview);
        previewCard = findViewById(R.id.previewCard);

        ActivityResultLauncher<String> getContent = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        previewCard.setVisibility(View.VISIBLE);
                        imagePreview.setImageURI(uri);
                        // Run inference
                        runInference(uri);
                    }
                });

        heroCard.setOnClickListener(v -> getContent.launch("image/*"));

        // ... (Keep your other existing button listeners here)
    }

    // --- Inference Logic ---

    private void runInference(Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            Bitmap resized = Bitmap.createScaledBitmap(bitmap, 224, 224, true);

            ByteBuffer inputBuffer = ByteBuffer.allocateDirect(1 * 224 * 224 * 3 * 4);
            inputBuffer.order(ByteOrder.nativeOrder());

            // Fill buffer with pixel data (standard RGB normalization)
            int[] intValues = new int[224 * 224];
            resized.getPixels(intValues, 0, 224, 0, 0, 224, 224);
            for (int pixelValue : intValues) {
                inputBuffer.putFloat(((pixelValue >> 16) & 0xFF) / 255.0f);
                inputBuffer.putFloat(((pixelValue >> 8) & 0xFF) / 255.0f);
                inputBuffer.putFloat((pixelValue & 0xFF) / 255.0f);
            }

            float[][] output = new float[1][labelList.size()];
            tflite.run(inputBuffer, output);

            // Find index of highest probability
            int maxIdx = 0;
            for (int i = 1; i < output[0].length; i++) {
                if (output[0][i] > output[0][maxIdx]) maxIdx = i;
            }

            Toast.makeText(this, "Result: " + labelList.get(maxIdx), Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- Helper Methods ---

    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = this.getAssets().openFd("best_float16.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, fileDescriptor.getStartOffset(), fileDescriptor.getDeclaredLength());
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