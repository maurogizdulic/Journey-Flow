package com.project.journeyflow.items;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.project.journeyflow.R;
import com.project.journeyflow.calculation.Calculation;
import com.project.journeyflow.database.GPSCoordinates;
import com.project.journeyflow.database.TrackingData;
import com.project.journeyflow.fragments.item_detail.ItemDetailFragment;

import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.realm.RealmList;

public class ItemAdapterPublic extends RecyclerView.Adapter<ItemAdapterPublic.MyViewHolder> {

    private final RealmList<TrackingData> trackingDataList;
    private final FragmentManager fragmentManager;

    public ItemAdapterPublic(RealmList<TrackingData> trackingDataList, FragmentManager fragmentManager){
        this.trackingDataList = trackingDataList;
        this.fragmentManager = fragmentManager;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewDistance, textViewTime, textViewDate, textViewUsername;
        public MapView mapView;

        public MyViewHolder(View itemView) {
            super(itemView);
            mapView = itemView.findViewById(R.id.mapViewItem);
            textViewDistance = itemView.findViewById(R.id.itemTotalDistance);
            textViewTime = itemView.findViewById(R.id.itemTimeOfTravel);
            textViewDate = itemView.findViewById(R.id.itemDateAndTime);
            textViewUsername = itemView.findViewById(R.id.itemUser);
            textViewUsername.setVisibility(View.VISIBLE);
        }
    }

    @NonNull
    @Override
    public ItemAdapterPublic.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout, parent, false);
        return new ItemAdapterPublic.MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ItemAdapterPublic.MyViewHolder holder, int position) {

        TrackingData trackingData = trackingDataList.get(position);

        Log.d("TRACKING DATA", String.valueOf(Objects.requireNonNull(trackingData).getDateTimeList()));

        holder.textViewUsername.setText("User: " + trackingData.getUsername());
        holder.textViewDistance.setText("Distance: " + Calculation.calculateDistance(Objects.requireNonNull(trackingData).getTraveledDistanceList().last()));
        holder.textViewTime.setText("Time: " + Calculation.calculateDurationOfJourney(Objects.requireNonNull(trackingData.getDateTimeList().first()), Objects.requireNonNull(trackingData.getDateTimeList().last())));
        holder.textViewDate.setText("Date: " + Calculation.convertToDateTimeString(trackingData.getDateTimeList().first()));

        showJourneyOnMap(holder.mapView, trackingData.getGpsCoordinates());

        holder.itemView.setOnClickListener(view -> {
            Fragment itemDetailFragment = new ItemDetailFragment();

            Bundle bundle = new Bundle();
            bundle.putLong("trackingDataId", trackingData.getId());
            bundle.putBoolean("public", true);
            itemDetailFragment.setArguments(bundle);

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, itemDetailFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
    }

    @Override
    public int getItemCount() {
        return trackingDataList.size();
    }

    private void showJourneyOnMap(MapView map, List<GPSCoordinates> gpsCoordinatesList) {
        List<GeoPoint> geoPointList = new ArrayList<>();
        for (GPSCoordinates coord : gpsCoordinatesList) {
            geoPointList.add(new GeoPoint(coord.getLatitude(), coord.getLongitude()));
        }

        // Calculate bounding box
        BoundingBox boundingBox = BoundingBox.fromGeoPoints(geoPointList);

        // Calculate the center point
        double centerLat = (boundingBox.getLatNorth() + boundingBox.getLatSouth()) / 2;
        double centerLon = (boundingBox.getLonEast() + boundingBox.getLonWest()) / 2;
        GeoPoint centerPoint = new GeoPoint(centerLat, centerLon);

        // Set center manually
        map.getController().setCenter(centerPoint);

        // Calculate approximate zoom level based on bounding box size
        double latSpan = boundingBox.getLatNorth() - boundingBox.getLatSouth();
        double lonSpan = boundingBox.getLonEast() - boundingBox.getLonWest();
        int zoomLevel = (int) Math.floor(10 - Math.log(Math.max(latSpan, lonSpan)) / Math.log(2));
        zoomLevel = (int) Math.max(map.getMinZoomLevel(), Math.min(zoomLevel, map.getMaxZoomLevel())); // Clamp to min/max zoom

        // Set zoom level manually
        map.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK);
        map.getController().setZoom(zoomLevel);
        //map.setMultiTouchControls(true);
        map.setClickable(false);
        map.setBuiltInZoomControls(false);
        Polyline polyline = new Polyline();
        polyline.setPoints(geoPointList); // Set the points for the polyline
        polyline.setColor(Color.BLUE); // Set polyline color
        polyline.setWidth(5.0f); // Set line width
        map.getOverlayManager().add(polyline);
        map.invalidate();
    }

}
