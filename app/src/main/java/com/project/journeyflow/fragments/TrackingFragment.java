package com.project.journeyflow.fragments;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.project.journeyflow.R;
import com.project.journeyflow.location.DataProcessing;
import com.project.journeyflow.location.GPS;
import com.project.journeyflow.location.LocationViewModel;
import com.project.journeyflow.location.TrackingService;
import com.project.journeyflow.permission.MyPermission;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.List;
import java.util.Objects;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class TrackingFragment extends Fragment {

    private GPS gps;

    private TextView textViewTraveledDistance, textViewNumberOfSteps, textViewCurrentAltitude, textViewSpeed, textViewAverageSpeed, textViewTrackingTitle;
    private MapView map;
    private MyLocationNewOverlay myLocationNewOverlay;
    private boolean hasLocationPermissionState = false;
    private GeoPoint currentLocation;
    private BroadcastReceiver locationReceiver;
    private Polyline polyline;
    private LocationViewModel locationViewModel;
    private DataProcessing dataProcessing;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the GPS class here
        gps = new GPS(requireActivity());
        dataProcessing = new DataProcessing();
        Log.d("TRACKING", "onCreate CALLED");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tracking, container, false);

        // Checking location permission on creating fragment
        MyPermission myPermission = new MyPermission(getActivity());
        hasLocationPermissionState = myPermission.checkLocationPermission();

        Button buttonStartStop = view.findViewById(R.id.buttonStartStop);
        Button buttonMapSettings = view.findViewById(R.id.buttonMapSettings);
        Button buttonCurrentLocation = view.findViewById(R.id.buttonFindCurrentLocation);
        textViewAverageSpeed = view.findViewById(R.id.textViewAverageSpeed);
        textViewTraveledDistance = view.findViewById(R.id.textViewTraveledDistance);
        textViewNumberOfSteps = view.findViewById(R.id.textViewNumberOfSteps);
        textViewCurrentAltitude = view.findViewById(R.id.textViewCurrentAltitude);
        textViewSpeed = view.findViewById(R.id.textViewSpeed);
        textViewTrackingTitle = view.findViewById(R.id.textViewJourney);

        // Initialize MapView
        map = view.findViewById(R.id.osmMapView);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        map.setClickable(true);
        map.setUseDataConnection(true);
        map.setVerticalMapRepetitionEnabled(false);
        map.setHorizontalMapRepetitionEnabled(false);

        // Setup Location Overlay
        myLocationNewOverlay = new MyLocationNewOverlay(map);

        // Start listening for location updates
        if (gps.isProviderEnabled()){
            startGPSListening();
        }
        else {
            //map.set
            Toast.makeText(getActivity(), "Location disabled! Turn on location for current location!", Toast.LENGTH_LONG).show();
        }

        // Button for start and stop journey
        buttonStartStop.setOnClickListener(view1 -> {

            // On button click check if location is enabled
            if (hasLocationPermissionState) {
                // Check if gps found our phone
                if (gps.isProviderEnabled()) {
                    if (!TrackingService.isTracking) {
                        startTracking();
                    } else {
                        stopTracking();
                    }
                }
                else {
                    Toast.makeText(getActivity(), "GPS has not found our device yet!", Toast.LENGTH_SHORT).show();
                }
            } else {
                hasLocationPermissionState = myPermission.checkLocationPermission();
            }
        });

        // Bottom sheet with journey statistics
        View bottomSheet = view.findViewById(R.id.bottom_sheet);
        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);

        // Configure BottomSheet
        behavior.setFitToContents(true); // Allow multiple states
        behavior.setPeekHeight(500); // Collapsed height in pixels

        behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        Log.d("BottomSheet", "Collapsed");
                        behavior.setPeekHeight(500);
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        Log.d("BottomSheet", "Expanded");
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        Log.d("BottomSheet", "Dragging");
                        break;
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                        Log.d("BottomSheet", "Half Expanded");
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        Log.d("BottomSheet", "Hidden");
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        Log.d("BottomSheet", "Settling");
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                Log.d("BottomSheet", "Sliding: " + slideOffset);
            }
        });

        buttonMapSettings.setOnClickListener(view1 -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

            // Inflate custom layout if you have one
            LayoutInflater dialogInflater = getLayoutInflater();
            View dialogView = dialogInflater.inflate(R.layout.dialog_map_settings, null);

            /*
            MaterialSwitch switchZoomButtons = dialogView.findViewById(R.id.switch1);
            MaterialSwitch switch2 = dialogView.findViewById(R.id.switch2);
            MaterialSwitch switch3 = dialogView.findViewById(R.id.switch3);
            MaterialSwitch switchMultiTouchControls = dialogView.findViewById(R.id.switch4);

             */

            builder.setView(dialogView)
                    .setTitle("Choose Options")
                    .setPositiveButton("Update", (dialog, which) -> {
                        // Handle OK click

                    })
                    .setNegativeButton("Cancel", null);

            // Create the dialog
            AlertDialog dialog = builder.create();

            // Apply the custom background with rounded corners
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.drawable.dialog_background);

            // Show the dialog
            dialog.show();
        });

        buttonCurrentLocation.setOnClickListener(view1 -> {
            if (gps.isProviderEnabled()){
                map.getController().animateTo(currentLocation);
                /*
                myLocationNewOverlay.enableMyLocation();
                map.getOverlays().add(myLocationNewOverlay);
                map.getController().setZoom(19.0);

                 */
            }
            else {
                Toast.makeText(getActivity(), "GPS doesn't find current location! Check if you have turned on the location in the settings!", Toast.LENGTH_LONG).show();
            }
        });

        Log.d("TRACKING", "onCreateView CALLED");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        polyline = new Polyline();
        map.getOverlays().add(polyline);

        locationViewModel = new ViewModelProvider(requireActivity()).get(LocationViewModel.class);

        locationViewModel.getPointsList().observe(getViewLifecycleOwner(), new Observer<List<GeoPoint>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onChanged(List<GeoPoint> geoPoints) {
                polyline.setPoints(geoPoints);
                map.invalidate();// Refresh map with updated polyline

                textViewTraveledDistance.setText("Traveled distance: " + dataProcessing.calculateTraveledDistance(locationViewModel.getMeasuredDistance()));
                Log.d("GEOPOINTS LIST", geoPoints.size() + " = " + geoPoints);
            }
        });

        locationViewModel.getAltitudeList().observe(getViewLifecycleOwner(), new Observer<List<Double>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onChanged(List<Double> altitudeList) {
                textViewCurrentAltitude.setText("Current altitude: " + dataProcessing.calculateAltitude(altitudeList) + " m");
            }
        });

        locationViewModel.getSpeedList().observe(getViewLifecycleOwner(), new Observer<List<Float>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onChanged(List<Float> speedList) {
                textViewSpeed.setText("Speed: " + dataProcessing.calculateSpeed(speedList) + " km/h");
                textViewAverageSpeed.setText("Average speed: " + dataProcessing.calculateAverageSpeed(speedList) + " km/h");
            }
        });

        Log.d("TRACKING", "onViewCreated CALLED");
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onResume() {
        super.onResume();

        if (TrackingService.isTracking) {
            setVisibleStatistic();
            textViewTrackingTitle.setText("Stop your journey");
        } else {
            textViewTrackingTitle.setText("Start your journey");
        }

        // Setup location overlay
        myLocationNewOverlay.enableMyLocation();
        map.getOverlays().add(myLocationNewOverlay);
        map.getController().setZoom(19.0);

        //startGPSListening();
        Log.d("TRACKING", "onResume CALLED");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Unregister the receiver when the fragment is destroyed
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(locationReceiver);
        Log.d("TRACKING", "onDestroy CALLED");

    }

    @Override
    public void onPause() {
        super.onPause();
        myLocationNewOverlay.disableMyLocation();
    }

    @SuppressLint("SetTextI18n")
    private void startTracking() {

        TrackingService.isTracking = true;
        //buttonStartStop.setText("Stop Tracking");
        textViewTrackingTitle.setText("Stop your journey");

        // Start the tracking service
        Intent serviceIntent = new Intent(getActivity(), TrackingService.class);
        ContextCompat.startForegroundService(requireActivity(), serviceIntent);

        // Clear point
        locationViewModel.clearPointsList();

        // Set visible statistics data
        setVisibleStatistic();
    }

    @SuppressLint("SetTextI18n")
    private void stopTracking() {
        // Save data
        if (TrackingService.saveData(getActivity())) {
            TrackingService.isTracking = false;
            //buttonStartStop.setText("Start Tracking");
            textViewTrackingTitle.setText("Start your journey");

            // Stop tracking service
            Intent serviceIntent = new Intent(getActivity(), TrackingService.class);
            requireActivity().stopService(serviceIntent);
        }
        else {
            Toast.makeText(getActivity(), "Error while saving data!", Toast.LENGTH_LONG).show();
        }
    }

    private void startGPSListening(){
        // Check for location permissions
        /*
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

         */

        // Start listening for GPS updates using the GPS class
        gps.startListening(new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                currentLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
                map.getController().animateTo(currentLocation);
                myLocationNewOverlay.enableFollowLocation();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(@NonNull String provider) {}

            @Override
            public void onProviderDisabled(@NonNull String provider) {}
        });
    }

    private void setVisibleStatistic() {
        textViewSpeed.setVisibility(View.VISIBLE);
        textViewAverageSpeed.setVisibility(View.VISIBLE);
        textViewTraveledDistance.setVisibility(View.VISIBLE);
        textViewNumberOfSteps.setVisibility(View.VISIBLE);
        textViewCurrentAltitude.setVisibility(View.VISIBLE);
    }

}