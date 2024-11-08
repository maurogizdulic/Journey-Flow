package com.project.journeyflow.signup;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.project.journeyflow.R;

public class SignUpActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private String email, password, confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);

        Button signUpButton = findViewById(R.id.buttonSignUp);
        signUpButton.setOnClickListener(view -> {

            email = editTextEmail.getText().toString().trim();
            password = editTextPassword.getText().toString();
            confirmPassword = editTextConfirmPassword.getText().toString();

            InputCorrectness inputCorrectness = new InputCorrectness();
            boolean isCorrect = true;

            if (email.isEmpty()) {
                editTextEmail.setError("Email is required!");
                isCorrect = false;
            }

            if (password.isEmpty()) {
                editTextPassword.setError("Password is required!");
                isCorrect = false;
            }

            if (confirmPassword.isEmpty()) {
                editTextConfirmPassword.setError("Confirmed password is required!");
                isCorrect = false;
            }

            if (!(inputCorrectness.checkEmailCorrectness(email))){
                editTextEmail.setError("E-mail incorrect!");
                isCorrect = false;
            }

            if (!(inputCorrectness.checkPasswordCorrectness(password))){
                editTextPassword.setError("Password must contain at least one capital letter, number and symbol");
                editTextPassword.setText("");
                isCorrect = false;
            }

            if (inputCorrectness.checkPasswordAndConfirmPasswordCorrectness(password, confirmPassword) && isCorrect) {
                Intent intent = new Intent(SignUpActivity.this, PersonalInformationActivity.class);
                intent.putExtra("Email", email);
                intent.putExtra("Password", password);
                intent.putExtra("ConfirmPassword", confirmPassword);
                startActivity(intent);
                finish();
            }
            else {
                editTextPassword.setText("");
                editTextConfirmPassword.setText("");
            }
        });
    }
}