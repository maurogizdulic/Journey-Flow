package com.project.journeyflow.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import com.project.journeyflow.R;
import com.project.journeyflow.database.TrackingData;
import com.project.journeyflow.items.ItemAdapter;
import com.project.journeyflow.query.HistoryFragmentQuery;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import io.realm.RealmResults;

public class HistoryFragment extends Fragment {

    private FloatingActionButton fabFilter;
    private RecyclerView recyclerViewHistory;
    private TextView textViewHistoryMessage;
    private Button buttonNow;
    private ItemAdapter adapter;

    private double minDistance = 999999999;
    private double maxDistance = 0;
    private int minDuration = 999999;
    private int maxDuration = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        HistoryFragmentQuery query = new HistoryFragmentQuery(requireActivity());

        initializeViews(view);

        fabFilter.setOnClickListener(view1 -> {
            showFilterDialog();
        });

        buttonNowAction(buttonNow);

        getAllJourneys(query);

        return view;
    }

    private void initializeViews(View view) {
        fabFilter = view.findViewById(R.id.fabHistoryFilter);
        recyclerViewHistory = view.findViewById(R.id.recyclerViewHistory);
        textViewHistoryMessage = view.findViewById(R.id.textViewHistoryMessage);
        buttonNow = view.findViewById(R.id.buttonHistoryNow);
    }

    private void getAllJourneys(HistoryFragmentQuery query) {

        adapter = new ItemAdapter(query.getAllJourneys(), getParentFragmentManager());
        Log.d("ITEM_ADAPTER", String.valueOf(adapter.getItemCount()));
        recyclerViewHistory.setAdapter(adapter);


        if (adapter.getItemCount() == 0) {
            fabFilter.setVisibility(View.GONE);
            recyclerViewHistory.setVisibility(View.GONE);

            textViewHistoryMessage.setVisibility(View.VISIBLE);
            buttonNow.setVisibility(View.VISIBLE);
        }
        else {
            fabFilter.setVisibility(View.VISIBLE);
            recyclerViewHistory.setVisibility(View.VISIBLE);

            textViewHistoryMessage.setVisibility(View.GONE);
            buttonNow.setVisibility(View.GONE);
        }
    }

    private void showFilterDialog() {

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
        ImageButton buttonExitFilter = dialogView.findViewById(R.id.imageButtonExitFilter);
        Button buttonResetFilter = dialogView.findViewById(R.id.buttonResetFilter);
        ImageButton imageButtonSubmitFilter = dialogView.findViewById(R.id.imageButtonSubmitFilter);

        HistoryFragmentQuery query = new HistoryFragmentQuery(requireActivity());

        // Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        setInitialValues(textViewStartDate, textViewEndDate, editTextDistanceFrom, editTextDistanceTo, editTextDurationFrom, editTextDurationTo, query);

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

            RealmResults<TrackingData> filteredData = query.getFilteredData(textViewStartDate.getText().toString(), textViewEndDate.getText().toString(), minDistance * 1000, maxDistance * 1000, minDuration, maxDuration);

            if (filteredData != null) {
                Log.d("FILTERED TRACKING DATA", String.valueOf(filteredData));
                adapter = new ItemAdapter(filteredData, getParentFragmentManager());
                Log.d("ITEM_ADAPTER", String.valueOf(adapter.getItemCount()));
                recyclerViewHistory.setAdapter(adapter);

                dialog.dismiss();
            }
        });

        buttonResetFilter.setOnClickListener(view -> {
            setInitialValues(textViewStartDate, textViewEndDate, editTextDistanceFrom, editTextDistanceTo, editTextDurationFrom, editTextDurationTo, query);
        });

        buttonExitFilter.setOnClickListener(view -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    @SuppressLint("DefaultLocale")
    private void setInitialValues(TextView startDate, TextView endDate, EditText distanceFrom, EditText distanceTo, EditText durationFrom, EditText durationTo, HistoryFragmentQuery query) {
        RealmResults<TrackingData> trackingDataList = query.getAllJourneys();

        if (!trackingDataList.isEmpty()){

            Date sDate = Objects.requireNonNull(trackingDataList.first()).getDateTimeList().first();
            Date eDate = Objects.requireNonNull(trackingDataList.last()).getDateTimeList().first();

            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            String formattedStartDate = sdf.format(Objects.requireNonNull(sDate));
            String formattedEndDate = sdf.format(Objects.requireNonNull(eDate));

            startDate.setText(formattedStartDate);
            endDate.setText(formattedEndDate);

            for (TrackingData data : trackingDataList) {
                if ((int) data.getDurationInSeconds() < minDuration) {
                    minDuration = (int) data.getDurationInSeconds();
                }

                if (data.getDurationInSeconds() > maxDuration) {
                    maxDuration = (int) data.getDurationInSeconds();
                }

                if (data.getTraveledDistanceList().last() < minDistance) {
                    minDistance = data.getTraveledDistanceList().last();
                }

                if (data.getTraveledDistanceList().last() > maxDistance) {
                    maxDistance = data.getTraveledDistanceList().last();
                }
            }

            minDistance = minDistance / 1000; // From m to km
            maxDistance = maxDistance / 1000; // From m to km

            distanceFrom.setText("");
            distanceFrom.setHint(String.format("%.3f", minDistance));
            distanceTo.setText("");
            distanceTo.setHint(String.format("%.3f", maxDistance));

            durationFrom.setText("");
            durationFrom.setHint(String.valueOf(minDuration));
            durationTo.setText("");
            durationTo.setHint(String.valueOf(maxDuration));
        }
        else {
            Toast.makeText(requireActivity(), "Empty database", Toast.LENGTH_LONG).show();
        }

    }

    private void buttonNowAction(Button buttonNow){
        buttonNow.setOnClickListener(view -> {

            TrackingFragment trackingFragment = new TrackingFragment();

            // Replace the current fragment with the new one
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, trackingFragment)
                    .addToBackStack(null) // Add this transaction to the back stack
                    .commit();

        });
    }
}
