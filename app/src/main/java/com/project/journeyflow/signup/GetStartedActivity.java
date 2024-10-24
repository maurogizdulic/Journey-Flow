package com.project.journeyflow.signup;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.project.journeyflow.MainActivity;
import com.project.journeyflow.R;

public class GetStartedActivity extends AppCompatActivity {

    Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_get_started);/*
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        */

        startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(view -> {
            Intent i = new Intent(GetStartedActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        });
    }
}