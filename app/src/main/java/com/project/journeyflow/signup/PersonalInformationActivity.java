package com.project.journeyflow.signup;

import static android.app.PendingIntent.getActivity;
import static androidx.core.content.ContentProviderCompat.requireContext;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.project.journeyflow.MainActivity;
import com.project.journeyflow.R;
import com.project.journeyflow.database.User;

import java.lang.reflect.Parameter;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import io.realm.Realm;

public class PersonalInformationActivity extends AppCompatActivity {

    private String[] gender = { "Female", "Male" };
    private String[] weight = {"30", "30.5", "31", "31.5"};
    private String[] height = {"168.5", "169", "169.5", "170"};
    private Spinner genderSpinner;
    private Spinner weightSpinner;
    private Spinner heightSpinner;
    private Button pickDateBtn;
    private Button confirmBtn;
    private TextView selectedDateTV;
    private TextInputEditText textInputFirstName;
    private TextInputEditText textInputLastName;
    private TextInputEditText textInputUsername;
    private Realm realm;
    private boolean correctData = true;
    private boolean successfulAddToDB = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_personal_information);
        /*
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

         */
        Realm.init(this);  // Initialize Realm
        realm = Realm.getDefaultInstance();  // Get a Realm instance

        textInputFirstName = findViewById(R.id.firstName);
        textInputLastName = findViewById(R.id.lastName);
        textInputUsername = findViewById(R.id.username);

        // Gender spinner
        genderSpinner = findViewById(R.id.spinnerGender);

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, gender);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(arrayAdapter);
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Weight spinner
        weightSpinner = findViewById(R.id.spinnerWeight);
        ArrayAdapter arrayAdapterWeight = new ArrayAdapter(this, android.R.layout.simple_spinner_item, weight);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weightSpinner.setAdapter(arrayAdapterWeight);
        weightSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Height spinner
        heightSpinner = findViewById(R.id.spinnerHeight);

        ArrayAdapter arrayAdapterHeight = new ArrayAdapter(this, android.R.layout.simple_spinner_item, height);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        heightSpinner.setAdapter(arrayAdapterHeight);
        heightSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Date Picker
        pickDateBtn = findViewById(R.id.buttonPickDate);
        selectedDateTV = findViewById(R.id.textViewSelectedDate);

        // on below line we are adding click listener for our pick date button
        pickDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on below line we are getting
                // the instance of our calendar.
                final Calendar c = Calendar.getInstance();

                // on below line we are getting
                // our day, month and year.
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // on below line we are creating a variable for date picker dialog.
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        // on below line we are passing context.
                        PersonalInformationActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // on below line we are setting date to our text view.
                                selectedDateTV.setText(dayOfMonth + "." + (monthOfYear + 1) + "." + year);

                            }
                        },
                        // on below line we are passing year,
                        // month and day for selected date in our date picker.
                        year, month, day);
                // at last we are calling show to
                // display our date picker dialog.
                datePickerDialog.show();


            }
        });
;

        // Confirm button
        confirmBtn = findViewById(R.id.buttonConfirm);

        confirmBtn.setOnClickListener(view -> {

            if(textInputFirstName.getText().toString().isEmpty()){
                textInputFirstName.setError("First name is required!");
                correctData = false;
            }

            if(textInputLastName.getText().toString().isEmpty()){
                textInputLastName.setError("Last name is required!");
                correctData = false;
            }

            if(textInputUsername.getText().toString().isEmpty()){
                textInputUsername.setError("Username is required!");
                correctData = false;
            }

            if (textInputUsername.getText().toString().length() <= 4){
                textInputUsername.setError("Username must be min length 4!");
                correctData = false;
            }

            if (correctData){
                addDataToDB(textInputFirstName.getText().toString(), textInputLastName.getText().toString(), textInputUsername.getText().toString(), genderSpinner.getSelectedItem().toString(), weightSpinner.getSelectedItem().toString(), heightSpinner.getSelectedItem().toString());

                Toast.makeText(this, "Successfully add to database", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(PersonalInformationActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
        });



    }

    private void addDataToDB(String firstName, String lastName, String username, String gender, String weight, String height) {
        // on below line creating and initializing our data object class
        User userObject = new User();
        // on below line we are getting id for the course which we are storing.
        Number id = realm.where(User.class).max("id");
        // on below line we are
        // creating a variable for our id.
        long nextId;
        // validating if id is null or not.
        if (id == null) {
            // if id is null
            // we are passing it as 1.
            nextId = 1;
        } else {
            // if id is not null then
            // we are incrementing it by 1
            nextId = id.intValue() + 1;
        }

        realm.beginTransaction();
        User user = realm.createObject(User.class, nextId);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setGender(gender);
        user.setWeight(Double.parseDouble(weight));
        user.setHeight(Double.parseDouble(height));
        realm.commitTransaction();
        realm.close();
    }
}