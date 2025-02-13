package com.project.journeyflow.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.osmdroid.util.GeoPoint;

import java.util.List;

public class LocationBroadcastReceiver extends BroadcastReceiver {

    private LocationViewModel locationViewModel;

    // Prazan konstruktor potreban za refleksiju
    public LocationBroadcastReceiver() {
    }

    public LocationBroadcastReceiver(LocationViewModel viewModel) {
        this.locationViewModel = viewModel;
    }

    /**
     * Method that receives data from the tracking service (location, speed, distance traveled and altitude).
     * Stores data received from the service in the locationViewModel.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (TrackingService.ACTION_LOCATION_BROADCAST.equals(intent.getAction())) {
            List<GeoPoint> geoPointList = (List<GeoPoint>) intent.getSerializableExtra("GEOPOINT_LIST");
            List<Double> altitudeList = (List<Double>) intent.getSerializableExtra("ALTITUDE_LIST");
            List<Float> speedList = (List<Float>) intent.getSerializableExtra("SPEED_LIST");
            List<Float> accuracyList = (List<Float>) intent.getSerializableExtra("ACCURACY_LIST");
            double distance = intent.getDoubleExtra("TRAVELED_DISTANCE", 0);


            // Add the list to the ViewModel (or handle it in another way)
            if (geoPointList != null) {
                // Example: Send it to the ViewModel or process it
                locationViewModel.setGeoPoints(geoPointList);  // assuming myViewModel is your ViewModel instance
                locationViewModel.setAltitudeList(altitudeList);
                locationViewModel.setAccuracyList(accuracyList);
                locationViewModel.setSpeedList(speedList);
                locationViewModel.setMeasuredDistance(distance);
            }

            Log.d("LOCATION RECEIVED", "LOCATION RECEIVED");
        }
    }
}
