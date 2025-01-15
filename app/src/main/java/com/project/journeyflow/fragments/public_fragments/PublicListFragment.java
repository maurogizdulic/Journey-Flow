package com.project.journeyflow.fragments.public_fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.journeyflow.R;
import com.project.journeyflow.database.GPSCoordinates;
import com.project.journeyflow.database.TrackingData;
import com.project.journeyflow.items.ItemAdapterPublic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import io.realm.RealmList;

public class PublicListFragment extends Fragment {

    private ItemAdapterPublic adapter;
    private FloatingActionButton fabFilterPublic;
    private TextView textViewMessage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_public_list, container, false);

        fabFilterPublic = view.findViewById(R.id.fabPublicFilter);
        textViewMessage = view.findViewById(R.id.textViewPublicMessage);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewPublic);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        showPublicJourneys(recyclerView, fabFilterPublic);

        return view;
    }

    public void showPublicJourneys(RecyclerView recyclerView, FloatingActionButton fabFilterPublic) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("journeys").get().addOnSuccessListener(queryDocumentSnapshots -> {
            RealmList<TrackingData> trackingDataList = new RealmList<>();

            for (DocumentSnapshot document : queryDocumentSnapshots) {
                if (Boolean.TRUE.equals(document.getBoolean("public"))) {
                    TrackingData trackingData = new TrackingData();
                    trackingData.setId(Long.parseLong(document.getId()));

                    // Convert to ArrayList to RealmList
                    ArrayList<Double> arrayList = (ArrayList<Double>) document.get("traveledDistanceList");
                    if (arrayList != null) {
                        // Convert ArrayList to RealmList
                        RealmList<Double> realmList = new RealmList<>();
                        realmList.addAll(arrayList);

                        // Set the converted RealmList
                        trackingData.setTraveledDistanceList(realmList);
                    }

                    // Convert to ArrayList to RealmList
                    arrayList = (ArrayList<Double>) document.get("altitudeList");
                    if (arrayList != null) {
                        // Convert ArrayList to RealmList
                        RealmList<Double> realmList = new RealmList<>();
                        realmList.addAll(arrayList);

                        // Set the converted RealmList
                        trackingData.setAltitudeList(realmList);
                    }

                    // Convert to ArrayList to RealmList
                    ArrayList<Float> speedArrayList = (ArrayList<Float>) document.get("speedList");
                    if (speedArrayList != null) {
                        // Convert ArrayList to RealmList
                        RealmList<Float> realmList = new RealmList<>();
                        realmList.addAll(speedArrayList);

                        // Set the converted RealmList
                        trackingData.setSpeedList(realmList);
                    }

                    // Convert Timestamp list to RealmList<Date>
                    List<Timestamp> journeyDates = (List<Timestamp>) document.get("dateTimeList");

                    Log.d("JOURNEY DATES", String.valueOf(journeyDates));
                    if (journeyDates != null) {
                        RealmList<Date> realmList = new RealmList<>();

                        // Convert each Timestamp to Date
                        for (Timestamp timestamp : journeyDates) {
                            if (timestamp != null) {
                                realmList.add(timestamp.toDate()); // Convert to Date and add to list
                            }
                        }

                        Log.d("REALM LIST", String.valueOf(realmList));
                        trackingData.setDateTimeList(realmList);
                    }

                    // Convert Timestamp to Date
                    Timestamp timestamp = (Timestamp) document.get("journeyDate");
                    Log.d("FETCHED TIMESTAMP", String.valueOf(timestamp));
                    if (timestamp != null) {
                        Date date = timestamp.toDate();
                        Log.d("CONVERT TIMESTAMP to DATE", String.valueOf(date));
                        trackingData.setJourneyDate(date);
                    }

                    trackingData.setUserID((document.getLong("ownerId")));
                    trackingData.setDurationInSeconds(document.getLong("durationInSeconds"));
                    trackingData.setTotalDistance(document.getDouble("totalDistance"));

                    List<Double> longitude = (List<Double>) document.get("gpsCoordinatesLongitude");
                    List<Double> latitude = (List<Double>) document.get("gpsCoordinatesLatitude");

                    RealmList<GPSCoordinates> gpsCoordinatesList = new RealmList<>();
                    for (int i = 0; i < Objects.requireNonNull(longitude).size(); i++) {
                        GPSCoordinates gpsCoordinates = new GPSCoordinates(Objects.requireNonNull(latitude).get(i), longitude.get(i));
                        gpsCoordinatesList.add(gpsCoordinates);
                    }

                    trackingData.setGpsCoordinates(gpsCoordinatesList);
                    trackingData.setUsername(document.getString("ownerUsername"));

                    trackingDataList.add(trackingData);
                }
            }

            adapter = new ItemAdapterPublic(trackingDataList, getParentFragmentManager());
            recyclerView.setAdapter(adapter);

            if (adapter.getItemCount() == 0) {
                fabFilterPublic.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);

                textViewMessage.setVisibility(View.VISIBLE);
            }
            else {
                fabFilterPublic.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);

                textViewMessage.setVisibility(View.GONE);
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Failed to fetch public journeys", Toast.LENGTH_LONG).show();
        });
    }
}
