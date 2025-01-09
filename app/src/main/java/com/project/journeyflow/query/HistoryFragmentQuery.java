package com.project.journeyflow.query;

import android.annotation.SuppressLint;
import android.content.Context;

import com.project.journeyflow.database.TrackingData;
import com.project.journeyflow.database.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmResults;

public class HistoryFragmentQuery extends Query{

    public HistoryFragmentQuery(Context context) {
        super(context);
    }

    public RealmResults<TrackingData> getAllJourneys() {
        User user = fetchUserData();
        Realm realm = initializeRealm();

        return realm.where(TrackingData.class).equalTo("userID", user.getId()).findAll();
    }

    public RealmResults<TrackingData> getFilteredData(String startDate, String endDate, double minDistance, double maxDistance, int minDuration, int maxDuration) {
        Realm realm = initializeRealm();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat inputFormat = new SimpleDateFormat("dd.MM.yyyy");
        try {
            Date start = inputFormat.parse(startDate);
            Date end = inputFormat.parse(endDate);

            end = new Date(Objects.requireNonNull(end).getTime() + (24 * 60 * 60 * 1000) - 1); // Adjust to include full day

            return realm.where(TrackingData.class)
                    .greaterThanOrEqualTo("journeyDate", Objects.requireNonNull(start))
                    .lessThanOrEqualTo("journeyDate", end)
                    .greaterThanOrEqualTo("durationInSeconds", minDuration)
                    .lessThanOrEqualTo("durationInSeconds", maxDuration)
                    .greaterThanOrEqualTo("totalDistance", minDistance)
                    .lessThanOrEqualTo("totalDistance", maxDistance)
                    .findAll();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
