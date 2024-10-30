package com.project.journeyflow.signup;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.project.journeyflow.R;

public class SignUpActivity extends AppCompatActivity {

    private Button signUpButton;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        /*
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

         */

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);

        signUpButton = findViewById(R.id.buttonSignUp);
        signUpButton.setOnClickListener(view -> {
            if (editTextEmail.getText().toString().isEmpty()){
                editTextEmail.setError("Email is required!");
            }

            if (editTextPassword.getText().toString().isEmpty()){
                editTextPassword.setError("Password is required!");
            }

            if (editTextConfirmPassword.getText().toString().isEmpty()){
                editTextConfirmPassword.setError("Confirmed password is required!");
            }

            if (!(editTextEmail.getText().toString().isEmpty() || editTextPassword.getText().toString().isEmpty() || editTextConfirmPassword.getText().toString().isEmpty())){
                if (editTextPassword.getText() == editTextConfirmPassword.getText()){
                    editTextConfirmPassword.setError("Confirmed password isn't same as password!");
                    Toast.makeText(getApplicationContext(), "Confirmed password isn't same as password!", Toast.LENGTH_LONG).show();
                }
                else{

                    Intent intent = new Intent(SignUpActivity.this, PersonalInformationActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

        });
    }
}