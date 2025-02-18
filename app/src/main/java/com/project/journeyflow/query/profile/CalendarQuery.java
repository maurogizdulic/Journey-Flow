package com.project.journeyflow.query.profile;

import android.annotation.SuppressLint;
import android.content.Context;

import com.project.journeyflow.database.TrackingData;
import com.project.journeyflow.database.User;
import com.project.journeyflow.query.ProfileFragmentQuery;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

public class CalendarQuery extends ProfileFragmentQuery {

    public CalendarQuery(Context context) {
        super(context);
    }

    public RealmResults<TrackingData> getAllJourneys() {
        User user = fetchUserData();
        Realm realm = initializeRealm();

        return realm.where(TrackingData.class).equalTo("userID", user.getId()).findAll();
    }

    public RealmResults<TrackingData> getJourneysToday(Date startDate, Date endDate) {
        User user = fetchUserData();
        Realm realm = initializeRealm();

        return realm.where(TrackingData.class)
                .equalTo("userID", user.getId())
                .greaterThanOrEqualTo("journeyDate", startDate)
                .lessThan("journeyDate", endDate)
                .findAll();
    }


    @SuppressLint("NotifyDataSetChanged")
    public RealmResults<TrackingData> getJourneysOnDate(Date startDate, Date endDate) {

        User user = fetchUserData();
        Realm realm = initializeRealm();

        return realm.where(TrackingData.class)
                .equalTo("userID", user.getId())
                .greaterThanOrEqualTo("journeyDate", startDate)
                .lessThan("journeyDate", endDate) // Exclude the next day
                .findAll();
    }
}
