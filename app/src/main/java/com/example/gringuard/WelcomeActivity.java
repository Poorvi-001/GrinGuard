package com.example.gringuard;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This line links the Java code to the XML layout
        setContentView(R.layout.activity_main);
    }
}