package com.project.journeyflow;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.project.journeyflow.fragments.HistoryFragment;
import com.project.journeyflow.fragments.HomeFragment;
import com.project.journeyflow.fragments.ProfileFragment;
import com.project.journeyflow.fragments.PublicFragment;
import com.project.journeyflow.fragments.TrackingFragment;
import com.project.journeyflow.location.LocationBroadcastReceiver;
import com.project.journeyflow.location.LocationViewModel;
import com.project.journeyflow.location.TrackingService;
import com.project.journeyflow.signup.NavigationActivity;

import org.osmdroid.config.Configuration;

public class MainActivity extends AppCompatActivity {

    private LocationBroadcastReceiver locationBroadcastReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        LocationViewModel locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
        locationBroadcastReceiver = new LocationBroadcastReceiver(locationViewModel);

        // Check signup status
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        boolean isSignedUp = sharedPreferences.getBoolean("isSignedUp", false);

        if (!isSignedUp) {
            // User is not signed up, redirect to SignUpActivity
            Intent signUpIntent = new Intent(this, NavigationActivity.class);
            startActivity(signUpIntent);
            finish(); // Prevent MainActivity from loading if user isn't signed up
        } else {
            // User is signed up, continue with MainActivity
            setContentView(R.layout.activity_main);
            // Initialize MainActivity components here
            Configuration.getInstance().setUserAgentValue(getPackageName());
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        if (getIntent() != null && "TrackingFragment".equals(getIntent().getStringExtra("openFragment"))) {
            openOrReuseTrackingFragment();
            bottomNavigationView.setSelectedItemId(R.id.nav_tracking);
        }
        else {
            // Home fragment on every entry in app
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();

            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }

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

    private void openOrReuseTrackingFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        TrackingFragment trackingFragment = (TrackingFragment) fragmentManager.findFragmentByTag("TRACKING_FRAGMENT");

        if (trackingFragment == null) {
            // TrackingFragment does not exist, add it
            trackingFragment = new TrackingFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, trackingFragment, "TRACKING_FRAGMENT")
                    .addToBackStack(null)
                    .commit();


        } else {
            // TrackingFragment exists, bring it to the foreground
            fragmentManager.popBackStack("TRACKING_FRAGMENT", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(TrackingService.ACTION_LOCATION_BROADCAST);
        registerReceiver(locationBroadcastReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(locationBroadcastReceiver);
    }
}