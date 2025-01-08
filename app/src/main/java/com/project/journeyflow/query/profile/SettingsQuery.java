package com.project.journeyflow.query.profile;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.project.journeyflow.database.TrackingData;
import com.project.journeyflow.database.User;
import com.project.journeyflow.query.ProfileFragmentQuery;

import io.realm.Realm;
import io.realm.RealmResults;

public class SettingsQuery extends ProfileFragmentQuery {

    private final Context context;
    public SettingsQuery(Context context) {
        super(context);
        this.context = context;
    }
/*
    public void changePassword(){

    }

    public void changeEmail() {

    }

 */

    public void deleteAccount() {

        try (Realm realm = initializeRealm()) {
            // Start a transaction
            realm.executeTransaction(r -> {

                SharedPreferences sharedPreferences = context.getSharedPreferences("AppPreferences", MODE_PRIVATE);
                long userID = sharedPreferences.getLong("id", 123456789);
                User user = r.where(User.class).equalTo("id", userID).findFirst();

                if (user != null) {
                    // Delete all trackings associated with the user
                    RealmResults<TrackingData> userTrackings = r.where(TrackingData.class)
                            .equalTo("userID", user.getId()) // Assuming Tracking has a userId field
                            .findAll();
                    userTrackings.deleteAllFromRealm();

                    // Delete the user
                    user.deleteFromRealm();

                    Toast.makeText(context, "Account deleted successfully", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "User not found, account deletion failed", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
