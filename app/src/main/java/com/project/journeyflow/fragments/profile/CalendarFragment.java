package com.project.journeyflow.fragments.profile;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.project.journeyflow.R;
import com.project.journeyflow.items.ItemAdapter;
import com.project.journeyflow.query.profile.CalendarQuery;

import java.util.Calendar;
import java.util.Date;


public class CalendarFragment extends Fragment {

    private FloatingActionButton floatingActionButton;
    private TextView textViewDay, textViewMonth, textViewYear;
    private ItemAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewCalendar);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        CalendarQuery calendarQuery = new CalendarQuery(requireContext());

        initializeViews(view);

        getTodayJourneys(recyclerView, calendarQuery);

        getCalendar(recyclerView, calendarQuery);

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    private void initializeViews(View view) {
        floatingActionButton = view.findViewById(R.id.floatingActionButtonCalendar);
        textViewDay = view.findViewById(R.id.textViewCalendarDay);
        textViewMonth = view.findViewById(R.id.textViewCalendarMonth);
        textViewYear = view.findViewById(R.id.textViewCalendarYear);
    }

    private void getTodayJourneys(RecyclerView recyclerView, CalendarQuery calendarQuery) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date startOfDay = calendar.getTime();

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        Date endOfDay = calendar.getTime();

        //textViewDay.setText(String.valueOf(calendar.getTime().getDay()));

        adapter = new ItemAdapter(calendarQuery.getJourneysToday(startOfDay, endOfDay));
        recyclerView.setAdapter(adapter);
    }

    private void getCalendar(RecyclerView recyclerView, CalendarQuery calendarQuery) {
        //CalendarQuery calendarQuery = new CalendarQuery(requireContext());

        floatingActionButton.setOnClickListener(view -> {
            // on below line we are getting
            // the instance of our calendar.
            final Calendar c = Calendar.getInstance();

            // on below line we are getting
            // our day, month and year.
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // on below line we are creating a variable for date picker dialog.
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    // on below line we are passing context.
                    requireContext(),
                    (view1, year1, monthOfYear, dayOfMonth) -> {
                        // on below line we are setting date to our text view.
                        //textViewSelectedDate.setText(dayOfMonth + "." + (monthOfYear + 1) + "." + year1 + ".");

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year1, monthOfYear, dayOfMonth, 0, 0, 0); // Start of the day
                        calendar.set(Calendar.MILLISECOND, 0);
                        Date startDate = calendar.getTime();

                        calendar.set(year1, monthOfYear, dayOfMonth, 23, 59, 59); // End of the day
                        calendar.set(Calendar.MILLISECOND, 999);
                        Date endDate = calendar.getTime();

                        Log.d("START DATE", String.valueOf(startDate));
                        Log.d("END DATE", String.valueOf(endDate));

                        textViewDay.setText(String.valueOf(dayOfMonth));
                        textViewMonth.setText(String.valueOf(monthOfYear + 1));
                        textViewYear.setText(String.valueOf(year1));

                        //calendarQuery.getJourneysOnDate(startDate, endDate, recyclerView);
                        adapter = new ItemAdapter(calendarQuery.getJourneysOnDate(startDate, endDate));
                        recyclerView.setAdapter(adapter);
                    },
                    // on below line we are passing year,
                    // month and day for selected date in our date picker.
                    year, month, day);
            // at last we are calling show to
            // display our date picker dialog.
            datePickerDialog.show();
        });
    }
}
