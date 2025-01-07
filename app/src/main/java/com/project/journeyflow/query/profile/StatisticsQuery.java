package com.project.journeyflow.query.profile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.project.journeyflow.calculation.Calculation;
import com.project.journeyflow.calculation.DateAndTime;
import com.project.journeyflow.database.TrackingData;
import com.project.journeyflow.database.User;
import com.project.journeyflow.query.ProfileFragmentQuery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmResults;

public class StatisticsQuery extends ProfileFragmentQuery {

    public StatisticsQuery(Context context) {
        super(context);
    }

    // Statistics for month

    public String showNumOfJourneysMonth() {

        RealmResults<TrackingData> trackingData = getMonthJourneys();

        return "Number of journeys: " + trackingData.size();
    }

    public String calculateTotalDistanceOfMonthJourneys() {
        RealmResults<TrackingData> monthTrackingDataList = getMonthJourneys();
        double totalDistance = 0;

        for (TrackingData tracking : monthTrackingDataList) {
            if (tracking != null) {
                totalDistance += tracking.getTraveledDistanceList().last();
            }
        }

        return "Total distance traveled: " + Calculation.calculateDistance(totalDistance);
    }

    @SuppressLint("DefaultLocale")
    public String calculateTotalDurationOfMonthJourneys() {
        RealmResults<TrackingData> monthTrackingDataList = getMonthJourneys();

        int totalDurationInSeconds = 0;

        for (TrackingData tracking : monthTrackingDataList) {
            if (tracking != null) {
                long duration = Calculation.calculateDurationOfJourneyInSeconds(Objects.requireNonNull(tracking.getDateTimeList().first()), Objects.requireNonNull(tracking.getDateTimeList().last()));
                totalDurationInSeconds += (int) duration;
            }
        }

        Log.i("TIME IN HOURS", String.valueOf(totalDurationInSeconds));
        int hours = totalDurationInSeconds / 3600;
        int minutes = (totalDurationInSeconds % 3600) / 60;
        int seconds = totalDurationInSeconds % 60;
        return String.format("Total duration of journeys: %02dh:%02dm:%02ds", hours, minutes, seconds);
    }

    public String calculateAvgDurationOfMonthJourneys() {
        RealmResults<TrackingData> monthTrackingDataList = getMonthJourneys();

        int totalDurationInSeconds = 0;

        for (TrackingData tracking : monthTrackingDataList) {
            if (tracking != null) {
                long duration = Calculation.calculateDurationOfJourneyInSeconds(Objects.requireNonNull(tracking.getDateTimeList().first()), Objects.requireNonNull(tracking.getDateTimeList().last()));
                totalDurationInSeconds += (int) duration;
            }
        }

        int averageDurationInSeconds = totalDurationInSeconds / monthTrackingDataList.size();

        Log.i("TIME IN HOURS", String.valueOf(totalDurationInSeconds));
        int hours = averageDurationInSeconds / 3600;
        int minutes = (averageDurationInSeconds % 3600) / 60;
        int seconds = averageDurationInSeconds % 60;
        return String.format("Avg duration of journeys: %02dh:%02dm:%02ds", hours, minutes, seconds);
    }

    public String calculateAvgDistanceOfMonthJourneys() {
        RealmResults<TrackingData> monthTrackingDataList = getMonthJourneys();
        double totalDistance = 0;

        for (TrackingData tracking : monthTrackingDataList) {
            if (tracking != null) {
                totalDistance += tracking.getTraveledDistanceList().last();
            }
        }

        double avgDistance = totalDistance / monthTrackingDataList.size();

        return "Avg distance traveled: " + Calculation.calculateDistance(avgDistance);
    }

    public List<String> getDatesInMonth() {
        RealmResults<TrackingData> trackingDataList = getMonthJourneys();

        List<String> dates = new ArrayList<>();
        String date = "";
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        for (TrackingData data : trackingDataList) {
            date = dateFormat.format(Objects.requireNonNull(data.getDateTimeList().first()));
            dates.add(date);
        }

        return dates;
    }

