package com.project.journeyflow.query.item_detail;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.project.journeyflow.database.GPSCoordinates;
import com.project.journeyflow.database.TrackingData;
import com.project.journeyflow.database.User;
import com.project.journeyflow.query.ProfileFragmentQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;

public class ItemDetailQuery extends ProfileFragmentQuery {
    public ItemDetailQuery(Context context) {
        super(context);
    }

    public TrackingData getTrackingData(long id) {
        Realm realm = initializeRealm();

        return realm.where(TrackingData.class)
                .equalTo("id", id)
                .findFirst();
    }

    public String getUserOfJourneyInString(TrackingData trackingData) {
        long userID = trackingData.getUserID();

        Realm realm = initializeRealm();

        User user = realm.where(User.class).equalTo("id", userID).findFirst();

        realm.close();

        if (user != null){
            return user.getUsername();
        }
        else {
            return "User";
        }

    }

    public User getUserOfJourney(TrackingData trackingData) {
        long userID = trackingData.getUserID();

        Realm realm = initializeRealm();

        User user = realm.where(User.class).equalTo("id", userID).findFirst();

        realm.close();

        if (user != null){
            return user;
        }
        else {
            return null;
        }
    }

    public boolean isOwner(TrackingData trackingData) {
        User user = fetchUserData();
        long userID = trackingData.getUserID();

        return user.getId() == userID;
    }

    public boolean deleteJourney(TrackingData trackingData) {

        try (Realm realm = initializeRealm()) {
            realm.executeTransaction(r -> {
                TrackingData data = r.where(TrackingData.class)
                        .equalTo("id", trackingData.getId())
                        .findFirst();

                if (data != null) {
                    data.deleteFromRealm();
                }
            });

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Close the Realm instance

        return false;
    }

    public void shareJourneyToGroup(TrackingData trackingData, List<String> listOfUsers) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        User user = fetchUserData();

        List<Double> gpsCoordinatesListLongitude = new ArrayList<>();
        List<Double> gpsCoordinatesListLatitude = new ArrayList<>();

        for (GPSCoordinates gpsCoordinate : trackingData.getGpsCoordinates()) {
            gpsCoordinatesListLongitude.add(gpsCoordinate.getLongitude());
            gpsCoordinatesListLatitude.add(gpsCoordinate.getLatitude());
        }

        Map<String, Object> journeyData = new HashMap<>();
        journeyData.put("ownerId", user.getId());
        journeyData.put("ownerUsername", user.getUsername());
        journeyData.put("public", false);
        journeyData.put("sharedTo", listOfUsers);

        journeyData.put("traveledDistanceList", trackingData.getTraveledDistanceList());
        journeyData.put("altitudeList", trackingData.getAltitudeList());
        journeyData.put("speedList", trackingData.getSpeedList());
        journeyData.put("gpsCoordinatesLatitude", gpsCoordinatesListLatitude);
        journeyData.put("gpsCoordinatesLongitude", gpsCoordinatesListLongitude);
        journeyData.put("dateTimeList", trackingData.getDateTimeList());
        journeyData.put("journeyDate", trackingData.getJourneyDate());
        journeyData.put("durationInSeconds", trackingData.getDurationInSeconds());
        journeyData.put("totalDistance", trackingData.getTotalDistance());

        db.collection("journeys").document(String.valueOf(trackingData.getId()))
                .set(journeyData).addOnSuccessListener(unused -> {
                    Toast.makeText(getContext(), "Successful shared journey", Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Unsuccessful shared journey", Toast.LENGTH_LONG).show();
                });
    }

    public void shareJourneyToAll(TrackingData trackingData) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        User user = fetchUserData();

        List<Double> gpsCoordinatesListLongitude = new ArrayList<>();
        List<Double> gpsCoordinatesListLatitude = new ArrayList<>();

        for (GPSCoordinates gpsCoordinate : trackingData.getGpsCoordinates()) {
            gpsCoordinatesListLongitude.add(gpsCoordinate.getLongitude());
            gpsCoordinatesListLatitude.add(gpsCoordinate.getLatitude());
        }

        Map<String, Object> journeyData = new HashMap<>();
        journeyData.put("ownerId", user.getId());
        journeyData.put("ownerUsername", user.getUsername());
        journeyData.put("public", true);
        journeyData.put("sharedTo", null);

        journeyData.put("traveledDistanceList", trackingData.getTraveledDistanceList());
        journeyData.put("altitudeList", trackingData.getAltitudeList());
        journeyData.put("speedList", trackingData.getSpeedList());
        journeyData.put("gpsCoordinatesLatitude", gpsCoordinatesListLatitude);
        journeyData.put("gpsCoordinatesLongitude", gpsCoordinatesListLongitude);
        journeyData.put("dateTimeList", trackingData.getDateTimeList());
        journeyData.put("journeyDate", trackingData.getJourneyDate());
        journeyData.put("durationInSeconds", trackingData.getDurationInSeconds());
        journeyData.put("totalDistance", trackingData.getTotalDistance());

        db.collection("journeys").document(String.valueOf(trackingData.getId()))
                .set(journeyData).addOnSuccessListener(unused -> {
                    Toast.makeText(getContext(), "Successful shared journey", Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Unsuccessful shared journey", Toast.LENGTH_LONG).show();
                });
    }
}
