package com.project.journeyflow.fragments.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.github.mikephil.charting.data.BarEntry;
import com.project.journeyflow.R;
import com.project.journeyflow.chart.BarChartPagerAdapter;
import com.project.journeyflow.fragments.TrackingFragment;
import com.project.journeyflow.query.profile.StatisticsQuery;
import com.project.journeyflow.query.profile.StatisticsQueryChart;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StatisticsFragment extends Fragment {

    private TextView textViewMonth, textViewTotalDistanceMonth, textViewNumOfJourneyMonth, textViewDurationMonth;
    private TextView textViewAvgDistanceMonth, textViewAvgDurationMonth;
    private TextView textViewYear, textViewTotalDistanceYear, textViewNumOfJourneyYear, textViewDurationYear;
    private TextView textViewAvgDistanceYear, textViewAvgDurationYear, textViewMessage;
    private ViewPager2 viewPagerMonth, viewPagerYear;
    private Button buttonNow;
    private ScrollView scrollView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_statistics, container, false);
        StatisticsQueryChart statisticsQueryChart = new StatisticsQueryChart(requireContext());
        StatisticsQuery statisticsQuery = new StatisticsQuery(requireActivity());

        initializeViews(view);

        if (!statisticsQuery.getMonthJourneys().isEmpty()) {

            textViewMessage.setVisibility(View.GONE);
            buttonNow.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);

            showMonthStatistics(statisticsQuery);

            showYearStatistics(statisticsQuery);

            showMonthChartStatistics(statisticsQueryChart, statisticsQuery);

            showYearChartStatistics(statisticsQueryChart, statisticsQuery);
        }
        else {
            textViewMessage.setVisibility(View.VISIBLE);
            buttonNow.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);

            buttonNow.setOnClickListener(view1 -> {
                TrackingFragment trackingFragment = new TrackingFragment();

                // Replace the current fragment with the new one
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, trackingFragment)
                        .addToBackStack(null) // Add this transaction to the back stack
                        .commit();
            });
        }

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    private void initializeViews(View view) {
        viewPagerMonth = view.findViewById(R.id.viewPagerStatistics);
        viewPagerYear = view.findViewById(R.id.viewPagerStatistics2);
        textViewMonth = view.findViewById(R.id.textViewStatisticsMonth);
        textViewTotalDistanceMonth = view.findViewById(R.id.textViewTotalDistanceMonth);
        textViewNumOfJourneyMonth = view.findViewById(R.id.textViewNumOfJourneysMonth);
        textViewDurationMonth = view.findViewById(R.id.textViewTotalDurationMonth);
        textViewAvgDistanceMonth = view.findViewById(R.id.textViewAvgDistanceMonth);
        textViewAvgDurationMonth = view.findViewById(R.id.textViewAvgDurationMonth);

        textViewYear = view.findViewById(R.id.textViewStatisticsYear);
        textViewNumOfJourneyYear = view.findViewById(R.id.textViewNumOfJourneysYear);
        textViewDurationYear = view.findViewById(R.id.textViewTotalDurationYear);
        textViewTotalDistanceYear = view.findViewById(R.id.textViewTotalDistanceYear);
        textViewAvgDistanceYear = view.findViewById(R.id.textViewAvgDistanceYear);
        textViewAvgDurationYear = view.findViewById(R.id.textViewAvgDurationYear);

        textViewMessage = view.findViewById(R.id.textViewStatisticsMessage);
        buttonNow = view.findViewById(R.id.buttonStatisticsNow);
        scrollView = view.findViewById(R.id.scrollViewStatistics);
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

    private void showMonthStatistics(StatisticsQuery statisticsQuery) {
        LocalDate today = LocalDate.now();
        textViewMonth.setText(today.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
        textViewNumOfJourneyMonth.setText(statisticsQuery.showNumOfJourneysMonth());
        textViewTotalDistanceMonth.setText(statisticsQuery.calculateTotalDistanceOfMonthJourneys());
        textViewDurationMonth.setText(statisticsQuery.calculateTotalDurationOfMonthJourneys());
        textViewAvgDurationMonth.setText(statisticsQuery.calculateAvgDurationOfMonthJourneys());
        textViewAvgDistanceMonth.setText(statisticsQuery.calculateAvgDistanceOfMonthJourneys());
    }

    private void showYearStatistics(StatisticsQuery statisticsQuery) {
        LocalDate today = LocalDate.now();
        textViewYear.setText(String.valueOf(today.getYear()));
        textViewNumOfJourneyYear.setText(statisticsQuery.showNumOfJourneysYear());
        textViewDurationYear.setText(statisticsQuery.calculateTotalDurationJourneysInYear());
        textViewTotalDistanceYear.setText(statisticsQuery.calculateTotalDistanceOfJourneysInYear());
        textViewAvgDistanceYear.setText(statisticsQuery.calculateAvgDistanceOfJourneysInYear());
        textViewAvgDurationYear.setText(statisticsQuery.calculateAvgDurationOfJourneysInYear());
    }

    private void showMonthChartStatistics(StatisticsQueryChart statisticsQueryChart, StatisticsQuery query) {

        // Prepare data for the charts
        List<List<BarEntry>> chartDataList = new ArrayList<>();
        chartDataList.add(createBarEntries(statisticsQueryChart.getNumberOfMonthJourneys())); // Chart 1
        chartDataList.add(createBarEntries(statisticsQueryChart.getDurationForDaysInMonth())); // Chart 2
        chartDataList.add(createBarEntries(statisticsQueryChart.getDistanceForDaysInMonth()));    // Chart 3

        // Set up adapter and attach it to the ViewPager
        BarChartPagerAdapter adapterMonth = new BarChartPagerAdapter(requireContext(), chartDataList, "MONTH", query.getDatesInMonth());
        viewPagerMonth.setAdapter(adapterMonth);
    }

    private void showYearChartStatistics(StatisticsQueryChart statisticsQueryChart, StatisticsQuery query) {

        // Prepare data for the charts
        List<List<BarEntry>> chartDataList = new ArrayList<>();
        chartDataList.add(createBarEntries(statisticsQueryChart.getNumberOfJourneysInYear())); // Chart 1
        chartDataList.add(createBarEntries(statisticsQueryChart.getDurationOfJourneysInYear())); // Chart 2
        chartDataList.add(createBarEntries(statisticsQueryChart.getDistanceOfJourenysInYear()));    // Chart 3

        // Set up adapter and attach it to the ViewPager
        BarChartPagerAdapter adapterYear = new BarChartPagerAdapter(requireContext(), chartDataList, "YEAR", query.getMonthInYear());
        viewPagerYear.setAdapter(adapterYear);
    }
}
