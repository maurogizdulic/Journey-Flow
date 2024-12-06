package com.project.journeyflow.location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class LocationViewModel extends ViewModel {

    private final MutableLiveData<List<GeoPoint>> pointsList = new MutableLiveData<>();
    private final MutableLiveData<List<Double>> altitudeList = new MutableLiveData<>();
    private final MutableLiveData<List<Float>> speedList = new MutableLiveData<>();
    private final MutableLiveData<List<Float>> accuracyList = new MutableLiveData<>();
    private double measuredDistance = 0;

    public LiveData<List<GeoPoint>> getPointsList() {
        return pointsList;
    }

    // Method to set the list of GeoPoints
    public void setGeoPoints(List<GeoPoint> geoPointsList) {
        pointsList.setValue(geoPointsList);
    }

    public LiveData<List<Double>> getAltitudeList() {
        return altitudeList;
    }

    public void setAltitudeList(List<Double> altitudeList){
        this.altitudeList.setValue(altitudeList);
    }

    public LiveData<List<Float>> getSpeedList() {
        return speedList;
    }

    public void setSpeedList(List<Float> speedList){
        this.speedList.setValue(speedList);
    }

    public LiveData<List<Float>> getAccuracyList() {
        return accuracyList;
    }

    public void setAccuracyList(List<Float> accuracyList){
        this.accuracyList.setValue(accuracyList);
    }

    public void clearPointsList() {
        pointsList.setValue(new ArrayList<>());
    }

    public double getMeasuredDistance() {
        return measuredDistance;
    }

    public void setMeasuredDistance(double measuredDistance) {
        this.measuredDistance = measuredDistance;
    }
}
