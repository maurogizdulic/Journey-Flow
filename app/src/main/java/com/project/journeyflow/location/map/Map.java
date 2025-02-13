package com.project.journeyflow.location.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.project.journeyflow.R;
import com.project.journeyflow.database.GPSCoordinates;

import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;

public class Map {

    public static void showJourneyOnMap(MapView map, List<GPSCoordinates> gpsCoordinatesList, Context context){

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

        addStartMarker(map, gpsCoordinatesList, context);

        addEndMarker(map, gpsCoordinatesList, context);

        // Calculate approximate zoom level based on bounding box size
        double latSpan = boundingBox.getLatNorth() - boundingBox.getLatSouth();
        double lonSpan = boundingBox.getLonEast() - boundingBox.getLonWest();
        int zoomLevel = (int) Math.floor(10 - Math.log(Math.max(latSpan, lonSpan)) / Math.log(2));
        zoomLevel = (int) Math.max(map.getMinZoomLevel(), Math.min(zoomLevel, map.getMaxZoomLevel())); // Clamp to min/max zoom

        // Set zoom level manually
        map.getController().setZoom(zoomLevel);
        map.setMultiTouchControls(true);
        Polyline polyline = new Polyline();
        polyline.setPoints(geoPointList); // Set the points for the polyline
        polyline.setColor(Color.BLUE); // Set polyline color
        polyline.setWidth(5.0f); // Set line width
        map.getOverlayManager().add(polyline);
        map.invalidate();
    }

    private static void addStartMarker(MapView map, List<GPSCoordinates> gpsCoordinatesList, Context context) {
        Marker marker = new Marker(map);
        marker.setPosition(new GeoPoint(gpsCoordinatesList.get(0).getLatitude(), gpsCoordinatesList.get(0).getLongitude()));
        marker.setTitle("Start");

        // Set custom icon for the marker
        Drawable icon = ContextCompat.getDrawable(context, R.drawable.start_icon);

        Bitmap bitmap = ((BitmapDrawable) icon).getBitmap();

        // Resize the bitmap to the desired size (e.g., 100x100 pixels)
        int iconWidth = 30; // Desired width
        int iconHeight = 30; // Desired height
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, iconWidth, iconHeight, false);

        marker.setIcon(new BitmapDrawable(context.getResources(), resizedBitmap));

        map.getOverlays().add(marker);
    }

    private static void addEndMarker(MapView map,  List<GPSCoordinates> gpsCoordinatesList, Context context) {
        Marker marker = new Marker(map);
        marker.setPosition(new GeoPoint(gpsCoordinatesList.get(gpsCoordinatesList.size()-1).getLatitude(), gpsCoordinatesList.get(gpsCoordinatesList.size()-1).getLongitude()));
        marker.setTitle("Start");

        // Set custom icon for the marker
        Drawable icon = ContextCompat.getDrawable(context, R.drawable.stop_icon);

        Bitmap bitmap = ((BitmapDrawable) icon).getBitmap();

        // Resize the bitmap to the desired size (e.g., 100x100 pixels)
        int iconWidth = 30; // Desired width
        int iconHeight = 30; // Desired height
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, iconWidth, iconHeight, false);

        marker.setIcon(new BitmapDrawable(context.getResources(), resizedBitmap));

        map.getOverlays().add(marker);
    }
}
