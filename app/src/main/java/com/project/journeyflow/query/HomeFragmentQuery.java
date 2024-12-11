package com.project.journeyflow.query;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.project.journeyflow.database.User;

import io.realm.Realm;

public class HomeFragmentQuery extends Query {

    public HomeFragmentQuery(Context context) {
        super(context);
    }

    public User fetchUserData() {
        Realm realm = initializeRealm();
        if (realm != null) {
            // Declare a user variable to hold the result
            User user = realm.where(User.class).equalTo("id", 1).findFirst();
            Log.e("USER DATA", String.valueOf(user));

            if (user != null && !user.getTrackings().isEmpty()) {
                // Make a copy of the user object if needed (Realm objects are live and cannot be used outside Realm)
                return realm.copyFromRealm(user);
            } else {
                Toast.makeText(getContext(), "Tracking is empty or user is null", Toast.LENGTH_LONG).show();
            }
        }
        return null;
    }
}
