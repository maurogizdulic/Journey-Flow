package com.project.journeyflow.query;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.project.journeyflow.database.TrackingData;
import com.project.journeyflow.database.User;

import io.realm.Realm;
import io.realm.RealmResults;

public class HistoryFragmentQuery extends Query{

    public HistoryFragmentQuery(Context context) {
        super(context);
    }

    public RealmResults<TrackingData> getAllJourneys() {
        User user = fetchUserData();
        Realm realm = initializeRealm();

        RealmResults<TrackingData> trackingList = realm.where(TrackingData.class).equalTo("userID", user.getId()).findAll();

        return trackingList;
    }
}
