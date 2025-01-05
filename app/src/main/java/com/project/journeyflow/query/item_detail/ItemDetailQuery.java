package com.project.journeyflow.query.item_detail;

import android.content.Context;

import com.project.journeyflow.database.TrackingData;
import com.project.journeyflow.query.ProfileFragmentQuery;

import io.realm.Realm;

public class ItemDetailQuery extends ProfileFragmentQuery {
    public ItemDetailQuery(Context context) {
        super(context);
    }

    public TrackingData getTrackingData(long id) {
        Realm realm = initializeRealm();

        TrackingData trackingList = realm.where(TrackingData.class)
                .equalTo("id", id)
                .findFirst();

        return trackingList;
    }
}
