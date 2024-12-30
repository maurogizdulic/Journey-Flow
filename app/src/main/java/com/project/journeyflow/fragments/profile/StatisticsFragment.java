package com.project.journeyflow.fragments.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.github.mikephil.charting.data.BarEntry;
import com.project.journeyflow.R;
import com.project.journeyflow.chart.BarChartPagerAdapter;
import com.project.journeyflow.query.profile.StatisticsQuery;

import java.util.ArrayList;
import java.util.List;

public class StatisticsFragment extends Fragment {

    private StatisticsQuery statisticsQuery;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        ViewPager2 viewPagerMonth = view.findViewById(R.id.viewPagerStatistics);
        ViewPager2 viewPagerYear = view.findViewById(R.id.viewPagerStatistics2);

        // Prepare data for the charts
        List<List<BarEntry>> chartDataList = new ArrayList<>();
        chartDataList.add(createBarEntries(new float[]{120, 150, 100, 90, 130})); // Chart 1
        chartDataList.add(createBarEntries(new float[]{200, 250, 150, 300, 400})); // Chart 2
        chartDataList.add(createBarEntries(new float[]{80, 100, 60, 50, 90}));    // Chart 3

        // Set up adapter and attach it to the ViewPager
        BarChartPagerAdapter adapterMonth = new BarChartPagerAdapter(requireContext(), chartDataList);
        viewPagerMonth.setAdapter(adapterMonth);

        BarChartPagerAdapter adapterYear = new BarChartPagerAdapter(requireContext(), chartDataList);
        viewPagerYear.setAdapter(adapterYear);

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    // Helper method to generate bar entries for each chart
    private List<BarEntry> createBarEntries(float[] values) {

        //statisticsQuery = new StatisticsQuery(requireContext());
        //User user = statisticsQuery.getUserData();

        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            entries.add(new BarEntry(i, values[i]));
        }
        return entries;
    }
}
