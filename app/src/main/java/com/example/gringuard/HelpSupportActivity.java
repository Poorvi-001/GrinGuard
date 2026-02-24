package com.example.gringuard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class HelpSupportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_support);

        TextView tvPhone1 = findViewById(R.id.tvPhone1);
        TextView tvPhone2 = findViewById(R.id.tvPhone2);
        TextView tvEmail1 = findViewById(R.id.tvEmail1);
        TextView tvEmail2 = findViewById(R.id.tvEmail2);

        // Click Logic for Phone 1
        tvPhone1.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:+918937061646"));
            startActivity(intent);
        });

        // Click Logic for Phone 2
        tvPhone2.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:+918077560107"));
            startActivity(intent);
        });

        // Click Logic for Email 1
        tvEmail1.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:singhapoorvahere@gmail.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Dental Inquiry");
            startActivity(intent);
        });

        // Click Logic for Email 2
        tvEmail2.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:mittalparidhi48@gmail.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Dental Inquiry");
            startActivity(intent);
        });
    }
}