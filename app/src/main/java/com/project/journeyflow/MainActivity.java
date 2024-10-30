package com.project.journeyflow;

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

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Home fragment on every entry in app
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment())
                .commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

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