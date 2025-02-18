package com.project.journeyflow.fragments.item_detail;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.project.journeyflow.R;
import com.project.journeyflow.database.TrackingData;
import com.project.journeyflow.location.map.Map;

import org.osmdroid.views.MapView;

public class MapDetailFragment extends Fragment {

    private final TrackingData trackingData;
    private MapView mapView;
    private Context context;

    public MapDetailFragment(TrackingData trackingData) {
        this.trackingData = trackingData;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;  // Store context when the fragment is attached
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map_detail, container, false);

        initializeView(view);

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();

        if (trackingData != null) {
            Map.showJourneyOnMap(mapView, trackingData.getGpsCoordinates(), context);
        }
    }

    private void initializeView(View view) {
        mapView = view.findViewById(R.id.mapViewItemDetail);
    }
}
