package com.project.journeyflow.items;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.project.journeyflow.database.TrackingData;
import com.project.journeyflow.fragments.item_detail.AltitudeDetailFragment;
import com.project.journeyflow.fragments.item_detail.DistanceDetailFragment;
import com.project.journeyflow.fragments.item_detail.MapDetailFragment;
import com.project.journeyflow.fragments.item_detail.SpeedDetailFragment;

public class TabPagerAdapter extends FragmentStateAdapter {
    TrackingData trackingData;

    public TabPagerAdapter(@NonNull FragmentActivity fragmentActivity, TrackingData trackingData) {
        super(fragmentActivity);
        this.trackingData = trackingData;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new MapDetailFragment(trackingData); // Tab 1
            case 1:
                return new SpeedDetailFragment(trackingData); // Tab 2
            case 2:
                return new AltitudeDetailFragment(trackingData); // Tab 3
            case 3:
                return new DistanceDetailFragment(trackingData);
            default:
                return new MapDetailFragment(trackingData); // Default
        }

    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
