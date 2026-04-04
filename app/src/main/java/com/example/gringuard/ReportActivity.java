package com.example.gringuard;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReportActivity extends AppCompatActivity {

    private TextView dateText, nameText, ageText, genderText, emailText;
    private TextView conditionText, severityText, descriptionText, tipsText;
    private Button exportPdfBtn;
    private ScrollView reportLayout;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        // Initialize Views
        reportLayout = findViewById(R.id.reportLayout);
        dateText = findViewById(R.id.dateText);
        nameText = findViewById(R.id.nameText);
        ageText = findViewById(R.id.ageText);
        genderText = findViewById(R.id.genderText);
        emailText = findViewById(R.id.emailText);
        conditionText = findViewById(R.id.conditionText);
        severityText = findViewById(R.id.severityText);
        descriptionText = findViewById(R.id.descriptionText);
        tipsText = findViewById(R.id.tipsText);
        exportPdfBtn = findViewById(R.id.exportPdfBtn);

        // Set Current Date
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        if (dateText != null) {
            dateText.setText("Date: " + currentDate);
        }

        // 1. Fetch User Profile Info from Firebase Database
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (uid != null) {
            dbRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);
            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String fName = snapshot.child("fName").getValue(String.class);
                        String lName = snapshot.child("lName").getValue(String.class);
                        String age = snapshot.child("age").getValue(String.class);
                        String gender = snapshot.child("gender").getValue(String.class);
                        String email = snapshot.child("email").getValue(String.class);

                        if (nameText != null) nameText.setText("Name: " + (fName != null ? fName : "") + " " + (lName != null ? lName : ""));
                        if (ageText != null) ageText.setText("Age: " + (age != null ? age : "N/A"));
                        if (genderText != null) genderText.setText("Gender: " + (gender != null ? gender : "N/A"));
                        if (emailText != null) emailText.setText("Email: " + (email != null ? email : "N/A"));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("FirebaseReport", "Database error: " + error.getMessage());
                }
            });
        }

        // 2. Fetch Dental Analysis Results from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("DentalData_" + uid, MODE_PRIVATE);
        String disease = prefs.getString("detectedDisease", "No scan recorded");
        String severity = prefs.getString("severity", "N/A");

        if (conditionText != null) conditionText.setText("Detected Condition: " + disease);
        if (severityText != null) severityText.setText("Severity: " + (severity != null ? severity.toUpperCase() : "N/A"));

        generateReportContent(disease, severity);

        if (exportPdfBtn != null) {
            exportPdfBtn.setOnClickListener(v -> exportAsPdf());
        }
    }

    private void generateReportContent(String disease, String severity) {
        if (descriptionText == null || tipsText == null) return;

        if (disease != null && disease.equalsIgnoreCase("Gingivitis")) {
            descriptionText.setText("Gingivitis is an early form of gum disease caused by plaque buildup. It causes redness, swelling, and bleeding of the gums.");
            tipsText.setText("• Brush twice daily with a soft brush.\n• Floss daily to remove interdental plaque.\n• Use an antiseptic mouthwash.");
        } else if (disease != null && (disease.equalsIgnoreCase("Caries") || disease.equalsIgnoreCase("Cavity"))) {
            descriptionText.setText("Dental Caries (cavities) are permanently damaged areas in the hard surface of your teeth that develop into tiny openings or holes.");
            tipsText.setText("• Reduce sugary food and drinks.\n• Use fluoride toothpaste.\n• Drink more water.");
        } else if (disease != null && disease.equalsIgnoreCase("Calculus")) {
            descriptionText.setText("Calculus (tartar) is hardened plaque that has attached to the enamel of your teeth and below the gum line.");
            tipsText.setText("• Professional scaling is required for removal.\n• Use tartar-control toothpaste.");
        } else if (disease != null && disease.equalsIgnoreCase("Fractured")) {
            descriptionText.setText("A fractured tooth involves a break or crack in the enamel or structure of the tooth.");
            tipsText.setText("• Avoid chewing on the affected side.\n• See a dentist immediately.");
        } else {
            descriptionText.setText("No specific condition detected or healthy teeth observed.");
            tipsText.setText("Maintain regular checkups and good oral hygiene.");
        }
    }

    private void exportAsPdf() {
        // Hide the button so it doesn't appear in the PDF
        exportPdfBtn.setVisibility(View.GONE);

        PdfDocument document = new PdfDocument();
        // Get the view content dimensions
        View content = reportLayout.getChildAt(0);
        int width = content.getWidth();
        int height = content.getHeight();

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(width, height, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        content.draw(canvas);
        document.finishPage(page);

        // Save the document to the Downloads folder
        String fileName = "GrinGuard_Report_" + System.currentTimeMillis() + ".pdf";
        
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                values.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

                Uri uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                if (uri != null) {
                    OutputStream out = getContentResolver().openOutputStream(uri);
                    document.writeTo(out);
                    out.close();
                    Toast.makeText(this, "PDF saved to Downloads", Toast.LENGTH_LONG).show();
                }
            } else {

                java.io.File file = new java.io.File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
                document.writeTo(new java.io.FileOutputStream(file));
                Toast.makeText(this, "PDF saved to Downloads", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e("PDF_ERROR", "Error: " + e.getMessage());
            Toast.makeText(this, "Failed to save PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        document.close();
        // Show the button again
        exportPdfBtn.setVisibility(View.VISIBLE);
    }
}
