
package com.example.gringaurd;

import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;



public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tvTitle = findViewById(R.id.tvTitle);

        // Apply Gradient to Title Text (Mimicking your CSS gradient)
        Shader textShader = new LinearGradient(0, 0, tvTitle.getPaint().measureText(tvTitle.getText().toString()), 0,
                new int[]{0xFFD94FA3, 0xFF7A5CFF},
                null, Shader.TileMode.CLAMP);
        tvTitle.getPaint().setShader(textShader);
    }
}