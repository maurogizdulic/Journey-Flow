package com.project.journeyflow.calculation;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Calculation {

    @SuppressLint("DefaultLocale")
    public static String calculateDurationOfJourney(Date start, Date end){
        long durationMillis = end.getTime() - start.getTime();

        // Convert milliseconds to hours, minutes, and seconds
        long hours = (durationMillis / (1000 * 60 * 60)) % 24;
        long minutes = (durationMillis / (1000 * 60)) % 60;
        long seconds = (durationMillis / 1000) % 60;

        return String.format("%02dh:%02dm:%02ds", hours, minutes, seconds);
    }

    @SuppressLint("DefaultLocale")
    public static String calculateDistance(Double distance){
        if (distance >= 1000) {
            distance /= 1000;
            return String.format("%.2f km", distance);
        }
        else {
            return String.format("%.1f m", distance);
        }
    }

    @SuppressLint("DefaultLocale")
    public static String calculateAverageSpeed(List<Float> speedList){
        float sumSpeed = 0, averageSpeed = 0;
        for (Float speed : speedList) {
            sumSpeed += speed;
        }
        averageSpeed = sumSpeed / speedList.size();

        // Convert from m/s to km/h
        averageSpeed = averageSpeed * 3600 / 1000;

        return String.format("%.2f", averageSpeed);
    }

    public static String convertToDateTimeString(Date dateTime){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
        return simpleDateFormat.format(dateTime);
    }

    public static List<Double> convertFromMtoKm(List<Double> list) {
        List<Double> doubleList = new ArrayList<>();
        for (Double value : list) {
            value = value / 1000;
            doubleList.add(value);
        }
        return doubleList;
    }

    public static List<Float> convertFromMsToKmh(List<Float> list) {
        List<Float> floatList = new ArrayList<>();
        for (Float value : list) {
            value = value * 3600 / 1000;
            floatList.add(value);
        }
        return floatList;
    }
}
