package com.project.journeyflow.query.profile;

import android.content.Context;

import com.project.journeyflow.calculation.Calculation;
import com.project.journeyflow.database.TrackingData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.realm.RealmResults;

public class StatisticsQueryChart extends StatisticsQuery{
    public StatisticsQueryChart(Context context) {
        super(context);
    }

    // Month statistics
    public float[] getNumberOfMonthJourneys() {
        RealmResults<TrackingData> monthTrackingDataList = getMonthJourneys();
        Map<Integer, Integer> dailyTrackingCount = new HashMap<>();

        for (TrackingData tracking : monthTrackingDataList) {

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(tracking.getJourneyDate());
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

            dailyTrackingCount.put(dayOfMonth, dailyTrackingCount.getOrDefault(dayOfMonth, 0) + 1);
        }

        // Build a list to store counts only for days with trackings
        List<Float> trackingCounts = new ArrayList<>();

        // Iterate over the entries in the map and add counts to the list
        for (Map.Entry<Integer, Integer> entry : dailyTrackingCount.entrySet()) {
            int day = entry.getKey();
            int count = entry.getValue();

            // Add the count as a float to the list
            trackingCounts.add((float) count);
        }

        // Convert the list to a float array
        float[] result = new float[trackingCounts.size()];
        for (int i = 0; i < trackingCounts.size(); i++) {
            result[i] = trackingCounts.get(i);
        }

        return result;
    }

    public float[] getDurationForDaysInMonth() {
        RealmResults<TrackingData> monthTrackingDataList = getMonthJourneys();
        Map<Integer, Integer> dailyTrackingCount = new HashMap<>();

        for (TrackingData tracking : monthTrackingDataList) {

            if (tracking != null) {
                long duration = Calculation.calculateDurationOfJourneyInSeconds(Objects.requireNonNull(tracking.getDateTimeList().first()), Objects.requireNonNull(tracking.getDateTimeList().last()));
                duration = duration / 60;

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(tracking.getJourneyDate());
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                dailyTrackingCount.put(dayOfMonth, dailyTrackingCount.getOrDefault(dayOfMonth, 0) + (int) duration);
            }
        }

        // Build a list to store counts only for days with trackings
        List<Float> trackingCounts = new ArrayList<>();

        // Iterate over the entries in the map and add counts to the list
        for (Map.Entry<Integer, Integer> entry : dailyTrackingCount.entrySet()) {
            int day = entry.getKey();
            int count = entry.getValue();

            // Add the count as a float to the list
            trackingCounts.add((float) count);
        }

        // Convert the list to a float array
        float[] result = new float[trackingCounts.size()];
        for (int i = 0; i < trackingCounts.size(); i++) {
            result[i] = trackingCounts.get(i);
        }

        return result;
    }

    public float[] getDistanceForDaysInMonth() {

        RealmResults<TrackingData> monthTrackingDataList = getMonthJourneys();
        Map<Integer, Float> dailyTrackingCount = new HashMap<>();

        for (TrackingData tracking : monthTrackingDataList) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(tracking.getJourneyDate());
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

            dailyTrackingCount.put(dayOfMonth, (float) (dailyTrackingCount.getOrDefault(dayOfMonth, Float.valueOf(0)) + (tracking.getTraveledDistanceList().last() / 1000)));
        }

        // Build a list to store counts only for days with trackings
        List<Float> trackingCounts = new ArrayList<>();

        // Iterate over the entries in the map and add counts to the list
        for (Map.Entry<Integer, Float> entry : dailyTrackingCount.entrySet()) {
            int day = entry.getKey();
            float count = entry.getValue();

            // Add the count as a float to the list
            trackingCounts.add((float) count);
        }

        // Convert the list to a float array
        float[] result = new float[trackingCounts.size()];
        for (int i = 0; i < trackingCounts.size(); i++) {
            result[i] = trackingCounts.get(i);
        }

        return result;
    }

    // Year statistics
    public float[] getNumberOfJourneysInYear() {
        RealmResults<TrackingData> yearTrackingDataList = getYearJourneys();
        Map<Integer, Integer> monthlyTrackingCount = new HashMap<>();

        for (TrackingData tracking : yearTrackingDataList) {

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(tracking.getJourneyDate());
            int monthOfYear = calendar.get(Calendar.MONTH);

            monthlyTrackingCount.put(monthOfYear, monthlyTrackingCount.getOrDefault(monthOfYear, 0) + 1);
        }

        List<Float> trackingCounts = new ArrayList<>();

        for (Map.Entry<Integer, Integer> entry : monthlyTrackingCount.entrySet()) {
            int month = entry.getKey();
            int count = entry.getValue();

            // Add the count as a float to the list
            trackingCounts.add((float) count);
        }

        float[] result = new float[trackingCounts.size()];
        for (int i = 0; i < trackingCounts.size(); i++) {
            result[i] = trackingCounts.get(i);
        }

        return result;
    }

    public float[] getDurationOfJourneysInYear() {
        RealmResults<TrackingData> yearTrackingDataList = getYearJourneys();
        Map<Integer, Integer> monthlyTrackingCount = new HashMap<>();

        for (TrackingData tracking : yearTrackingDataList) {

            if (tracking != null) {
                long duration = Calculation.calculateDurationOfJourneyInSeconds(tracking.getDateTimeList().first(), tracking.getDateTimeList().last());
                duration = duration / 60;

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(tracking.getJourneyDate());
                int monthOfYear = calendar.get(Calendar.MONTH);

                monthlyTrackingCount.put(monthOfYear, monthlyTrackingCount.getOrDefault(monthOfYear, 0) + (int) duration);
            }
        }

        // Build a list to store counts only for days with trackings
        List<Float> trackingCounts = new ArrayList<>();

        // Iterate over the entries in the map and add counts to the list
        for (Map.Entry<Integer, Integer> entry : monthlyTrackingCount.entrySet()) {
            int day = entry.getKey();
            int count = entry.getValue();

            // Add the count as a float to the list
            trackingCounts.add((float) count);
        }

        // Convert the list to a float array
        float[] result = new float[trackingCounts.size()];
        for (int i = 0; i < trackingCounts.size(); i++) {
            result[i] = trackingCounts.get(i);
        }

        return result;
    }

    public float[] getDistanceOfJourenysInYear() {
        RealmResults<TrackingData> yearTrackingDataList = getYearJourneys();
        Map<Integer, Float> monthlyTrackingCount = new HashMap<>();

        for (TrackingData tracking : yearTrackingDataList) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(tracking.getJourneyDate());
            int monthOfYear = calendar.get(Calendar.MONTH);

            monthlyTrackingCount.put(monthOfYear, (float) (monthlyTrackingCount.getOrDefault(monthOfYear, Float.valueOf(0)) + (tracking.getTraveledDistanceList().last() / 1000)));
        }

        // Build a list to store counts only for days with trackings
        List<Float> trackingCounts = new ArrayList<>();

        // Iterate over the entries in the map and add counts to the list
        for (Map.Entry<Integer, Float> entry : monthlyTrackingCount.entrySet()) {
            int day = entry.getKey();
            float count = entry.getValue();

            // Add the count as a float to the list
            trackingCounts.add((float) count);
        }

        // Convert the list to a float array
        float[] result = new float[trackingCounts.size()];
        for (int i = 0; i < trackingCounts.size(); i++) {
            result[i] = trackingCounts.get(i);
        }

        return result;
    }
}
