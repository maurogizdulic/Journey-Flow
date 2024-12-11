package com.project.journeyflow.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.project.journeyflow.R;
import com.project.journeyflow.query.display.HomeFragmentDisplayData;

import org.osmdroid.views.MapView;

public class HomeFragment extends Fragment {

    private MapView mapView;
    private TextView textViewTitle, textViewNoJourney, textViewJourneyDuration, textViewLastDistance, textViewAverageSpeed, textViewDateTime;
    private ScrollView scrollViewLastJourney;
    private LineChart lineChartDistance, lineChartAltitude, lineChartSpeed;

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initializeViews(view);

        // Enable scrolling on map (Map is in Scroll View)
        mapView = view.findViewById(R.id.osmMapView);
        mapView.setMultiTouchControls(true);
        mapView.setOnTouchListener((view1, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                view1.getParent().requestDisallowInterceptTouchEvent(true);
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                view1.getParent().requestDisallowInterceptTouchEvent(false);
            }
            return false;
        });

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        HomeFragmentDisplayData query = new HomeFragmentDisplayData(requireActivity());
        query.displayDataOnHomeFragment(scrollViewLastJourney, textViewTitle, textViewNoJourney, textViewDateTime, textViewJourneyDuration, textViewLastDistance, textViewAverageSpeed, mapView, lineChartDistance, lineChartAltitude, lineChartSpeed);
    }

    public void initializeViews(View view) {
        textViewTitle = view.findViewById(R.id.textViewHomeTitle);
        textViewNoJourney = view.findViewById(R.id.textViewHomeNoJourney);
        textViewDateTime = view.findViewById(R.id.textViewHomeJourneyDateTime);
        textViewJourneyDuration = view.findViewById(R.id.textViewHomeJourneyDuration);
        textViewLastDistance = view.findViewById(R.id.textViewHomeLastDistance);
        textViewAverageSpeed = view.findViewById(R.id.textViewHomeAverageSpeed);
        lineChartDistance = view.findViewById(R.id.lineChartHomeDistance);
        lineChartAltitude = view.findViewById(R.id.lineChartHomeAltitude);
        lineChartSpeed = view.findViewById(R.id.lineChartHomeSpeed);
        scrollViewLastJourney = view.findViewById(R.id.scrollViewLastJourney);
    }
}
