package com.project.journeyflow.database;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;

public class TrackingData extends RealmObject {
    private RealmList<Double> traveledDistanceList;
    private RealmList<Double> altitudeList;
    private RealmList<Float> speedList;
    private RealmList<GPSCoordinates> gpsCoordinates;
    private RealmList<Date> dateTimeList;

    // Empty constructor
    public TrackingData() {}

    public RealmList<Double> getTraveledDistanceList() {
        return traveledDistanceList;
    }

    public void setTraveledDistanceList(RealmList<Double> traveledDistanceList) {
        this.traveledDistanceList = traveledDistanceList;
    }

    public RealmList<Double> getAltitudeList() {
        return altitudeList;
    }

    public void setAltitudeList(RealmList<Double> altitudeList) {
        this.altitudeList = altitudeList;
    }

    public RealmList<Float> getSpeedList() {
        return speedList;
    }

    public void setSpeedList(RealmList<Float> speedList) {
        this.speedList = speedList;
    }

    public RealmList<GPSCoordinates> getGpsCoordinates() {
        return gpsCoordinates;
    }

    public void setGpsCoordinates(RealmList<GPSCoordinates> gpsCoordinates) {
        this.gpsCoordinates = gpsCoordinates;
    }

    public RealmList<Date> getDateTimeList() {
        return dateTimeList;
    }

    public void setDateTimeList(RealmList<Date> dateTimeList) {
        this.dateTimeList = dateTimeList;
    }
}
