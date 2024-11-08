package com.project.journeyflow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.project.journeyflow.fragments.HistoryFragment;
import com.project.journeyflow.fragments.HomeFragment;
import com.project.journeyflow.fragments.ProfileFragment;
import com.project.journeyflow.fragments.PublicFragment;
import com.project.journeyflow.fragments.TrackingFragment;
import com.project.journeyflow.signup.NavigationActivity;
import com.project.journeyflow.signup.SignUpActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Check signup status
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        boolean isSignedUp = sharedPreferences.getBoolean("isSignedUp", false);

        if (!isSignedUp) {
            // User is not signed up, redirect to SignUpActivity
            Intent signUpIntent = new Intent(this, SignUpActivity.class);
            startActivity(signUpIntent);
            finish(); // Prevent MainActivity from loading if user isn't signed up
        } else {
            // User is signed up, continue with MainActivity
            setContentView(R.layout.activity_main);
            // Initialize MainActivity components here
        }


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Home fragment on every entry in app
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment())
                .commit();

        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment;

            if (item.getItemId() == R.id.nav_home){
                selectedFragment = new HomeFragment();
            }
            else if (item.getItemId() == R.id.nav_history){
                selectedFragment = new HistoryFragment();
            }
            else if (item.getItemId() == R.id.nav_public){
                selectedFragment = new PublicFragment();
            }
            else if (item.getItemId() == R.id.nav_profile){
                selectedFragment = new ProfileFragment();
            }
            else /*if (item.getItemId() == R.id.nav_tracking)*/{
                selectedFragment = new TrackingFragment();
            }

            // Replace the fragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();

            return true;
        });

    }
}