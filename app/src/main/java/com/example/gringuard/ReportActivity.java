package com.example.gringuard;

import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReportActivity extends AppCompatActivity {

    TextView nameText, ageText, genderText, emailText;
    TextView conditionText, severityText, descriptionText, tipsText, dateText;
    Button exportBtn;
    ScrollView reportLayout;

    FirebaseAuth auth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        nameText = findViewById(R.id.nameText);
        ageText = findViewById(R.id.ageText);
        genderText = findViewById(R.id.genderText);
        emailText = findViewById(R.id.emailText);
        conditionText = findViewById(R.id.conditionText);
        severityText = findViewById(R.id.severityText);
        descriptionText = findViewById(R.id.descriptionText);
        tipsText = findViewById(R.id.tipsText);
        dateText = findViewById(R.id.dateText);

        exportBtn = findViewById(R.id.exportPdfBtn);
        reportLayout = findViewById(R.id.reportLayout);

        auth = FirebaseAuth.getInstance();

        String uid = auth.getCurrentUser().getUid();

        databaseReference = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(uid);

        fetchUserData();

        // receive disease and severity from previous activity
        String detectedDisease = getIntent().getStringExtra("disease");
        String detectedSeverity = getIntent().getStringExtra("severity");

        if (detectedDisease != null && detectedSeverity != null) {
            setDiseaseData(detectedDisease, detectedSeverity);
        }

        exportBtn.setOnClickListener(v -> exportPDF());
    }

    private void fetchUserData() {

        databaseReference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        String firstName = snapshot.child("firstName").getValue(String.class);
                        String age = snapshot.child("age").getValue(String.class);
                        String gender = snapshot.child("gender").getValue(String.class);
                        String email = snapshot.child("email").getValue(String.class);

                        nameText.setText("Name: " + firstName);
                        ageText.setText("Age: " + age);
                        genderText.setText("Gender: " + gender);
                        emailText.setText("Email: " + email);

                        dateText.setText("Date: " + getCurrentDate());
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                }
        );
    }

    // modified function to accept severity
    private void setDiseaseData(String disease, String severity) {

        severityText.setText("Severity: " + severity);

        if (disease.equals("caries")) {

            conditionText.setText("Detected Condition: Caries");

            descriptionText.setText(
                    "Caries are permanently damaged areas in the tooth surface " +
                            "caused by bacteria, plaque, and sugar buildup."
            );

            tipsText.setText(
                    "• Brush twice daily with fluoride toothpaste\n" +
                            "• Reduce sugary food intake\n" +
                            "• Use fluoride mouthwash\n" +
                            "• Visit a dentist for cavity treatment"
            );

        }

        else if (disease.equals("gingivitis")) {

            conditionText.setText("Detected Condition: Gingivitis");

            descriptionText.setText(
                    "Gingivitis is an early stage gum disease caused by plaque buildup " +
                            "around the gum line which leads to swollen and bleeding gums."
            );

            tipsText.setText(
                    "• Brush teeth twice daily\n" +
                            "• Floss regularly\n" +
                            "• Use antiseptic mouthwash\n" +
                            "• Maintain good oral hygiene"
            );
        }

        else if (disease.equals("fracture")) {

            conditionText.setText("Detected Condition: Fractured Tooth");

            descriptionText.setText(
                    "A fractured tooth occurs when a crack develops in the tooth " +
                            "due to trauma, biting hard foods, or accidents."
            );

            tipsText.setText(
                    "• Avoid chewing on the affected side\n" +
                            "• Rinse mouth with warm salt water\n" +
                            "• Avoid hard foods\n" +
                            "• Visit a dentist immediately"
            );
        }

        else if (disease.equals("calculus")) {

            conditionText.setText("Detected Condition: Dental Calculus");

            descriptionText.setText(
                    "Dental calculus (tartar) is hardened plaque that forms on teeth " +
                            "when plaque is not removed through regular brushing."
            );

            tipsText.setText(
                    "• Brush twice daily\n" +
                            "• Use tartar-control toothpaste\n" +
                            "• Floss regularly\n" +
                            "• Get professional dental cleaning"
            );
        }
    }

    private String getCurrentDate() {

        SimpleDateFormat sdf =
                new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

        return sdf.format(new Date());
    }

    private void exportPDF() {

        try {

            View view = reportLayout;

            view.measure(
                    View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY)
            );

            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

            PdfDocument document = new PdfDocument();

            PdfDocument.PageInfo pageInfo =
                    new PdfDocument.PageInfo.Builder(
                            view.getMeasuredWidth(),
                            view.getMeasuredHeight(),
                            1).create();

            PdfDocument.Page page = document.startPage(pageInfo);

            view.draw(page.getCanvas());

            document.finishPage(page);

            File file = new File(
                    Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS),
                    "GrinGuard_Report.pdf"
            );

            document.writeTo(new FileOutputStream(file));

            document.close();

            Toast.makeText(this,
                    "PDF saved in Downloads",
                    Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}