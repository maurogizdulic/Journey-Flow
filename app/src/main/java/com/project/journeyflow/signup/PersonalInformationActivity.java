package com.project.journeyflow.signup;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.journeyflow.MainActivity;
import com.project.journeyflow.R;
import com.project.journeyflow.database.User;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.realm.Realm;

public class PersonalInformationActivity extends AppCompatActivity {

    // initializing variables
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 101;

    // Declaring variables
    private Spinner weightSpinner, heightSpinner;
    private TextView textViewSelectedDate;
    private TextInputEditText textInputFirstName, textInputLastName, textInputUsername;
    private Realm realm;
    private ImageButton profileImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_personal_information);

        // Realm database
        Realm.init(this);  // Initialize Realm
        realm = Realm.getDefaultInstance();  // Get a Realm instance

        // Personal information input
        textInputFirstName = findViewById(R.id.firstName);
        textInputLastName = findViewById(R.id.lastName);
        textInputUsername = findViewById(R.id.username);
        RadioGroup radioGroup = findViewById(R.id.radioGroupGender);

        // Sign up values from activity sign up
        String email = getIntent().getStringExtra("Email");
        String password = getIntent().getStringExtra("Password");
        String confirmPassword = getIntent().getStringExtra("ConfirmPassword");

        Log.d("EMAIL", email);
        Log.d("PASSWORD", password);
        Log.d("CONFIRMPASSWORD", confirmPassword);

        // Button for setting profile picture
        profileImageButton = findViewById(R.id.imageButtonProfilePicture);

        profileImageButton.setOnClickListener(view -> {
            if (checkStoragePermission()) {
                openImageChooser();
            } else {
                requestStoragePermission();
            }
        });

        // Weight spinner
        List<String> listWeight = new ArrayList<>();
        for (int i = 1; i <= 200; i++) {
            listWeight.add(i + " kg");
        }

        weightSpinner = findViewById(R.id.spinnerWeight);

        ArrayAdapter<String> arrayAdapterWeight = new ArrayAdapter<>(this, R.layout.drop_down_spinner, listWeight);
        arrayAdapterWeight.setDropDownViewResource(R.layout.drop_down_spinner);
        weightSpinner.setAdapter(arrayAdapterWeight);
        weightSpinner.setSelection(64);
        weightSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Height spinner
        List<String> listHeight = new ArrayList<>();
        for (int i = 1; i <= 230; i++) {
            listHeight.add(i + " cm");
        }

        heightSpinner = findViewById(R.id.spinnerHeight);

        ArrayAdapter arrayAdapterHeight = new ArrayAdapter(this, R.layout.drop_down_spinner, listHeight);
        arrayAdapterHeight.setDropDownViewResource(R.layout.drop_down_spinner);
        heightSpinner.setAdapter(arrayAdapterHeight);
        heightSpinner.setSelection(159);
        heightSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Date Picker
        ImageButton pickDateBtn = findViewById(R.id.imageButtonPickDate);
        textViewSelectedDate = findViewById(R.id.textViewSelectedDate);

        pickDateBtn.setOnClickListener(v -> {
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
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            // on below line we are setting date to our text view.
                            textViewSelectedDate.setText(dayOfMonth + "." + (monthOfYear + 1) + "." + year + ".");
                        }
                    },
                    // on below line we are passing year,
                    // month and day for selected date in our date picker.
                    year, month, day);
            // at last we are calling show to
            // display our date picker dialog.
            datePickerDialog.show();
        });

        // Confirm button
        Button confirmBtn = findViewById(R.id.buttonConfirm);

        confirmBtn.setOnClickListener(view -> {
            boolean correctData = true;
            String selectedGender = "";

            int selectedId = radioGroup.getCheckedRadioButtonId();
            if (selectedId != -1) {
                // Find the radio button by ID
                RadioButton selectedRadioButton = findViewById(selectedId);

                // Get the text of the selected radio button
                selectedGender = selectedRadioButton.getText().toString();
            } else {
                // No radio button is selected
                correctData = false;
                Toast.makeText(getApplicationContext(), "No option selected for gender", Toast.LENGTH_SHORT).show();
            }

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

            if(textInputFirstName.getText().toString().length() <= 1){
                textInputFirstName.setError("First name must be min length 2!");
                correctData = false;
            }

            if(textInputFirstName.getText().toString().length() <= 1){
                textInputLastName.setError("Last name must be min length 2!");
                correctData = false;
            }

            if (textInputUsername.getText().toString().length() <= 4){
                textInputUsername.setError("Username must be min length 5!");
                correctData = false;
            }

            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            Date birthDate = null;
            try {
                Log.d("DATE", textViewSelectedDate.toString());
                birthDate = dateFormat.parse(textViewSelectedDate.getText().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            boolean isEighteenOrOlder = false;
            if (birthDate != null) {
                Calendar birthCalendar = Calendar.getInstance();
                birthCalendar.setTime(birthDate);

                Calendar today = Calendar.getInstance();
                today.add(Calendar.YEAR, -18); // Subtract 18 years from today's date

                isEighteenOrOlder = !birthCalendar.after(today); // Check if birth date is 18 years or more
            }

            if (!isEighteenOrOlder){
                Toast.makeText(this, "Must be older than 18 years!", Toast.LENGTH_SHORT).show();
                correctData = false;
            }

            if (correctData){

                if (!isInternetAvailable(this)) {
                    dialogInternetConnection(this, "No Internet Connection", "Journey is public. You need to turn on the internet to update the journey. Please connect to the internet to continue.");
                }
                else
                {
                    long userID = addDataToDB(textInputFirstName.getText().toString(), textInputLastName.getText().toString(), textInputUsername.getText().toString(), selectedGender, weightSpinner.getSelectedItem().toString(), heightSpinner.getSelectedItem().toString(), birthDate, email, password);

                    SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isSignedUp", true); // Set this to true when signup is complete
                    editor.putLong("id", userID);
                    editor.apply();

                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    Map<String, Object> user = new HashMap<>();
                    user.put("username", textInputUsername.getText().toString());

                    db.collection("user").document(String.valueOf(userID))
                            .set(user)
                            .addOnSuccessListener(aVoid -> Log.d("TAG", "DocumentSnapshot successfully written!"))
                            .addOnFailureListener(e -> Log.w("TAG", "Error writing document", e));

                    Toast.makeText(this, "Profile successfully created!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PersonalInformationActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private long addDataToDB(String firstName, String lastName, String username, String gender, @NonNull String weight, String height, Date dateOfBirth, String email, String password) {
        if (weight.length() > 3) {
            weight = weight.substring(0, weight.length() - 3);
        }

        if (height.length() > 3) {
            height = height.substring(0, height.length() - 3);
        }

        Random random = new Random(System.nanoTime());
        long userID = Math.abs(random.nextLong());

        realm.beginTransaction();
        User user = realm.createObject(User.class, userID);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setGender(gender);
        user.setDateOfBirth(dateOfBirth);
        user.setWeight(Double.parseDouble(weight));
        user.setHeight(Double.parseDouble(height));
        user.seteMail(email);
        user.setPassword(password);
        realm.commitTransaction();
        realm.close();

        return userID;
    }

    // Method to open the image chooser
    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Method to handle result from image chooser
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            // Define the path where you want to save the image
            File appDirectory = new File(getExternalFilesDir(null), "JourneyFlow");
            if (!appDirectory.exists()) {
                appDirectory.mkdirs();
            }

            File profileImage = new File(appDirectory, "profile_picture.jpg");

            try (InputStream inputStream = getContentResolver().openInputStream(imageUri);
                 OutputStream outputStream = Files.newOutputStream(profileImage.toPath())) {

                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }

                try {
                    Bitmap resizedBitmap = getResizedBitmap(imageUri, 300, 300); // Adjust size as needed
                    Bitmap circularBitmap = getCircularBitmap(resizedBitmap);
                    profileImageButton.setImageBitmap(circularBitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Image resize problem", Toast.LENGTH_LONG).show();
                }

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Loading image problem", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Method to resize the selected image
    private Bitmap getResizedBitmap(Uri uri, int targetWidth, int targetHeight) throws Exception {
        InputStream input = getContentResolver().openInputStream(uri);

        // First, decode with inJustDecodeBounds=true to get the dimensions
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(input, null, options);
        input.close();

        // Calculate inSampleSize to reduce memory usage
        options.inSampleSize = calculateInSampleSize(options, targetWidth, targetHeight);
        options.inJustDecodeBounds = false;

        // Decode with inSampleSize set
        input = getContentResolver().openInputStream(uri);
        Bitmap resizedBitmap = BitmapFactory.decodeStream(input, null, options);
        input.close();

        return resizedBitmap;
    }

    // Method to calculate the appropriate sample size
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    // Check if storage permission is granted
    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    // Request storage permission if not already granted
    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_CODE);
        }
    }

    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImageChooser();
            } else {
                Toast.makeText(this, "Permission denied. Cannot access images.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Method to create a circular bitmap
    private Bitmap getCircularBitmap(Bitmap bitmap) {
        int size = Math.min(bitmap.getWidth(), bitmap.getHeight());
        Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        float radius = size / 2f;
        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint.setShader(shader);

        canvas.drawCircle(radius, radius, radius, paint);
        return output;
    }

    private void dialogInternetConnection(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Settings", (dialog, which) -> {
            // Open network settings
            context.startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private boolean isInternetAvailable(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }
}