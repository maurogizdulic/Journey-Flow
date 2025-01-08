package com.project.journeyflow.signup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.project.journeyflow.MainActivity;
import com.project.journeyflow.R;
import com.project.journeyflow.database.User;
import com.project.journeyflow.query.LogInSignUpQuery;

public class LogInActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in);

        editTextEmail = findViewById(R.id.editTextLogInEmail);
        editTextPassword = findViewById(R.id.editTextLogInPassword);
        Button buttonLogIn = findViewById(R.id.buttonLogIn);

        TextView textViewSignUp = findViewById(R.id.textViewSignUp);

        textViewSignUp.setOnClickListener(view -> {
            Intent intent = new Intent(LogInActivity.this, SignUpActivity.class);
            startActivity(intent);
            finish();
        });

        buttonLogIn.setOnClickListener(view -> {
            checkExistenceOfAccount(editTextEmail.getText().toString(), editTextPassword.getText().toString(), editTextEmail, editTextPassword);
        });
    }

    private void checkExistenceOfAccount(String email, String password, EditText editTextEmail, EditText editTextPassword) {
        LogInSignUpQuery query = new LogInSignUpQuery(getApplication());
        User user = query.checkAccount(email, password);

        if(user != null) {
            SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isSignedUp", true); // Set this to true when signup is complete
            editor.putLong("id", user.getId());
            editor.apply();

            Toast.makeText(this, "Successful login!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LogInActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            Toast.makeText(getApplication(), "Incorrect email or password entered.", Toast.LENGTH_LONG).show();

            editTextEmail.setText("");
            editTextPassword.setText("");
        }
    }
}