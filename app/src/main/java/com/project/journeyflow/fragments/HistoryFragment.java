package com.project.journeyflow.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.project.journeyflow.R;
import com.project.journeyflow.items.ItemAdapter;
import com.project.journeyflow.query.HistoryFragmentQuery;

public class HistoryFragment extends Fragment {

    private FloatingActionButton fabFilter;
    private RecyclerView recyclerViewHistory;
    private TextView textViewHistoryMessage;
    private Button buttonNow;
    private ItemAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        HistoryFragmentQuery query = new HistoryFragmentQuery(requireActivity());

        initializeViews(view);

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

    private void getValueFromFabFilter(FloatingActionButton fabFilter) {
        fabFilter.setOnClickListener(view -> {

        });
    }

    private void getFilteredJourneys(HistoryFragmentQuery query, FloatingActionButton fabFilter) {

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
