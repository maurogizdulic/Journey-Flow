package com.project.journeyflow.query;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.project.journeyflow.calculation.Calculation;
import com.project.journeyflow.chart.Chart;
import com.project.journeyflow.database.GPSCoordinates;
import com.project.journeyflow.database.TrackingData;
import com.project.journeyflow.database.User;

import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class HomeFragmentQuery extends Query {

    public HomeFragmentQuery(Context context) {
        super(context);
    }

    public void displayDataOnHomeFragment(ScrollView scrollView, ImageView imageView, TextView title, TextView noJourney, TextView dateTime, TextView duration, TextView distance, TextView averageSpeed, MapView map, LineChart graphDistance, LineChart graphAltitude, LineChart graphSpeed) {
        User user = fetchUserData();

        if (user != null && !user.getTrackings().isEmpty()) {
            scrollView.setVisibility(View.VISIBLE);
            noJourney.setVisibility(View.GONE);
            displayLastJourneyData(user, title, dateTime, duration, distance, averageSpeed, map, graphDistance, graphAltitude, graphSpeed);
        }
        else {
            scrollView.setVisibility(View.GONE);
            noJourney.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);
            displayMotivationalMessage(title, noJourney, user);
        }
    }

    @SuppressLint("SetTextI18n")
    private void displayMotivationalMessage(TextView title, TextView noJourney, User user) {
        if (user != null){
            title.setText(String.format("Hello %s", user.getFirstName()));
            noJourney.setText("Every journey begins with a single step—today is yours. Progress isn’t about perfection but showing up and pushing forward. Embrace challenges, celebrate small wins, and believe in your strength. Let’s make this journey count!");
        }
        else {
            Log.d("USER", "USER IS NULL");
        }
    }

    @SuppressLint("SetTextI18n")
    private void displayLastJourneyData(User user, TextView title, TextView dateTime, TextView duration, TextView distance, TextView averageSpeed, MapView map, LineChart chartDistance, LineChart chartAltitude, LineChart chartSpeed) {
        title.setText(String.format("Hello %s", user.getFirstName()));

        TrackingData trackingData = user.getTrackings().get(user.getTrackings().size() - 1);
        String durationTime = Calculation.calculateDurationOfJourney(trackingData.getDateTimeList().first(), Objects.requireNonNull(trackingData.getDateTimeList().last()));
        String printDistance = Calculation.calculateDistance(trackingData.getTraveledDistanceList().last());
        String printAverageSpeed = Calculation.calculateAverageSpeed(trackingData.getSpeedList());

        dateTime.setText("- Date and time: " + Calculation.convertToDateTimeString(trackingData.getDateTimeList().last()));
        duration.setText("- Duration: " + durationTime);
        distance.setText("- Distance: " + printDistance);
        averageSpeed.setText("- Avg. speed: " + printAverageSpeed);

        showJourneyOnMap(map, trackingData.getGpsCoordinates());

        // Show Distance chart
        Chart.showLineChartDistance(chartDistance, trackingData.getDateTimeList(), trackingData.getTraveledDistanceList());

        // Show Altitude chart
        Chart.showLineChartAltitude(chartAltitude, trackingData.getTraveledDistanceList(),trackingData.getAltitudeList());

        // Show Speed chart
        Chart.showLineChartSpeed(chartSpeed, trackingData.getTraveledDistanceList(), trackingData.getSpeedList());
    }

    private void showJourneyOnMap(MapView map, List<GPSCoordinates> gpsCoordinatesList){

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
        map.setMultiTouchControls(true);
        Polyline polyline = new Polyline();
        polyline.setPoints(geoPointList); // Set the points for the polyline
        polyline.setColor(Color.BLUE); // Set polyline color
        polyline.setWidth(5.0f); // Set line width
        map.getOverlayManager().add(polyline);
        map.invalidate();
    }
}
