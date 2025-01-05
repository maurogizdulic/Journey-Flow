package com.project.journeyflow.fragments.item_detail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.project.journeyflow.R;
import com.project.journeyflow.chart.Chart;
import com.project.journeyflow.database.TrackingData;

public class SpeedDetailFragment extends Fragment {

    private TrackingData trackingData;
    private LineChart lineChart;

    public SpeedDetailFragment(TrackingData trackingData) {
        this.trackingData = trackingData;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_speed_detail, container, false);

        initializeView(view);

        if (trackingData != null) {
            Chart.showLineChartSpeed(lineChart, trackingData.getTraveledDistanceList(), trackingData.getSpeedList());
        }

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    private void initializeView(View view) {
        lineChart = view.findViewById(R.id.lineChartSpeedDetail);
    }
}
