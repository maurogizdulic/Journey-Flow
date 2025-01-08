package com.project.journeyflow.query;

import android.content.Context;

import com.project.journeyflow.database.User;

import io.realm.Realm;

public class LogInSignUpQuery extends Query{

    public LogInSignUpQuery(Context context) {
        super(context);
    }

    public User checkAccount(String email, String password) {
        Realm realm = initializeRealm();

        return realm.where(User.class).equalTo("eMail", email).equalTo("password", password).findFirst();
    }

    public User checkEmail(String email) {
        Realm realm = initializeRealm();

        return realm.where(User.class).equalTo("eMail", email).findFirst();
    }

    public boolean currentPasswordExists(String currentPassword) {
        User user = fetchUserData();

        if (user.getPassword().equals(currentPassword)) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean newEmailExists(String newEmail) {
        Realm realm = initializeRealm();

        User user = realm.where(User.class).equalTo("eMail", newEmail).findFirst();

        if (user != null) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean currentEmailExists(String currentEmail) {
        User user = fetchUserData();

        if (user.geteMail().equals(currentEmail)) {
            return true;
        }
        else {
            return false;
        }
    }
}
