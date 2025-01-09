package com.project.journeyflow.location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.project.journeyflow.MainActivity;
import com.project.journeyflow.R;
import com.project.journeyflow.calculation.Calculation;
import com.project.journeyflow.database.GPSCoordinates;
import com.project.journeyflow.database.TrackingData;
import com.project.journeyflow.database.User;

import android.app.NotificationChannel;
import android.app.NotificationManager;

import org.osmdroid.util.GeoPoint;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;

public class TrackingService extends Service {

    private static final int NOTIFICATION_ID = 101;
    private static final String CHANNEL_ID = "tracking_channel";
    private static final String CHANNEL_NAME = "Tracking Notifications";
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private double calculatedDistance = 0;
    private Location lastLocation = null;
    public static final String ACTION_LOCATION_BROADCAST = "LOCATION_UPDATE";
    private static List<GeoPoint> geoPointList;
    private static List<Double> altitudeList, traveledDistanceList;
    private static List<Float> accuracyList, speedList;
    private static List<Date> dateList;
    private double traveledDistance = 0;
    public static boolean isTracking = false;
    private double measuredDistance = 0;

    @SuppressLint("ForegroundServiceType")
    @Override
    public void onCreate() {
        super.onCreate();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        geoPointList = new ArrayList<>();
        altitudeList = new ArrayList<>();
        accuracyList = new ArrayList<>();
        speedList = new ArrayList<>();
        traveledDistanceList = new ArrayList<>();
        dateList = new ArrayList<>();

        createLocationCallback();
        //startForeground(NOTIFICATION_ID, createNotification());

        // CREATING NOTIFICATION
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
        );
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        }
        startForeground(NOTIFICATION_ID, createNotification());

        startLocationUpdates();

        Log.d("START TRACKING SERVICE", "TRACKING SERVICE START");
    }

    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Log.d("LOCATION CHANGED", "LOCATION CHANGED");
                for (Location location : locationResult.getLocations()) {

                    measuredDistance = updatePath(location);
                    if (measuredDistance != 0) {
                        Date date = new Date();
                        Log.d("MEASURED DISTANCE", String.valueOf(updatePath(location)));
                        Log.d("MEASURED NEW DATE", String.valueOf(date));
                        traveledDistance += measuredDistance;
                        traveledDistanceList.add(traveledDistance);
                        geoPointList.add(new GeoPoint(location.getLatitude(), location.getLongitude()));
                        altitudeList.add(location.getAltitude());
                        speedList.add(location.getSpeed());
                        accuracyList.add(location.getAccuracy());
                        dateList.add(new Date());
                        Log.d("MEASURED NEW DATE LIST", String.valueOf(dateList));
                        //Log.d("POINT LIST VALUES", String.valueOf(trackingData.getPointList().size()));
                        // Here send to Tracking fragment
                        broadcastLocation(geoPointList, altitudeList, speedList, accuracyList, traveledDistance, traveledDistanceList);
                    }
                }
            }
        };
    }

    /**
     * Sends a list to an activity for display on an OSM map.
     * The list is received in the broadcast receiver in the onReceive method.
     *
     * @param geoPointList list of geographic points (latitude and longitude) that the user has passed since
     *                     the start of the path recording
     */
    private void broadcastLocation(List<GeoPoint> geoPointList, List<Double> altitudeList, List<Float> speedList, List<Float> accuracyList, double measuredDistance, List<Double> traveledDistanceList) {
        Intent intent = new Intent(ACTION_LOCATION_BROADCAST);
        intent.putExtra("GEOPOINT_LIST", (Serializable) geoPointList);
        intent.putExtra("ALTITUDE_LIST", (Serializable) altitudeList);
        intent.putExtra("ACCURACY_LIST", (Serializable) accuracyList);
        intent.putExtra("SPEED_LIST", (Serializable) speedList);
        intent.putExtra("TRAVELED_DISTANCE_LIST", (Serializable) traveledDistanceList);
        intent.putExtra("TRAVELED_DISTANCE", measuredDistance);
        sendBroadcast(intent);
    }

    private void startLocationUpdates() {
        Log.d("LOCATION UPDATED", "LOCATION UPDATED");
        /*LocationRequest locationRequest = LocationRequest.create()
                .setInterval(5000)  // Update interval
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

         */


         LocationRequest locationRequest = new LocationRequest.Builder(
                LocationRequest.PRIORITY_HIGH_ACCURACY, // Priority
                1500 // Interval in milliseconds
            )
            .setMinUpdateIntervalMillis(1000) // Optional: Minimum interval between updates
            .setMaxUpdateDelayMillis(2000)  // Optional: Maximum wait time for batching
            .build();


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    /**
     * The method that creates the notification
     * @return notification
     */
    private Notification createNotification() {
        // Build a notification for the foreground service
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Tracking Your Movement")
                .setContentText("The app is tracking your location.")
                .setSmallIcon(R.drawable.navigation_icon) // Replace with your icon resource
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true)  // Make the notification ongoing (i.e., can't be dismissed by the user)
                .setContentIntent(createContentIntent()) // Optional: Intent to open an activity when clicked
                .build();
    }

    /**
     * Method that is set in NotificationCompat.Builder in setContentIntent.
     * It is used in such a way that when the user presses the notification,
     * the application opens the main activity and forwards it to the tracking fragment
     * @return intent, opens MainActivity and forwards to tracking fragment
     */
    private PendingIntent createContentIntent() {
        // Create an intent that will be triggered when the notification is clicked
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("openFragment", "TrackingFragment"); // Modify to your target activity
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    /**
     * A method that receives the current location and calls the calculateDistance method,
     * passing it the current and previous locations in order to calculate the distance between them
     * @param location current location received from the service
     * @return calculated distance between two locations
     */
    private double updatePath(Location location) {

        if (lastLocation != null) {
            calculatedDistance = calculateDistance(lastLocation, location);
        }
        lastLocation = location;

        return calculatedDistance;
    }

    /**
     * A method that calculates the distance between two geographic locations
     * @param start Starting location
     * @param end Last location
     * @return distance between two geographic locations
     */
    private double calculateDistance(Location start, Location end) {
        float[] result = new float[1];
        Location.distanceBetween(start.getLatitude(), start.getLongitude(), end.getLatitude(), end.getLongitude(), result);

        // If calculated distance is less than 6m then it is considered a GPS error and return 0m
       /* if (result[0] <= 0.5) {
            Log.d("MEASURED DISTANCE", "LESS THAN 2m");
            return 0;
        }
        else {
            return result[0];
        }*/
        return result[0];
    }

    /**
     * Method that stores data locally using the Realm database
     * @param context context from fragment
     * @return true if the data was saved successfully or false if not
     */
    public static boolean saveData(Context context){
        Realm.init(context);
        RealmConfiguration config = new RealmConfiguration.Builder()
                // below line is to allow write
                // data to database on ui thread.
                .allowWritesOnUiThread(true)
                // below line is to delete realm
                // if migration is needed.
                .deleteRealmIfMigrationNeeded()
                // at last we are calling a build method to generate the configurations.
                .build();
        // on below line we are setting the default
        // configuration to our realm database.
        Realm.setDefaultConfiguration(config);
        Realm realm = Realm.getDefaultInstance();
       try {
           realm.executeTransaction(realm1 -> {
               SharedPreferences sharedPreferences = context.getSharedPreferences("AppPreferences", MODE_PRIVATE);
               long userID = sharedPreferences.getLong("id", 123456789);

               User user = realm1.where(User.class).equalTo("id", userID).findFirst();

               if (user != null) {
                   TrackingData trackingData = realm1.createObject(TrackingData.class);

                   RealmList<Double> traveledDistanceRealmList = new RealmList<>();
                   RealmList<Double> altitudeRealmList = new RealmList<>();
                   RealmList<Float> speedRealmList = new RealmList<>();
                   RealmList<GPSCoordinates> gpsCoordinatesRealmList = new RealmList<>();
                   RealmList<Date> dateRealmList = new RealmList<>();

                   traveledDistanceRealmList.addAll(traveledDistanceList);
                   altitudeRealmList.addAll(altitudeList);
                   speedRealmList.addAll(speedList);
                   dateRealmList.addAll(dateList);

                   for (GeoPoint geoPoint : geoPointList) {
                       GPSCoordinates gpsCoordinates = realm1.createObject(GPSCoordinates.class);
                       gpsCoordinates.setLatitude(geoPoint.getLatitude());
                       gpsCoordinates.setLongitude(geoPoint.getLongitude());
                       gpsCoordinatesRealmList.add(gpsCoordinates);
                   }



                   SecureRandom secureRandom = new SecureRandom();
                   trackingData.setId(secureRandom.nextLong());
                   trackingData.setTraveledDistanceList(traveledDistanceRealmList);
                   trackingData.setAltitudeList(altitudeRealmList);
                   trackingData.setSpeedList(speedRealmList);
                   trackingData.setGpsCoordinates(gpsCoordinatesRealmList);
                   trackingData.setDateTimeList(dateRealmList);
                   trackingData.setJourneyDate(dateRealmList.first());
                   trackingData.setUserID(userID);
                   trackingData.setDurationInSeconds(Calculation.calculateDurationOfJourneyInSeconds(Objects.requireNonNull(dateRealmList.first()), Objects.requireNonNull(dateRealmList.last())));
                   trackingData.setTotalDistance(traveledDistanceRealmList.last());

                   user.getTrackings().add(trackingData);
               } else {
                   Log.e("REALM USER", "USER NOT FOUND");
               }
           });

           realm.close();
           return true;
       }
       catch (Exception e) {
           Log.e("EXCEPTION E", "IN ON DESTROY" + e);
           return false;
       }
    }

    /**
     * Stops the service
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        fusedLocationClient.removeLocationUpdates(locationCallback);
        Log.d("STOP TRACKING SERVICE", "TRACKING SERVICE STOP");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