    public RealmResults<TrackingData> getMonthJourneys(){

        User user = fetchUserData();
        Realm realm = initializeRealm();

        return realm.where(TrackingData.class)
                .equalTo("userID", user.getId())
                .greaterThanOrEqualTo("journeyDate", DateAndTime.getStartOfMonth())
                .lessThan("journeyDate", DateAndTime.getEndOfMonth())
                .findAll();
    }

    // Statistics for year

    public String showNumOfJourneysYear() {
        RealmResults<TrackingData> trackingData = getYearJourneys();

        return "Number of journeys: " + trackingData.size();
    }

    public RealmResults<TrackingData> getYearJourneys() {

        User user = fetchUserData();
        Realm realm = initializeRealm();

        return realm.where(TrackingData.class)
                .equalTo("userID", user.getId())
                .greaterThanOrEqualTo("journeyDate", DateAndTime.getStartOfCurrentYear())
                .lessThan("journeyDate", DateAndTime.getEndOfCurrentYear())
                .findAll();
    }

    public String calculateTotalDistanceOfJourneysInYear() {
        RealmResults<TrackingData> monthTrackingDataList = getYearJourneys();
        double totalDistance = 0;

        for (TrackingData tracking : monthTrackingDataList) {
            if (tracking != null) {
                totalDistance += tracking.getTraveledDistanceList().last();
            }
        }

        return "Total distance traveled: " + Calculation.calculateDistance(totalDistance);
    }

    @SuppressLint("DefaultLocale")
    public String calculateTotalDurationJourneysInYear() {
        RealmResults<TrackingData> monthTrackingDataList = getYearJourneys();

        int totalDurationInSeconds = 0;

        for (TrackingData tracking : monthTrackingDataList) {
            if (tracking != null) {
                long duration = Calculation.calculateDurationOfJourneyInSeconds(Objects.requireNonNull(tracking.getDateTimeList().first()), Objects.requireNonNull(tracking.getDateTimeList().last()));
                totalDurationInSeconds += (int) duration;
            }
        }

        Log.i("TIME IN HOURS", String.valueOf(totalDurationInSeconds));
        int hours = totalDurationInSeconds / 3600;
        int minutes = (totalDurationInSeconds % 3600) / 60;
        int seconds = totalDurationInSeconds % 60;
        return String.format("Total duration of journeys: %02dh:%02dm:%02ds", hours, minutes, seconds);
    }

    @SuppressLint("DefaultLocale")
    public String calculateAvgDurationOfJourneysInYear() {
        RealmResults<TrackingData> monthTrackingDataList = getYearJourneys();

        int totalDurationInSeconds = 0;

        for (TrackingData tracking : monthTrackingDataList) {
            if (tracking != null) {
                long duration = Calculation.calculateDurationOfJourneyInSeconds(Objects.requireNonNull(tracking.getDateTimeList().first()), Objects.requireNonNull(tracking.getDateTimeList().last()));
                totalDurationInSeconds += (int) duration;
            }
        }

        int averageDurationInSeconds = totalDurationInSeconds / monthTrackingDataList.size();

        Log.i("TIME IN HOURS", String.valueOf(totalDurationInSeconds));
        int hours = averageDurationInSeconds / 3600;
        int minutes = (averageDurationInSeconds % 3600) / 60;
        int seconds = averageDurationInSeconds % 60;
        return String.format("Avg duration of journeys: %02dh:%02dm:%02ds", hours, minutes, seconds);
    }

    public String calculateAvgDistanceOfJourneysInYear() {
        RealmResults<TrackingData> monthTrackingDataList = getYearJourneys();
        double totalDistance = 0;

        for (TrackingData tracking : monthTrackingDataList) {
            if (tracking != null) {
                totalDistance += tracking.getTraveledDistanceList().last();
            }
        }

        double avgDistance = totalDistance / monthTrackingDataList.size();

        return "Avg distance traveled: " + Calculation.calculateDistance(avgDistance);
    }

    public List<String> getMonthInYear() {
        RealmResults<TrackingData> trackingDataList = getYearJourneys();

        List<String> months = new ArrayList<>();
        String date = "";
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM", Locale.ENGLISH);
        for (TrackingData data : trackingDataList) {
            date = dateFormat.format(Objects.requireNonNull(data.getDateTimeList().first()));
            months.add(date);
        }

        return months;
    }

}
