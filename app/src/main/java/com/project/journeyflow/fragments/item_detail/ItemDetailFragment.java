package com.project.journeyflow.fragments.item_detail;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.journeyflow.R;
import com.project.journeyflow.calculation.Calculation;
import com.project.journeyflow.database.GPSCoordinates;
import com.project.journeyflow.database.TrackingData;
import com.project.journeyflow.database.User;
import com.project.journeyflow.fragments.HistoryFragment;
import com.project.journeyflow.items.TabPagerAdapter;
import com.project.journeyflow.query.item_detail.ItemDetailQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import io.realm.RealmList;

public class ItemDetailFragment extends Fragment {

    private TextView textViewItemDetailUser, textViewItemDetailDistance, textViewItemDetailTime, textViewItemDetailAvgSpeed;
    private TextView textViewItemDetailStart, textViewItemDetailStop, textViewItemDetailAltitudeMax, textViewItemDetailAltitudeMin;
    private FloatingActionButton fabShare, fabDelete, fabView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_item_detail, container, false);

        initializeViews(view);

        if (getArguments() != null) {
            ItemDetailQuery query = new ItemDetailQuery(requireContext());
            long trackingDataId = getArguments().getLong("trackingDataId");
            boolean isPublic = getArguments().getBoolean("public");
            TrackingData trackingData = query.getTrackingData(trackingDataId);

            showJourneyDetails(view, trackingDataId, isPublic);

            if (query.isOwner(trackingData)) {
                fabView.setVisibility(View.VISIBLE);
                fabDelete.setVisibility(View.VISIBLE);
                fabShare.setVisibility(View.VISIBLE);
            }
            else {
                fabView.setVisibility(View.GONE);
                fabDelete.setVisibility(View.GONE);
                fabShare.setVisibility(View.GONE);
            }

            fabDelete.setOnClickListener(v -> {
                showDialogForDeleteJourney(query, trackingData);
            });

            fabShare.setOnClickListener(v -> {
                showDialogForShareJourney(query, trackingData);
            });

            fabView.setOnClickListener(v -> {
                showJourneyVisibility(query, trackingData);
            });
        }

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    private void initializeViews(View view) {
        textViewItemDetailUser = view.findViewById(R.id.textViewItemDetailUser);
        textViewItemDetailDistance = view.findViewById(R.id.textViewItemDetailDistance);
        textViewItemDetailTime = view.findViewById(R.id.textViewItemDetailTime);
        textViewItemDetailAvgSpeed = view.findViewById(R.id.textViewItemDetailAvgSpeed);
        textViewItemDetailStart = view.findViewById(R.id.textViewItemDetailStart);
        textViewItemDetailStop = view.findViewById(R.id.textViewItemDetailStop);
        textViewItemDetailAltitudeMax = view.findViewById(R.id.textViewItemDetailAltitudeMax);
        textViewItemDetailAltitudeMin = view.findViewById(R.id.textViewItemDetailAltitudeMin);

        fabDelete = view.findViewById(R.id.floatingActionButtonDelete);
        fabShare = view.findViewById(R.id.floatingActionButtonShare);
        fabView = view.findViewById(R.id.floatingActionButtonView);
    }

    private void showJourneyDetails(View view, long trackingDataID, boolean isPublic) {

        if (isPublic) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("journeys").document(String.valueOf(trackingDataID))
                            .get().addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    TrackingData documentData = new TrackingData();

                                    // Convert to ArrayList to RealmList
                                    ArrayList<Double> arrayList = (ArrayList<Double>) documentSnapshot.get("traveledDistanceList");
                                    if (arrayList != null) {
                                        // Convert ArrayList to RealmList
                                        RealmList<Double> realmList = new RealmList<>();
                                        realmList.addAll(arrayList);

                                        // Set the converted RealmList
                                        documentData.setTraveledDistanceList(realmList);
                                    }

                                    // Convert to ArrayList to RealmList
                                    arrayList = (ArrayList<Double>) documentSnapshot.get("altitudeList");
                                    if (arrayList != null) {
                                        // Convert ArrayList to RealmList
                                        RealmList<Double> realmList = new RealmList<>();
                                        realmList.addAll(arrayList);

                                        // Set the converted RealmList
                                        documentData.setAltitudeList(realmList);
                                    }

                                    // Convert ArrayList to RealmList
                                    ArrayList<Double> speedListFromFirestore = (ArrayList<Double>) documentSnapshot.get("speedList");

                                    if (speedListFromFirestore != null) {
                                        // Convert List<Double> to List<Float>
                                        List<Float> floatList = new ArrayList<>();
                                        for (Double speed : speedListFromFirestore) {
                                            if (speed != null) { // Check for null to avoid NullPointerException
                                                floatList.add(speed.floatValue()); // Convert Double to Float
                                            }
                                        }

                                        // Convert List<Float> to RealmList<Float>
                                        RealmList<Float> realmList = new RealmList<>();
                                        realmList.addAll(floatList);

                                        // Set the RealmList in your RealmObject
                                        documentData.setSpeedList(realmList);
                                    }

                                    // Convert Timestamp list to RealmList<Date>
                                    List<Timestamp> journeyDates = (List<Timestamp>) documentSnapshot.get("dateTimeList");

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
                                        documentData.setDateTimeList(realmList);
                                    }

                                    // Convert Timestamp to Date
                                    Timestamp timestamp = (Timestamp) documentSnapshot.get("journeyDate");
                                    Log.d("FETCHED TIMESTAMP", String.valueOf(timestamp));
                                    if (timestamp != null) {
                                        Date date = timestamp.toDate();
                                        Log.d("CONVERT TIMESTAMP to DATE", String.valueOf(date));
                                        documentData.setJourneyDate(date);
                                    }

                                    List<Double> longitude = (List<Double>) documentSnapshot.get("gpsCoordinatesLongitude");
                                    List<Double> latitude = (List<Double>) documentSnapshot.get("gpsCoordinatesLatitude");

                                    RealmList<GPSCoordinates> gpsCoordinatesList = new RealmList<>();
                                    for (int i = 0; i < Objects.requireNonNull(longitude).size(); i++) {
                                        GPSCoordinates gpsCoordinates = new GPSCoordinates(Objects.requireNonNull(latitude).get(i), longitude.get(i));
                                        gpsCoordinatesList.add(gpsCoordinates);
                                    }

                                    documentData.setGpsCoordinates(gpsCoordinatesList);

                                    textViewItemDetailUser.setText(documentSnapshot.getString("ownerUsername"));
                                    textViewItemDetailDistance.setText(Calculation.calculateDistance(documentData.getTraveledDistanceList().last()));
                                    textViewItemDetailTime.setText(Calculation.calculateDurationOfJourney(Objects.requireNonNull(documentData.getDateTimeList().first()), Objects.requireNonNull(documentData.getDateTimeList().last())));
                                    textViewItemDetailAvgSpeed.setText(Calculation.calculateAverageSpeed(documentData.getSpeedList()));
                                    textViewItemDetailStart.setText(Calculation.convertToDateTimeString(documentData.getDateTimeList().first()));
                                    textViewItemDetailStop.setText(Calculation.convertToDateTimeString(documentData.getDateTimeList().last()));
                                    textViewItemDetailAltitudeMax.setText(Calculation.calculateMaxAltitude(documentData.getAltitudeList()));
                                    textViewItemDetailAltitudeMin.setText(Calculation.calculateMinAltitude(documentData.getAltitudeList()));

                                    showTabPager(view, documentData);
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(requireContext(), "Data collection error", Toast.LENGTH_LONG).show();
                            });
        }
        else {
            ItemDetailQuery query = new ItemDetailQuery(requireContext());
            TrackingData trackingData = query.getTrackingData(trackingDataID);
            String username = query.getUserOfJourneyInString(trackingData);

            textViewItemDetailUser.setText(username);
            textViewItemDetailDistance.setText(Calculation.calculateDistance(trackingData.getTraveledDistanceList().last()));
            textViewItemDetailTime.setText(Calculation.calculateDurationOfJourney(Objects.requireNonNull(trackingData.getDateTimeList().first()), Objects.requireNonNull(trackingData.getDateTimeList().last())));
            textViewItemDetailAvgSpeed.setText(Calculation.calculateAverageSpeed(trackingData.getSpeedList()));
            textViewItemDetailStart.setText(Calculation.convertToDateTimeString(trackingData.getDateTimeList().first()));
            textViewItemDetailStop.setText(Calculation.convertToDateTimeString(trackingData.getDateTimeList().last()));
            textViewItemDetailAltitudeMax.setText(Calculation.calculateMaxAltitude(trackingData.getAltitudeList()));
            textViewItemDetailAltitudeMin.setText(Calculation.calculateMinAltitude(trackingData.getAltitudeList()));

            showTabPager(view, trackingData);
        }
    }

    private void showTabPager(View view, TrackingData trackingData) {
        TabLayout tabLayout = view.findViewById(R.id.tabLayoutItemDetail);
        ViewPager2 viewPager = view.findViewById(R.id.viewPagerItemDetail);

        // Set up the PagerAdapter
        TabPagerAdapter adapter = new TabPagerAdapter(requireActivity(), trackingData);
        viewPager.setAdapter(adapter);

        viewPager.setOffscreenPageLimit(4);

        // Link TabLayout and ViewPager2
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Map");
                            break;
                        case 1:
                            tab.setText("Speed");
                            break;
                        case 2:
                            tab.setText("Altitude");
                            break;
                        case 3:
                            tab.setText("Distance");
                            break;
                    }
                }).attach();
    }

    private void showDialogForDeleteJourney(ItemDetailQuery query, TrackingData trackingData) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Delete journey");
        builder.setMessage("Do you really want to delete your journey?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            if (trackingData.getPublicValue()) {

                if (!isInternetAvailable(requireActivity())) {
                    dialogInternetConnection(requireActivity(), "No Internet Connection", "Journey is public. You need to turn on the internet to delete the journey. Please connect to the internet to continue.");
                }
                else {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("journeys").document(String.valueOf(trackingData.getId()))
                            .delete().addOnSuccessListener(unused -> {
                                if(query.deleteJourney(trackingData)){
                                    Toast.makeText(requireActivity(), "Journey successfully deleted!", Toast.LENGTH_LONG).show();
                                    HistoryFragment yourFragment = new HistoryFragment();
                                    FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                                    transaction.replace(R.id.fragment_container, yourFragment);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                }
                                else {
                                    Toast.makeText(requireActivity(), "Journey unsuccessfully deleted!", Toast.LENGTH_LONG).show();
                                }

                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(requireActivity(), "Journey unsuccessfully deleted!", Toast.LENGTH_LONG).show();
                            });
                }
            }
            else {
                if(query.deleteJourney(trackingData)){
                    Toast.makeText(requireActivity(), "Journey successfully deleted!", Toast.LENGTH_LONG).show();
                    HistoryFragment yourFragment = new HistoryFragment();
                    FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, yourFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
                else {
                    Toast.makeText(requireActivity(), "Journey unsuccessfully deleted!", Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.dismiss();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDialogForShareJourney(ItemDetailQuery query, TrackingData trackingData) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_share, null);

        // Find the EditTexts and Button in the custom layout
        EditText editTextUsernameShare = dialogView.findViewById(R.id.editTextUsernameShare);
        Button buttonShare = dialogView.findViewById(R.id.buttonShare);
        Button buttonCancel = dialogView.findViewById(R.id.buttonCancelShare);
        RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroupShare);
        RadioButton radioButtonPublic = dialogView.findViewById(R.id.radioButtonPublic);
        RadioButton radioButtonGroup = dialogView.findViewById(R.id.radioButtonGroup);
        Button buttonAdd = dialogView.findViewById(R.id.buttonAdd);

        User user = query.getUserOfJourney(trackingData);
        List<String> listOfUsers = new ArrayList<>();

        buttonCancel.setBackgroundColor(Color.TRANSPARENT);

        // Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        radioButtonPublic.setOnCheckedChangeListener(((compoundButton, isChecked) -> {
            if (isChecked) {
                editTextUsernameShare.setVisibility(View.GONE);
                buttonAdd.setVisibility(View.GONE);
            }
            else {
                editTextUsernameShare.setVisibility(View.VISIBLE);
                buttonAdd.setVisibility(View.VISIBLE);
            }
        }));

        buttonCancel.setOnClickListener(view -> {
            dialog.dismiss();
        });

        buttonAdd.setOnClickListener(view -> {
            listOfUsers.add(editTextUsernameShare.getText().toString());
            Toast.makeText(getActivity(), "Added to the list", Toast.LENGTH_LONG).show();
            editTextUsernameShare.setText("");
        });

        buttonShare.setOnClickListener(view -> {

            if (user != null) {
                //shareJourney(user, query, trackingData, listOfUsers);
                if (radioButtonGroup.isChecked()) {
                    if (!isInternetAvailable(requireActivity())) {
                        dialogInternetConnection(requireActivity(), "No Internet Connection", "Please connect to the internet to continue.");
                    } else {
                        query.shareJourneyToGroup(trackingData, listOfUsers);
                        query.setAsPublic(trackingData);
                        dialog.dismiss();
                    }
                }
                else {
                    if (!isInternetAvailable(requireActivity())) {
                        dialogInternetConnection(requireActivity(), "No Internet Connection", "Please connect to the internet to continue.");
                    } else {
                        query.shareJourneyToAll(trackingData);
                        query.setAsPublic(trackingData);
                        dialog.dismiss();
                    }
                }
            }
            else {
                Toast.makeText(getActivity(), "Failed", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }

        });

        dialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void showJourneyVisibility(ItemDetailQuery query, TrackingData trackingData) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_view_share, null);

        TextView title = dialogView.findViewById(R.id.textViewTitleShareJourney);
        Button buttonPrivate = dialogView.findViewById(R.id.buttonPrivate);

        // Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        if (trackingData.getPublicValue()) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("journeys").document(String.valueOf(trackingData.getId()))
                    .get().addOnSuccessListener(documentSnapshot -> {
                        if (Boolean.TRUE.equals(documentSnapshot.getBoolean("public"))) {
                            title.setText("The journey is visible to all!");
                            buttonPrivate.setVisibility(View.VISIBLE);
                            buttonPrivate.setOnClickListener(view -> {
                                if (!isInternetAvailable(requireActivity())) {
                                    dialogInternetConnection(requireActivity(), "No Internet Connection", "Journey is public. You need to turn on the internet to update the journey. Please connect to the internet to continue.");
                                }
                                else
                                {
                                    setJourneyToPrivate(String.valueOf(trackingData.getId()), query, trackingData);
                                    dialog.dismiss();
                                }
                            });
                        }
                        else {
                            List<String> listUsername = (List<String>) documentSnapshot.get("sharedTo");
                            for (String username : Objects.requireNonNull(listUsername)) {
                                addRowToLayout(dialogView, username);
                                buttonPrivate.setVisibility(View.VISIBLE);
                                buttonPrivate.setOnClickListener(view -> {
                                    if (!isInternetAvailable(requireActivity())) {
                                        dialogInternetConnection(requireActivity(), "No Internet Connection", "Journey is public. You need to turn on the internet to update the journey. Please connect to the internet to continue.");
                                    }
                                    {
                                        setJourneyToPrivate(String.valueOf(trackingData.getId()), query, trackingData);
                                        dialog.dismiss();
                                    }
                                });
                            }
                        }
                    })
                    .addOnFailureListener(e -> {

                    });
        }
        else {
            title.setText("The journey is private");
            buttonPrivate.setVisibility(View.GONE);
        }

        dialog.show();
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

    private void addRowToLayout(View dialogView, String username) {
        LinearLayout usersLayout = dialogView.findViewById(R.id.usersLayout);

        // Create new row
        LinearLayout rowLayout = new LinearLayout(requireContext());
        rowLayout.setOrientation(LinearLayout.HORIZONTAL);
        rowLayout.setPadding(25, 16, 25, 0);

        TextView usernameView = new TextView(requireContext());
        usernameView.setText(username);
        usernameView.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark_green));
        usernameView.setTextSize(25);
        usernameView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        rowLayout.addView(usernameView);

        usersLayout.addView(rowLayout);
    }

    private void setJourneyToPrivate(String journeyID, ItemDetailQuery query, TrackingData trackingData) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("journeys").document(journeyID).delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(requireActivity(), "Journey successfully set to private", Toast.LENGTH_LONG).show();
                        query.setAsPrivate(trackingData);
                    } else {
                        Toast.makeText(requireActivity(), "Failed to set private", Toast.LENGTH_LONG).show();
                    }
                });
    }
}
