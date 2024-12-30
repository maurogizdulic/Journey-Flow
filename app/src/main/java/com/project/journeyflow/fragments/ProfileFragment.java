package com.project.journeyflow.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.project.journeyflow.R;
import com.project.journeyflow.fragments.profile.CalendarFragment;
import com.project.journeyflow.fragments.profile.PersInfoFragment;
import com.project.journeyflow.fragments.profile.SettingsFragment;
import com.project.journeyflow.fragments.profile.StatisticsFragment;
import com.project.journeyflow.query.ProfileFragmentQuery;

public class ProfileFragment extends Fragment {

    private TextView textViewName, textViewUsername;
    private ImageView imageViewProfile;
    private ConstraintLayout constraintLayoutStatistics, constraintLayoutCalendar, constraintLayoutPersonalInf, constraintLayoutSettings;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

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
        constraintLayoutStatistics = view.findViewById(R.id.constraintLayoutStatistics);
        constraintLayoutCalendar = view.findViewById(R.id.constraintLayoutCalendar);
        constraintLayoutPersonalInf = view.findViewById(R.id.constraintLayoutPersonalInf);
        constraintLayoutSettings = view.findViewById(R.id.constraintLayoutSettings);

        imageViewProfile = view.findViewById(R.id.imageViewProfileFragment);
        textViewName = view.findViewById(R.id.textViewProfileName);
        textViewUsername = view.findViewById(R.id.textViewProfileUsername);
    }

    private void buttonClicks() {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        constraintLayoutStatistics.setOnClickListener(view -> {
            Fragment statisticsFragment = new StatisticsFragment();
            fragmentTransaction.replace(R.id.fragment_container, statisticsFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        constraintLayoutCalendar.setOnClickListener(view -> {
            Fragment calendarFragment = new CalendarFragment();
            fragmentTransaction.replace(R.id.fragment_container, calendarFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        constraintLayoutPersonalInf.setOnClickListener(view -> {
            Fragment persInfoFragment = new PersInfoFragment();
            fragmentTransaction.replace(R.id.fragment_container, persInfoFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        constraintLayoutSettings.setOnClickListener(view -> {
            Fragment settingsFragment = new SettingsFragment();
            fragmentTransaction.replace(R.id.fragment_container, settingsFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
    }
}
