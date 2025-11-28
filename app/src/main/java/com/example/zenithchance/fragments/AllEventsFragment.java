package com.example.zenithchance.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.zenithchance.adapters.AllEventsAdapter;
import com.example.zenithchance.interfaces.UserProviderInterface;
import com.example.zenithchance.models.Event;
import com.example.zenithchance.models.User;
import com.example.zenithchance.navigation.EntrantNavigationHelper;
import com.example.zenithchance.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Date;

/**
 * This fragment displays all available events to Entrants
 *
 * @author Kira, Percy
 * @version 1.0
 * @see AllEventsAdapter
 * @see EntrantNavigationHelper
 * @see AdminMenuFragment
 */
public class AllEventsFragment extends Fragment {
    private RecyclerView recyclerView;
    private AllEventsAdapter adapter;
    private List<Event> events = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // for filtering events
    private MaterialButton startDateButton;
    private MaterialButton endDateButton;
    private MaterialButton clearDatesButton;
    private Date startDateFilter = null;
    private Date endDateFilter = null;

    // date formats
    private SimpleDateFormat fmt = new SimpleDateFormat("EEE, MMM d • h:mm a", Locale.getDefault());
    private SimpleDateFormat displayDateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());

    /**
     * This method defines what happens when this fragment is created
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return View to display
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_events, container, false);

        // recycler view
        recyclerView = view.findViewById(R.id.recycler_all_events);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // buttons + their listeners
        startDateButton = view.findViewById(R.id.btn_start_date);
        endDateButton = view.findViewById(R.id.btn_end_date);
        clearDatesButton = view.findViewById(R.id.btn_clear_dates);

        startDateButton.setOnClickListener(v -> showDatePicker(true));
        endDateButton.setOnClickListener(v -> showDatePicker(false));
        clearDatesButton.setOnClickListener(v -> clearDateFilters());

        adapter = new AllEventsAdapter(requireContext(), events, event -> {

            // Setup
            User currentUser = null;
            if (requireActivity() instanceof UserProviderInterface) currentUser = ((UserProviderInterface) requireActivity()).getCurrentUser();
            if (currentUser == null) return;

            // Bundle data to send to EventDetailsFragment
            Bundle bundle = new Bundle();
            bundle.putString("event_name", event.getName());
            bundle.putString("event_location", event.getLocation());
            bundle.putString("event_organizer", event.getOrganizer());

            String formattedDate = (event.getDate() != null) ? fmt.format(event.getDate()) : "Date not available";
            bundle.putString("event_time", formattedDate);

            bundle.putString("event_description", event.getDescription());
            bundle.putString("event_image_url", event.getImageUrl());
            bundle.putString("event_doc_id", event.getDocId());

            // Decide which EventDetailsFragment to show based on user type
            Fragment targetFragment;
            if ("admin".equalsIgnoreCase(currentUser.getType())) {
                targetFragment = new AdminEventDetailsFragment();
            } else {
                targetFragment = new EntrantEventDetailsFragment();
            }

            targetFragment.setArguments(bundle);

            // Determine which container to replace based on activity layout
            int containerId;
            if (requireActivity().findViewById(R.id.adminFragmentContainer) != null) {
                containerId = R.id.adminFragmentContainer;
            } else {
                containerId = R.id.fragmentContainer;
            }

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(containerId, targetFragment)
                    .addToBackStack(null)
                    .commit();
        });

        recyclerView.setAdapter(adapter);

        loadAllEvents();
        return view;
    }

    /**
     * This method fetches all events from Firestore to local list of events
     */
    private void loadAllEvents() {
        db.collection("events")
                .orderBy("date")
                .get()
                .addOnSuccessListener(snaps -> {
                    events.clear();
                    for (DocumentSnapshot doc : snaps) {
                        Event event = doc.toObject(Event.class);
                        if (event != null) {
                            event.setDocId(doc.getId());
                            events.add(event);
                        }
                    }
                    // adapter.updateList(eventList);
                    applyDateFilter();
                })
                .addOnFailureListener(e -> Log.e("AllEventsFragment", "Error fetching events", e));
    }

    /**
     * Display and set dates for start/end dates
     *
     * @param isStartDate   true if for start date, false otherwise
     */
    private void showDatePicker(boolean isStartDate) {
        Calendar cal = Calendar.getInstance();

        // Default date - today
        Date current = isStartDate ? startDateFilter : endDateFilter;
        if (current != null) cal.setTime(current);

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        // Opens calendar for fun, interactive date picking
        DatePickerDialog dialog = new DatePickerDialog(
                requireContext(),
                (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
                    Calendar picked = Calendar.getInstance();
                    if (isStartDate) { // start date
                        picked.set(selectedYear, selectedMonth, selectedDay, 0, 0, 0);
                        picked.set(Calendar.MILLISECOND, 0);
                        startDateFilter = picked.getTime();
                        startDateButton.setText(displayDateFormat.format(startDateFilter));
                    } else { // end date
                        picked.set(selectedYear, selectedMonth, selectedDay, 23, 59, 59);
                        picked.set(Calendar.MILLISECOND, 999);
                        endDateFilter = picked.getTime();
                        endDateButton.setText(displayDateFormat.format(endDateFilter));
                    }
                    applyDateFilter();
                },
                year, month, day
        );

        dialog.show();
    }

    /**
     * Clears date filters
     */
    private void clearDateFilters() {
        startDateFilter = null;
        endDateFilter = null;
        startDateButton.setText("Start date");
        endDateButton.setText("End date");
        applyDateFilter();
    }

    /**
     * Apply set filters to list of events
     */
    private void applyDateFilter() {
        if (events.isEmpty()) {
            adapter.updateList(new ArrayList<>());
            return;
        }

        // No filter
        if (startDateFilter == null && endDateFilter == null) {
            adapter.updateList(new ArrayList<>(events));
            return;
        }

        // Filter
        List<Event> filtered = new ArrayList<>();
        for (Event event : events) {
            Date d = event.getDate();
            boolean inRange = true;

            if (startDateFilter != null && d.before(startDateFilter)) inRange = false;
            if (endDateFilter != null && d.after(endDateFilter)) inRange = false;

            if (inRange) filtered.add(event);
        }
        adapter.updateList(filtered);
    }
}
