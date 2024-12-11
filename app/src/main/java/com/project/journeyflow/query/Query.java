package com.project.journeyflow.query;

import android.content.Context;
import android.widget.Toast;

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
}
