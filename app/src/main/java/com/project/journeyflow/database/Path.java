package com.project.journeyflow.database;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Path extends RealmObject {

    RealmList<GPSCoordinates> points = new RealmList<>();

    public RealmList<GPSCoordinates> getPoints() {
        return points;
    }
}
