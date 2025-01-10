package com.project.journeyflow.query.item_detail;

import android.content.Context;

import com.project.journeyflow.database.TrackingData;
import com.project.journeyflow.database.User;
import com.project.journeyflow.query.ProfileFragmentQuery;

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

    public String getUserOfJourney(TrackingData trackingData) {
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
}
