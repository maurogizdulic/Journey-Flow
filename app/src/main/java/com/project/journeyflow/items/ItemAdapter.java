package com.project.journeyflow.items;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyViewHolder> {

    private RealmResults<TrackingData> trackingDataList;
    private FragmentManager fragmentManager;

    public ItemAdapter(RealmResults<TrackingData> trackingDataList, FragmentManager fragmentManager){
        this.trackingDataList = trackingDataList;
        this.fragmentManager = fragmentManager;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewDistance, textViewTime, textViewDate;
        public MapView mapView;

        public MyViewHolder(View itemView) {
            super(itemView);
            mapView = itemView.findViewById(R.id.mapViewItem);
            textViewDistance = itemView.findViewById(R.id.itemTotalDistance);
            textViewTime = itemView.findViewById(R.id.itemTimeOfTravel);
            textViewDate = itemView.findViewById(R.id.itemDateAndTime);
        }
    }

    @NonNull
    @Override
    public ItemAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.MyViewHolder holder, int position) {

        TrackingData trackingData = trackingDataList.get(position);

        holder.textViewDistance.setText("Distance: " + Calculation.calculateDistance(trackingData.getTraveledDistanceList().last()));
        holder.textViewTime.setText("Time: " + Calculation.calculateDurationOfJourney(trackingData.getDateTimeList().first(), trackingData.getDateTimeList().last()));
        holder.textViewDate.setText("Date: " + Calculation.convertToDateTimeString(trackingData.getDateTimeList().first()));

        showJourneyOnMap(holder.mapView, trackingData.getGpsCoordinates());

        holder.itemView.setOnClickListener(view -> {
            Fragment itemDetailFragment = new ItemDetailFragment();

            Bundle bundle = new Bundle();
            //bundle.putSerializable("trackingData", (Serializable) trackingData);// Make sure TrackingData implements Serializable or Parcelable
            bundle.putLong("trackingDataId", trackingData.getId());
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

    public List<TrackingData> getDataList() {
        return trackingDataList;
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
