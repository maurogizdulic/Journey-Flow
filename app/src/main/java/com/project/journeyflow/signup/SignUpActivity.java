package com.project.journeyflow.signup;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.project.journeyflow.R;
import com.project.journeyflow.database.User;
import com.project.journeyflow.query.LogInSignUpQuery;

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

        TextView textViewLogIn = findViewById(R.id.textViewLogIn);
        onClickLogIn(textViewLogIn);

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

                LogInSignUpQuery query = new LogInSignUpQuery(getApplicationContext());
                User user = query.checkEmail(email);

                if(user == null) {
                    Intent intent = new Intent(SignUpActivity.this, PersonalInformationActivity.class);
                    intent.putExtra("Email", email);
                    intent.putExtra("Password", password);
                    intent.putExtra("ConfirmPassword", confirmPassword);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(getApplicationContext(), "An account with the specified email address already exists. Please change the email address.", Toast.LENGTH_LONG).show();
                    editTextEmail.setText("");
                    editTextPassword.setText("");
                    editTextConfirmPassword.setText("");
                }
            }
            else {
                editTextPassword.setText("");
                editTextConfirmPassword.setText("");
            }
        });
    }

    private void onClickLogIn(TextView textViewLogIn) {

        textViewLogIn.setOnClickListener(view -> {
            Intent intent = new Intent(SignUpActivity.this, LogInActivity.class);
            startActivity(intent);
            finish();
        });
    }
}