package com.project.journeyflow.query.profile;

import android.content.Context;

import com.github.mikephil.charting.data.BarEntry;
import com.project.journeyflow.database.TrackingData;
import com.project.journeyflow.database.User;
import com.project.journeyflow.query.ProfileFragmentQuery;

import java.util.Date;
import java.util.List;

public class StatisticsQuery extends ProfileFragmentQuery {

    private final Context context;

    public StatisticsQuery(Context context) {
        super(context);
        this.context = context;
    }

    /*
    public List<BarEntry> getListEntriesForNumOfJourneys() {
        User user = fetchUserData();

        if (user != null) {
            List<TrackingData> trackingDataList = user.getTrackings();

            if (!trackingDataList.isEmpty()) {

                for (TrackingData trackingData : trackingDataList) {

                }
            }
        }

    }

    public List<BarEntry> getListEntriesForDuration() {
        User user = fetchUserData();

    }

    public List<BarEntry> getListEntriesForDistance() {

    }

     */
}
