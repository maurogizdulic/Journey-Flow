package com.project.journeyflow.signup;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.project.journeyflow.R;

public class GetStartedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_get_started);

        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(view -> {
            Intent i = new Intent(GetStartedActivity.this, SignUpActivity.class);
            startActivity(i);
            finish();
        });
    }
}