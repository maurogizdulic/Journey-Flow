package com.project.journeyflow.fragments.profile;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.journeyflow.R;
import com.project.journeyflow.query.LogInSignUpQuery;
import com.project.journeyflow.query.ProfileFragmentQuery;
import com.project.journeyflow.query.profile.SettingsQuery;
import com.project.journeyflow.input_validation.InputCorrectness;
import com.project.journeyflow.signup.NavigationActivity;

public class SettingsFragment extends Fragment {

    private TextView textViewName, textViewUsername;
    private ImageView imageViewProfile;
    private ConstraintLayout constraintLayoutChangePassword, constraintLayoutChangeEmail, constraintLayoutLogOut, constraintLayoutDeleteAcc;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        SettingsQuery query = new SettingsQuery(requireActivity());
        LogInSignUpQuery logInSignUpQuery = new LogInSignUpQuery(requireActivity());

        initializeViews(view);

        buttonClicks(query, logInSignUpQuery);

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();

        ProfileFragmentQuery query = new ProfileFragmentQuery(requireActivity());
        query.displayNameAndPicture(imageViewProfile, textViewName, textViewUsername);
    }

    private void initializeViews(View view) {
        constraintLayoutChangePassword = view.findViewById(R.id.constraintLayoutChangePassword);
        constraintLayoutChangeEmail = view.findViewById(R.id.constraintLayoutChangeEmail);
        constraintLayoutLogOut = view.findViewById(R.id.constraintLayoutLogOut);
        constraintLayoutDeleteAcc = view.findViewById(R.id.constraintLayoutDeleteAcc);

        imageViewProfile = view.findViewById(R.id.imageViewProfileFragment);
        textViewName = view.findViewById(R.id.textViewProfileName);
        textViewUsername = view.findViewById(R.id.textViewProfileUsername);
    }

    private void buttonClicks(SettingsQuery query, LogInSignUpQuery logInSignUpQuery) {

        constraintLayoutChangePassword.setOnClickListener(view -> {
            showDialogForChangePassword(query, logInSignUpQuery);
        });

        constraintLayoutChangeEmail.setOnClickListener(view -> {
            showDialogForChangeEmail(query, logInSignUpQuery);
        });

        constraintLayoutLogOut.setOnClickListener(view -> {
            showDialogForLogOut();
        });

        constraintLayoutDeleteAcc.setOnClickListener(view -> {
            showDialogForDeleteAcc(query);
        });
    }

    private void showDialogForLogOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Log Out");
        builder.setMessage("Do you really want to log out?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isSignedUp", false); // Set this to true when signup is complete
            editor.apply();

            Intent intent = new Intent(requireActivity(), NavigationActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            Toast.makeText(requireActivity(), "Logged out successfully", Toast.LENGTH_LONG).show();
        });
        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.dismiss();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDialogForDeleteAcc(SettingsQuery query) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Delete account");
        builder.setMessage("Do you really want to delete your account?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            showSecondDialogForDeleteAcc(query);
        });
        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.dismiss();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showSecondDialogForDeleteAcc(SettingsQuery query) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Delete account");
        builder.setMessage("All data will be permanently deleted. Are you sure you want to continue deleting your account?");
        builder.setPositiveButton("Yes", (dialog, which) -> {

            if (!isInternetAvailable(requireActivity())) {
                dialogInternetConnection(requireActivity(), "No Internet Connection", "Your username is public. To delete your account, you must be connected to the internet to automatically delete your account, public username and public journeys. Please connect to the internet to continue.");
            }
            else {
                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", MODE_PRIVATE);
                long userID = sharedPreferences.getLong("id", 123456789);

                Log.i("USER ID DELETE", String.valueOf(userID));

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("user").document(String.valueOf(userID)).delete()
                                .addOnSuccessListener(unused -> {
                                    Log.i("USER ID DELETE", "SUCCESSFUL DELETE USER IN FIREBASE");
                                    db.collection("journeys").whereEqualTo("ownerId", userID).get()
                                                    .addOnSuccessListener(queryDocumentSnapshots -> {
                                                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                                                            document.getReference().delete().addOnSuccessListener(aVoid -> {
                                                                Log.d("Firestore", "Deleted journey: " + document.getId());
                                                            }).addOnFailureListener(e -> {
                                                                Log.e("FirestoreError", "Failed to delete journey: " + e.getMessage());
                                                            });
                                                        }

                                                        query.deleteAccount();
                                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                                        editor.putBoolean("isSignedUp", false);
                                                        editor.apply();

                                                        Intent intent = new Intent(requireActivity(), NavigationActivity.class);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        startActivity(intent);
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        Toast.makeText(requireActivity(), "Account deletion failed", Toast.LENGTH_LONG).show();
                                                    });
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(requireActivity(), "Account deletion failed", Toast.LENGTH_LONG).show();
                                });
            }
        });
        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.dismiss();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDialogForChangePassword(SettingsQuery query, LogInSignUpQuery query2){
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_change_password, null);

        // Find the EditTexts and Button in the custom layout
        EditText editTextCurrentPassword = dialogView.findViewById(R.id.editTextCurrentPassword);
        EditText editTextNewPassword = dialogView.findViewById(R.id.editTextNewPassword);
        EditText editTextConfirmNewPassword = dialogView.findViewById(R.id.editTextConfirmNewPassword);
        Button buttonChangePassword = dialogView.findViewById(R.id.buttonSubmit);

        // Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // Set button click listener
        buttonChangePassword.setOnClickListener(v -> {
            // Retrieve the input values
            String currentPassword = editTextCurrentPassword.getText().toString().trim();
            String newPassword = editTextNewPassword.getText().toString().trim();
            String confirmNewPassword = editTextConfirmNewPassword.getText().toString().trim();

            if (query2.currentPasswordExists(currentPassword)) {
                if(newPassword.equals(confirmNewPassword)) {

                    InputCorrectness inputCorrectness = new InputCorrectness();
                    boolean isCorrect = true;

                    if (newPassword.isEmpty()) {
                        editTextNewPassword.setError("Password is required!");
                        isCorrect = false;
                    }

                    if (confirmNewPassword.isEmpty()) {
                        editTextConfirmNewPassword.setError("Confirmed password is required!");
                        isCorrect = false;
                    }

                    if (!(inputCorrectness.checkPasswordCorrectness(newPassword))){
                        editTextNewPassword.setError("Password must contain at least one capital letter, number and symbol");
                        editTextNewPassword.setText("");
                        isCorrect = false;
                    }

                    if (isCorrect) {
                        query.changePassword(newPassword);
                        Toast.makeText(getActivity(), "Password changed successfully.", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                }
                else {
                    editTextNewPassword.setText("");
                    editTextConfirmNewPassword.setText("");
                    Toast.makeText(getActivity(), "Incorrectly entered New Password and Confirm new password", Toast.LENGTH_LONG).show();
                }
            }
            else {
                editTextCurrentPassword.setText("");
                Toast.makeText(getActivity(), "Incorrect current password!", Toast.LENGTH_LONG).show();
            }
        });

        // Show the dialog
        dialog.show();
    }

    private void showDialogForChangeEmail(SettingsQuery query, LogInSignUpQuery query2) {

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_change_email, null);

        // Find the EditTexts and Button in the custom layout
        EditText editTextCurrentPassword = dialogView.findViewById(R.id.editTextCurrentPassword);
        EditText editTextCurrentEmail = dialogView.findViewById(R.id.editTextCurrentEmail);
        EditText editTextNewEmail = dialogView.findViewById(R.id.editTextNewEmail);
        Button buttonChangeEmail = dialogView.findViewById(R.id.buttonChangeEmail);

        // Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // Set button click listener
        buttonChangeEmail.setOnClickListener(v -> {
            // Retrieve the input values
            String currentPassword = editTextCurrentPassword.getText().toString().trim();
            String currentEmail = editTextCurrentEmail.getText().toString().trim();
            String newEmail = editTextNewEmail.getText().toString().trim();

            if (query2.currentPasswordExists(currentPassword)) {
                if (query2.currentEmailExists(currentEmail)) {
                    if(!query2.newEmailExists(newEmail)) {

                        InputCorrectness inputCorrectness = new InputCorrectness();
                        boolean isCorrect = true;

                        if (newEmail.isEmpty()) {
                            editTextNewEmail.setError("Email is required!");
                            isCorrect = false;
                        }

                        if (!(inputCorrectness.checkEmailCorrectness(newEmail))){
                            editTextNewEmail.setError("E-mail incorrect!");
                            isCorrect = false;
                        }

                        if (isCorrect) {
                            query.changeEmail(newEmail);
                            Toast.makeText(getActivity(), "E-mail successfully changed!", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                        else {
                            Toast.makeText(getActivity(), "Incorrectly entered e-mail!", Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        editTextNewEmail.setText("");
                        Toast.makeText(getActivity(), "There is an account with a new email address, please change the new email address.", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    editTextCurrentEmail.setText("");
                    Toast.makeText(getActivity(), "Incorrect current email!", Toast.LENGTH_LONG).show();
                }
            }
            else {
                editTextCurrentPassword.setText("");
                Toast.makeText(getActivity(), "Incorrect current password!", Toast.LENGTH_LONG).show();
            }
        });

        // Show the dialog
        dialog.show();
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
