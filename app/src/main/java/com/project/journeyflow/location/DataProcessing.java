package com.project.journeyflow.location;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class DataProcessing {

    public String calculateSpeed(List<Float> speedList) {
        // Get current speed in m/s
        float currentSpeed = speedList.get(speedList.size()-1);

        // Convert speed to km/h and return
        return String.valueOf(convertToKmH(currentSpeed));
    }

    public String calculateAverageSpeed(List<Float> speedList){
        float averageSpeed, sumSpeed = 0;
        for (Float speed : speedList) {
            sumSpeed += speed;
        }
        averageSpeed = sumSpeed / speedList.size();
        averageSpeed = convertToKmHFloat(averageSpeed);

        return String.valueOf(roundValueToOneDecimalPoint(averageSpeed));
    }

    private int convertToKmH(float speed){
        return (int) (speed * 3600 / 1000);
    }

    private float convertToKmHFloat(float speed){
        return (speed * 3600 / 1000);
    }

    public String calculateTraveledDistance(double traveledDistance) {
        // If value is 1km
        if (traveledDistance >= 1000) {
            return roundValueToTwoDecimalPoints(traveledDistance / 1000) + " km";
        }
        else {
            return roundValueToTwoDecimalPoints(traveledDistance) + " m";
        }
    }

    private double roundValueToTwoDecimalPoints(double value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private float roundValueToOneDecimalPoint(float value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(1, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

    public String calculateAltitude(List<Double> altitudeList) {
        return String.valueOf(altitudeList.get(altitudeList.size()-1).intValue());
    }
}
