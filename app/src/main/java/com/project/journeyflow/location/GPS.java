package com.project.journeyflow.location;

import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Looper;

public class GPS {

    private Context context;
    private final LocationManager locationManager;
    private LocationListener locationListener;

    public GPS(Context context) {
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    // Listening GPS location every second
    public void startListening(LocationListener listener) {
        locationListener = listener;
        if (locationManager != null) {
            try {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, listener, Looper.getMainLooper());
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isProviderEnabled(){
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
}
