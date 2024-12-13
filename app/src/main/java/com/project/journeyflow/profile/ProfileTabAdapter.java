package com.project.journeyflow.profile;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.project.journeyflow.fragments.profile.CalendarFragment;
import com.project.journeyflow.fragments.profile.SettingsFragment;
import com.project.journeyflow.fragments.profile.StatisticsFragment;

public class ProfileTabAdapter extends FragmentStateAdapter {

    public ProfileTabAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new StatisticsFragment();
            case 1:
                return new CalendarFragment();
            case 2:
                return new SettingsFragment();
            default:
                return new StatisticsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
