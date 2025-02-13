package com.project.journeyflow.fragments.profile;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.textfield.TextInputEditText;
import com.project.journeyflow.R;
import com.project.journeyflow.database.User;
import com.project.journeyflow.fragments.ProfileFragment;
import com.project.journeyflow.query.profile.PersInfoQuery;

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
import java.util.List;


public class PersInfoFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 101;
    private ImageButton imageButtonProfilePicture;
    private TextInputEditText textInputFirstName, textInputLastName, textInputUsername;
    private RadioGroup radioGroup;
    private RadioButton radioButtonMale, radioButtonFemale;
    private Spinner weightSpinner, heightSpinner;
    private Button buttonUpdate;
    private TextView textViewSelectedDate;
    private ImageButton imageButtonPickDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_persinfo, container, false);
        PersInfoQuery query = new PersInfoQuery(requireActivity());

        initializeViews(view);

        User user = query.getPersonalInfo();

        // Display current personal information
        setFirstName(textInputFirstName, user.getFirstName());
        setLastName(textInputLastName, user.getLastName());
        setUsername(textInputUsername, user.getUsername());
        setWeightSpinner(weightSpinner, user.getWeight());
        setHeightSpinner(heightSpinner, user.getHeight());

        showAndChooseDateOfBirth(imageButtonPickDate, textViewSelectedDate, user.getDateOfBirth());
        showProfilePic(imageButtonProfilePicture, query);
        showAndChooseGender(radioButtonMale, radioButtonFemale, user.getGender());

        chooseNewProfilePic(imageButtonProfilePicture);

        updatePersonalInformation(buttonUpdate, query, radioGroup, view);

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    private void initializeViews(View view) {
        imageButtonProfilePicture = view.findViewById(R.id.imageButtonProfilePicturePersInfo);

        textInputFirstName = view.findViewById(R.id.firstNamePersInfo);
        textInputLastName = view.findViewById(R.id.lastNamePersInfo);
        textInputUsername = view.findViewById(R.id.usernamePersInfo);

        radioGroup = view.findViewById(R.id.radioGroupGenderPersInfo);
        radioButtonMale = view.findViewById(R.id.radioButtonMalePersInfo);
        radioButtonFemale = view.findViewById(R.id.radioButtonFemalePersInfo);

        heightSpinner = view.findViewById(R.id.spinnerHeightPersInfo);
        weightSpinner = view.findViewById(R.id.spinnerWeightPersInfo);

        imageButtonPickDate = view.findViewById(R.id.imageButtonPickDatePersInfo);
        textViewSelectedDate = view.findViewById(R.id.textViewPersInfoSelectedDate);

        buttonUpdate = view.findViewById(R.id.buttonUpdatePersInfo);
    }

    private void chooseNewProfilePic(ImageButton imageButton) {
        imageButton.setOnClickListener(view -> {
            if (checkStoragePermission()) {
                openImageChooser();
            } else {
                requestStoragePermission();
            }
        });
    }

    private void setFirstName(TextInputEditText textInputFirstName, String value){
        textInputFirstName.setText(value);
    }

    private void setLastName(TextInputEditText textInputLastName, String value){
        textInputLastName.setText(value);
    }

    private void setUsername(TextInputEditText textInputUsername, String value){
        textInputUsername.setText(value);
        textInputUsername.setFocusable(false);
        textInputUsername.setClickable(false);
    }



    private void showProfilePic(ImageButton profileImageButton, PersInfoQuery query) {
        query.displayProfilePicture(profileImageButton);
    }

    private void setWeightSpinner(Spinner weightSpinner, double currentWeight) {
        // Weight spinner
        List<String> listWeight = new ArrayList<>();
        for (int i = 1; i <= 200; i++) {
            listWeight.add(i + " kg");
        }

        ArrayAdapter<String> arrayAdapterWeight = new ArrayAdapter<>(requireActivity(), R.layout.drop_down_spinner, listWeight);
        arrayAdapterWeight.setDropDownViewResource(R.layout.drop_down_spinner);
        weightSpinner.setAdapter(arrayAdapterWeight);
        weightSpinner.setSelection(((int) currentWeight) - 1); // -1 because always round on less
        weightSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setHeightSpinner(Spinner heightSpinner, double currentHeight) {
        // Height spinner
        List<String> listHeight = new ArrayList<>();
        for (int i = 1; i <= 230; i++) {
            listHeight.add(i + " cm");
        }

        ArrayAdapter arrayAdapterHeight = new ArrayAdapter(requireActivity(), R.layout.drop_down_spinner, listHeight);
        arrayAdapterHeight.setDropDownViewResource(R.layout.drop_down_spinner);
        heightSpinner.setAdapter(arrayAdapterHeight);
        heightSpinner.setSelection(((int) currentHeight) - 1); // -1 because always round on less
        heightSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void showAndChooseDateOfBirth(ImageButton buttonChooseDate, TextView showDate, Date value) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(value);

        int currentDayOfBirth = calendar.get(Calendar.DAY_OF_MONTH);
        int currentMonthOfBirth = calendar.get(Calendar.MONTH) + 1; // Months are zero-based
        int currentYearOfBirth = calendar.get(Calendar.YEAR);
        showDate.setText(currentDayOfBirth + "." + currentMonthOfBirth + "." + currentYearOfBirth + ".");

        buttonChooseDate.setOnClickListener(v -> {
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
                    requireActivity(),
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            // on below line we are setting date to our text view.
                            showDate.setText(dayOfMonth + "." + (monthOfYear + 1) + "." + year + ".");
                        }
                    },
                    // on below line we are passing year,
                    // month and day for selected date in our date picker.
                    year, month, day);
            // at last we are calling show to
            // display our date picker dialog.
            datePickerDialog.show();
        });
    }

    private void showAndChooseGender(RadioButton radioButtonMale, RadioButton radioButtonFemale, String gender) {
        if (gender.equals("Male")) {
            radioButtonMale.setChecked(true);
            radioButtonFemale.setChecked(false);
        }
        else {
            radioButtonMale.setChecked(false);
            radioButtonFemale.setChecked(true);
        }
    }

    private void updatePersonalInformation(Button buttonUpdate, PersInfoQuery query, RadioGroup radioGroup, View mainView) {
        buttonUpdate.setOnClickListener(view -> {
            boolean correctData = true;
            String selectedGender = "";

            int selectedId = radioGroup.getCheckedRadioButtonId();
            if (selectedId != -1) {
                // Find the radio button by ID
                RadioButton selectedRadioButton = mainView.findViewById(selectedId);

                // Get the text of the selected radio button
                selectedGender = selectedRadioButton.getText().toString();
            } else {
                // No radio button is selected
                correctData = false;
                Toast.makeText(requireActivity(), "No option selected for gender", Toast.LENGTH_SHORT).show();
            }

            if(textInputFirstName.getText().toString().isEmpty()){
                textInputFirstName.setError("First name is required!");
                correctData = false;
            }

            if(textInputLastName.getText().toString().isEmpty()){
                textInputLastName.setError("Last name is required!");
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
                Toast.makeText(requireActivity(), "Must be older than 18 years!", Toast.LENGTH_SHORT).show();
                correctData = false;
            }

            if (correctData){
                query.updatePersonalInformation(textInputFirstName.getText().toString(), textInputLastName.getText().toString(), selectedGender, weightSpinner.getSelectedItem().toString(), heightSpinner.getSelectedItem().toString(), birthDate);

                Toast.makeText(requireActivity(), "Profile successfully updated!", Toast.LENGTH_SHORT).show();
                ProfileFragment profileFragment = new ProfileFragment();
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                // Replace FragmentA with FragmentB and add to back stack
                transaction.replace(R.id.fragment_container, profileFragment);
                transaction.addToBackStack(null); // Add to back stack for return navigation
                transaction.commit();
            }
        });
    }


    // Method to open the image chooser
    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Method to handle result from image chooser
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            // Define the path where you want to save the image
            File appDirectory = new File(requireActivity().getExternalFilesDir(null), "JourneyFlow");
            if (!appDirectory.exists()) {
                appDirectory.mkdirs();
            }

            File profileImage = new File(appDirectory, "profile_picture.jpg");

            try (InputStream inputStream = requireActivity().getContentResolver().openInputStream(imageUri);
                 OutputStream outputStream = Files.newOutputStream(profileImage.toPath())) {

                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }

                try {
                    Bitmap resizedBitmap = getResizedBitmap(imageUri, 300, 300); // Adjust size as needed
                    Bitmap circularBitmap = getCircularBitmap(resizedBitmap);
                    imageButtonProfilePicture.setImageBitmap(circularBitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(requireActivity(), "Image resize problem", Toast.LENGTH_LONG).show();
                }

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(requireActivity(), "Loading image problem", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Method to resize the selected image
    private Bitmap getResizedBitmap(Uri uri, int targetWidth, int targetHeight) throws Exception {
        InputStream input = requireActivity().getContentResolver().openInputStream(uri);

        // First, decode with inJustDecodeBounds=true to get the dimensions
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(input, null, options);
        input.close();

        // Calculate inSampleSize to reduce memory usage
        options.inSampleSize = calculateInSampleSize(options, targetWidth, targetHeight);
        options.inJustDecodeBounds = false;

        // Decode with inSampleSize set
        input = requireActivity().getContentResolver().openInputStream(uri);
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
        return ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    // Request storage permission if not already granted
    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(requireActivity(),
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
                Toast.makeText(requireActivity(), "Permission denied. Cannot access images.", Toast.LENGTH_SHORT).show();
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

}
