package com.project.journeyflow.query;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.project.journeyflow.database.User;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class Query {

    private Context context;

    public Query(Context context){
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    protected Realm initializeRealm() {
        try {
            Realm.init(getContext());
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
            return Realm.getDefaultInstance();
        }
        catch (Exception e){
            Toast.makeText(context, "Error while initializing Realm", Toast.LENGTH_LONG).show();
            return null;
        }
    }

    protected User fetchUserData() {
        Realm realm = initializeRealm();

        SharedPreferences sharedPreferences = context.getSharedPreferences("AppPreferences", MODE_PRIVATE);
        long userID = sharedPreferences.getLong("id", 123456789);
        Log.d("SHARED PREFERENCES ID", String.valueOf(userID));

        if (realm != null) {
            // Declare a user variable to hold the result
            User user = realm.where(User.class).equalTo("id", userID).findFirst();
            Log.e("USER DATA", String.valueOf(user));

            if (user != null) {
                // Make a copy of the user object if needed (Realm objects are live and cannot be used outside Realm)
                return realm.copyFromRealm(user);
            } else {
                Toast.makeText(getContext(), "Tracking is empty or user is null", Toast.LENGTH_LONG).show();
            }
        }
        return null;
    }
}
