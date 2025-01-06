package com.project.journeyflow.query;

import android.content.Context;

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

        return realm.where(TrackingData.class).equalTo("userID", user.getId()).findAll();
    }
}
