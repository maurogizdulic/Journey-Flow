package com.project.journeyflow.fragments.public_fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.project.journeyflow.calculation.Calculation;
import com.project.journeyflow.database.GPSCoordinates;
import com.project.journeyflow.database.TrackingData;
import com.project.journeyflow.items.ItemAdapterPublic;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import io.realm.RealmList;

public class PublicListFragment extends Fragment {

    private ItemAdapterPublic adapter;
    private TextView textViewMessage;

    private double minDistance = 0;
    private double maxDistance = 9999;
    private int minDuration = 0;
    private int maxDuration = 9999;

    private final LocalDateTime startLocalDateTime = LocalDateTime.of(1950, 1, 1, 0, 0);
    private Date sDate = Date.from(startLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());

    // Create endDate for December 31, 2025
    private final LocalDateTime endLocalDateTime = LocalDateTime.of(2025, 12, 31, 23, 59, 59);
    private Date eDate = Date.from(endLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_public_list, container, false);

        FloatingActionButton fabFilterPublic = view.findViewById(R.id.fabPublicFilter);
        textViewMessage = view.findViewById(R.id.textViewPublicMessage);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewPublic);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        showPublicJourneys(recyclerView, fabFilterPublic);

        fabFilterPublic.setOnClickListener(view1 -> {
            if (!isInternetAvailable(requireActivity())) {
                dialogInternetConnection(requireActivity(), "No Internet Connection", "To be able to filter journeys you need to be connected to the internet.");
            }
            else {
                showFilterDialog(recyclerView);
            }
        });

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

    public void showFilterDialog(RecyclerView recyclerView) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_history_filter, null);

        ImageButton imageButtonStartDate = dialogView.findViewById(R.id.imageButtonPickStartDate);
        ImageButton imageButtonEndDate = dialogView.findViewById(R.id.imageButtonPickEndDate);
        EditText editTextDistanceFrom = dialogView.findViewById(R.id.editTextDistanceFrom);
        EditText editTextDistanceTo = dialogView.findViewById(R.id.editTextDistanceTo);
        EditText editTextDurationFrom = dialogView.findViewById(R.id.editTextDurationFrom);
        EditText editTextDurationTo = dialogView.findViewById(R.id.editTextDurationTo);
        TextView textViewStartDate = dialogView.findViewById(R.id.textViewStartDate);
        TextView textViewEndDate = dialogView.findViewById(R.id.textViewEndDate);
        Button buttonExitFilter = dialogView.findViewById(R.id.buttonExitFilter);
        Button buttonResetFilter = dialogView.findViewById(R.id.buttonResetFilter);
        ImageButton imageButtonSubmitFilter = dialogView.findViewById(R.id.imageButtonSubmitFilter);

        buttonExitFilter.setBackgroundColor(Color.TRANSPARENT);
        buttonResetFilter.setBackgroundColor(Color.TRANSPARENT);

        // Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        setInitialValues(textViewStartDate, textViewEndDate, editTextDistanceFrom, editTextDistanceTo, editTextDurationFrom, editTextDurationTo);

        imageButtonStartDate.setOnClickListener(view -> {
            final Calendar c = Calendar.getInstance();

            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // on below line we are creating a variable for date picker dialog.
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    // on below line we are passing context.
                    requireActivity(),
                    new DatePickerDialog.OnDateSetListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            // on below line we are setting date to our text view.
                            textViewStartDate.setText(dayOfMonth + "." + (monthOfYear + 1) + "." + year + ".");
                        }
                    },
                    // on below line we are passing year,
                    // month and day for selected date in our date picker.
                    year, month, day);
            // at last we are calling show to
            // display our date picker dialog.
            datePickerDialog.show();
        });

        imageButtonEndDate.setOnClickListener(view -> {
            final Calendar c = Calendar.getInstance();

            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // on below line we are creating a variable for date picker dialog.
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    // on below line we are passing context.
                    requireActivity(),
                    new DatePickerDialog.OnDateSetListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            // on below line we are setting date to our text view.
                            textViewEndDate.setText(dayOfMonth + "." + (monthOfYear + 1) + "." + year + ".");
                        }
                    },
                    // on below line we are passing year,
                    // month and day for selected date in our date picker.
                    year, month, day);
            // at last we are calling show to
            // display our date picker dialog.
            datePickerDialog.show();
        });

        imageButtonSubmitFilter.setOnClickListener(view -> {

            if (!editTextDistanceFrom.getText().toString().isEmpty()) {
                minDistance = Double.parseDouble(editTextDistanceFrom.getText().toString());
            }

            if (!editTextDistanceTo.getText().toString().isEmpty()) {
                maxDistance = Double.parseDouble(editTextDistanceTo.getText().toString());
            }

            if (!editTextDurationFrom.getText().toString().isEmpty()) {
                minDuration = Integer.parseInt(editTextDurationFrom.getText().toString());
            }

            if (!editTextDurationTo.getText().toString().isEmpty()) {
                maxDuration = Integer.parseInt(editTextDurationTo.getText().toString());
            }

            boolean isCorrect = true;

            if (!Calculation.correctOrderStartEndDate(textViewStartDate.getText().toString(), textViewEndDate.getText().toString())) {
                isCorrect = false;
                Toast.makeText(requireActivity(), "Incorrectly entered start and end date", Toast.LENGTH_LONG).show();
            }

            if (!Calculation.correctOrderMinMaxDistance(minDistance, maxDistance)) {
                isCorrect = false;
                Toast.makeText(requireActivity(), "Incorrectly entered min and max distance", Toast.LENGTH_LONG).show();
            }

            if (!Calculation.correctOrderMinMaxDuration(minDuration, maxDuration)) {
                isCorrect = false;
                Toast.makeText(requireActivity(), "Incorrectly entered min and max duration", Toast.LENGTH_LONG).show();
            }

            // Get filtered data
            if (isCorrect) {
                Log.d("ENTERED IN isCorrect", "ENTERED IN isCorrect");

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("journeys")
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (queryDocumentSnapshots != null) {
                                RealmList<TrackingData> trackingDataList = new RealmList<>();

                                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                                try {
                                    Date startDate = dateFormat.parse(textViewStartDate.getText().toString());
                                    Date endDate = dateFormat.parse(textViewEndDate.getText().toString());

                                    // Create start date (00:00:00)
                                    Calendar startCalendar = Calendar.getInstance();
                                    startCalendar.setTime(Objects.requireNonNull(startDate));
                                    startCalendar.set(Calendar.HOUR_OF_DAY, 0);
                                    startCalendar.set(Calendar.MINUTE, 0);
                                    startCalendar.set(Calendar.SECOND, 0);
                                    startCalendar.set(Calendar.MILLISECOND, 0);
                                    Timestamp startTimestamp = new Timestamp(startCalendar.getTime());

                                    // Create end date (23:59:59)
                                    Calendar endCalendar = Calendar.getInstance();
                                    endCalendar.setTime(endDate);
                                    endCalendar.set(Calendar.HOUR_OF_DAY, 23);
                                    endCalendar.set(Calendar.MINUTE, 59);
                                    endCalendar.set(Calendar.SECOND, 59);
                                    endCalendar.set(Calendar.MILLISECOND, 999);
                                    Timestamp endTimestamp = new Timestamp(endCalendar.getTime());

                                    for (DocumentSnapshot document : queryDocumentSnapshots) {

                                        if ((document.getDouble("totalDistance") / 1000 >= minDistance && document.getDouble("totalDistance") / 1000 <= maxDistance)
                                                && (document.getDouble("durationInSeconds") / 60 >= minDuration && document.getDouble("durationInSeconds") / 60 < maxDuration)
                                                && (document.getTimestamp("journeyDate").compareTo(startTimestamp) > 0 && document.getTimestamp("journeyDate").compareTo(endTimestamp) < 0)) {
                                            Log.d("Firestore", document.getId() + " => " + document.getData());
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
                                    }
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }

                                adapter = new ItemAdapterPublic(trackingDataList, getParentFragmentManager());
                                recyclerView.setAdapter(adapter);

                                dialog.dismiss();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e("ERROR FIREBASE", String.valueOf(e));
                            Toast.makeText(requireActivity(), "Filter error in firebase", Toast.LENGTH_LONG).show();
                        });
            }
        });


        buttonResetFilter.setOnClickListener(view -> {
            setInitialValues(textViewStartDate, textViewEndDate, editTextDistanceFrom, editTextDistanceTo, editTextDurationFrom, editTextDurationTo);
        });

        buttonExitFilter.setOnClickListener(view -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    @SuppressLint("DefaultLocale")
    private void setInitialValues(TextView startDate, TextView endDate, EditText distanceFrom, EditText distanceTo, EditText durationFrom, EditText durationTo) {

        sDate = Date.from(startLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());
        eDate = Date.from(endLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());
        minDistance = 0;
        maxDistance = 999999999;
        minDuration = 0;
        maxDuration = 999999999;

        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        String formattedStartDate = formatter.format(sDate);
        String formattedEndDate = formatter.format(eDate);

        startDate.setText(formattedStartDate);
        endDate.setText(formattedEndDate);

        distanceFrom.setHint(String.valueOf(minDistance));
        distanceFrom.setText("");
        distanceTo.setHint(String.valueOf(maxDistance));
        distanceTo.setText("");

        durationFrom.setHint(String.valueOf(minDuration));
        durationFrom.setText("");
        durationTo.setHint(String.valueOf(maxDuration));
        durationTo.setText("");
    }

    private void dialogInternetConnection(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Settings", (dialog, which) -> {
            // Open network settings
            context.startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private boolean isInternetAvailable(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }
}
