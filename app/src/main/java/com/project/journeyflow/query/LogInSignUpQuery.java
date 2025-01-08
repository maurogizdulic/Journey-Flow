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
}
