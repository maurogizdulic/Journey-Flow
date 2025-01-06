package com.project.journeyflow.fragments.item_detail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.project.journeyflow.R;
import com.project.journeyflow.calculation.Calculation;
import com.project.journeyflow.database.TrackingData;
import com.project.journeyflow.items.TabPagerAdapter;
import com.project.journeyflow.query.item_detail.ItemDetailQuery;

public class ItemDetailFragment extends Fragment {

    private TextView textViewItemDetailUser, textViewItemDetailDistance, textViewItemDetailTime, textViewItemDetailAvgSpeed;
    private TextView textViewItemDetailStart, textViewItemDetailStop, textViewItemDetailAltitudeMax, textViewItemDetailAltitudeMin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_item_detail, container, false);

        initializeViews(view);

        if (getArguments() != null) {
            ItemDetailQuery query = new ItemDetailQuery(requireContext());
            long trackingDataId = getArguments().getLong("trackingDataId");
            TrackingData trackingData = query.getTrackingData(trackingDataId);

            showJourneyDetails(trackingData);

            showTabPager(view, trackingData);
        }

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    private void initializeViews(View view) {
        textViewItemDetailUser = view.findViewById(R.id.textViewItemDetailUser);
        textViewItemDetailDistance = view.findViewById(R.id.textViewItemDetailDistance);
        textViewItemDetailTime = view.findViewById(R.id.textViewItemDetailTime);
        textViewItemDetailAvgSpeed = view.findViewById(R.id.textViewItemDetailAvgSpeed);
        textViewItemDetailStart = view.findViewById(R.id.textViewItemDetailStart);
        textViewItemDetailStop = view.findViewById(R.id.textViewItemDetailStop);
        textViewItemDetailAltitudeMax = view.findViewById(R.id.textViewItemDetailAltitudeMax);
        textViewItemDetailAltitudeMin = view.findViewById(R.id.textViewItemDetailAltitudeMin);
    }

    private void showJourneyDetails(TrackingData trackingData) {

        //textView
        textViewItemDetailDistance.setText(Calculation.calculateDistance(trackingData.getTraveledDistanceList().last()));
        textViewItemDetailTime.setText(Calculation.calculateDurationOfJourney(trackingData.getDateTimeList().first(), trackingData.getDateTimeList().last()));
        textViewItemDetailAvgSpeed.setText(Calculation.calculateAverageSpeed(trackingData.getSpeedList()));
        textViewItemDetailStart.setText(Calculation.convertToDateTimeString(trackingData.getDateTimeList().first()));
        textViewItemDetailStop.setText(Calculation.convertToDateTimeString(trackingData.getDateTimeList().last()));
    }

    private void showTabPager(View view, TrackingData trackingData) {
        TabLayout tabLayout = view.findViewById(R.id.tabLayoutItemDetail);
        ViewPager2 viewPager = view.findViewById(R.id.viewPagerItemDetail);

        // Set up the PagerAdapter
        TabPagerAdapter adapter = new TabPagerAdapter(requireActivity(), trackingData);
        viewPager.setAdapter(adapter);

        // Link TabLayout and ViewPager2
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Map");
                            break;
                        case 1:
                            tab.setText("Speed");
                            break;
                        case 2:
                            tab.setText("Altitude");
                            break;
                        case 3:
                            tab.setText("Distance");
                            break;
                    }
                }).attach();
    }
}
