package com.project.journeyflow.fragments.profile;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.project.journeyflow.MainActivity;
import com.project.journeyflow.R;
import com.project.journeyflow.location.map.Map;
import com.project.journeyflow.query.ProfileFragmentQuery;
import com.project.journeyflow.signup.NavigationActivity;

public class SettingsFragment extends Fragment {

    private TextView textViewName, textViewUsername;
    private ImageView imageViewProfile;
    private ConstraintLayout constraintLayoutChangePassword, constraintLayoutChangeEmail, constraintLayoutLogOut, constraintLayoutDeleteAcc;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        initializeViews(view);

        buttonClicks();

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

    private void buttonClicks() {

        constraintLayoutChangePassword.setOnClickListener(view -> {

        });

        constraintLayoutChangeEmail.setOnClickListener(view -> {

        });

        constraintLayoutLogOut.setOnClickListener(view -> {
            showDialogForLogOut();
        });

        constraintLayoutDeleteAcc.setOnClickListener(view -> {
            showDialogForDeleteAcc();
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

    private void showDialogForDeleteAcc() {
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
}
