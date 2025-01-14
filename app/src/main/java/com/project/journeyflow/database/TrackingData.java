package com.project.journeyflow.database;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;

public class TrackingData extends RealmObject {
    private long id;
    private RealmList<Double> traveledDistanceList;
    private RealmList<Double> altitudeList;
    private RealmList<Float> speedList;
    private RealmList<GPSCoordinates> gpsCoordinates;
    private RealmList<Date> dateTimeList;
    private Date journeyDate;
    private long userID;
    private long durationInSeconds;
    private double totalDistance;
    private boolean publicValue;
    private String username;

    // Empty constructor
    public TrackingData() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

    public Date getJourneyDate() {
        return journeyDate;
    }

    public void setJourneyDate(Date journeyDate) {
        this.journeyDate = journeyDate;
    }

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    public long getDurationInSeconds() {
        return durationInSeconds;
    }

    public void setDurationInSeconds(long durationInSeconds) {
        this.durationInSeconds = durationInSeconds;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(double totalDIstance) {
        this.totalDistance = totalDIstance;
    }

    public boolean getPublicValue() {
        return publicValue;
    }

    public void setPublicValue(boolean publicValue) {
        this.publicValue = publicValue;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
