package com.project.journeyflow.fragments.item_detail;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.project.journeyflow.R;
import com.project.journeyflow.calculation.Calculation;
import com.project.journeyflow.database.TrackingData;
import com.project.journeyflow.fragments.HistoryFragment;
import com.project.journeyflow.items.TabPagerAdapter;
import com.project.journeyflow.query.item_detail.ItemDetailQuery;

import java.util.Objects;

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
            TrackingData trackingData = query.getTrackingData(trackingDataId);
            String username = query.getUserOfJourney(trackingData);

            showJourneyDetails(trackingData, username);

            showTabPager(view, trackingData);

            if (query.isOwner(trackingData)) {
                fabView.setVisibility(View.VISIBLE);
            }
            else {
                fabView.setVisibility(View.GONE);
            }

            fabDelete.setOnClickListener(v -> {
                showDialogForDeleteJourney(query, trackingData);
            });

            fabShare.setOnClickListener(v -> {
                showDialogForShareJourney(query, trackingData);
            });

            fabView.setOnClickListener(v -> {
                showJourneyVisibility();
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

    private void showJourneyDetails(TrackingData trackingData, String username) {

        textViewItemDetailUser.setText(username);
        textViewItemDetailDistance.setText(Calculation.calculateDistance(trackingData.getTraveledDistanceList().last()));
        textViewItemDetailTime.setText(Calculation.calculateDurationOfJourney(Objects.requireNonNull(trackingData.getDateTimeList().first()), Objects.requireNonNull(trackingData.getDateTimeList().last())));
        textViewItemDetailAvgSpeed.setText(Calculation.calculateAverageSpeed(trackingData.getSpeedList()));
        textViewItemDetailStart.setText(Calculation.convertToDateTimeString(trackingData.getDateTimeList().first()));
        textViewItemDetailStop.setText(Calculation.convertToDateTimeString(trackingData.getDateTimeList().last()));
        textViewItemDetailAltitudeMax.setText(Calculation.calculateMaxAltitude(trackingData.getAltitudeList()));
        textViewItemDetailAltitudeMin.setText(Calculation.calculateMinAltitude(trackingData.getAltitudeList()));
    }

    private void showTabPager(View view, TrackingData trackingData) {
        TabLayout tabLayout = view.findViewById(R.id.tabLayoutItemDetail);
        ViewPager2 viewPager = view.findViewById(R.id.viewPagerItemDetail);

        // Set up the PagerAdapter
        TabPagerAdapter adapter = new TabPagerAdapter(requireActivity(), trackingData);
        viewPager.setAdapter(adapter);

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
            if(query.deleteJourney(trackingData)) {
                Toast.makeText(requireActivity(), "Journey successfully deleted!", Toast.LENGTH_LONG).show();

                Fragment fragment = new HistoryFragment();
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
            else {
                Toast.makeText(requireActivity(), "Unsuccessfully deleted journey!", Toast.LENGTH_LONG).show();
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

        buttonCancel.setBackgroundColor(Color.TRANSPARENT);

        // Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        buttonCancel.setOnClickListener(view -> {
            dialog.dismiss();
        });

        buttonShare.setOnClickListener(view -> {

        });

        dialog.show();
    }

    private void showJourneyVisibility() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_view_share, null);

        // Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        dialog.show();
    }
}
