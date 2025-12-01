package com.example.zenithchance.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;

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
import com.google.android.material.textfield.TextInputEditText;
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

    private ImageView scannerButton;

    // date formats
    private SimpleDateFormat fmt = new SimpleDateFormat("EEE, MMM d • h:mm a", Locale.getDefault());
    private SimpleDateFormat displayDateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());

    // for searching events
    private TextInputEditText searchEditText;
    private String searchQuery = "";


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

        // Recycler view
        recyclerView = view.findViewById(R.id.recycler_all_events);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Buttons + their listeners
        startDateButton = view.findViewById(R.id.btn_start_date);
        endDateButton = view.findViewById(R.id.btn_end_date);
        clearDatesButton = view.findViewById(R.id.btn_clear_dates);
        scannerButton = view.findViewById(R.id.qr_scanner_button);


        startDateButton.setOnClickListener(v -> showDatePicker(true));
        endDateButton.setOnClickListener(v -> showDatePicker(false));
        clearDatesButton.setOnClickListener(v -> clearDateFilters());

        scannerButton.setOnClickListener(v -> {
            QRScannerFragment createFragment = new QRScannerFragment();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, createFragment)
                    .commit();
                });

        // Search bar + set listener
        searchEditText = view.findViewById(R.id.search_edit_text);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s.toString().trim().toLowerCase(Locale.getDefault());
                applyFilters();
            }
        });

        // Setup adapter
        adapter = new AllEventsAdapter(requireContext(), events, event -> {
            // Setup
            User currentUser = null;
            if (requireActivity() instanceof UserProviderInterface) currentUser = ((UserProviderInterface) requireActivity()).getCurrentUser();
            if (currentUser == null) return;

            // Bundle data to send to EventDetailsFragment
            Bundle bundle = new Bundle();
            bundle.putSerializable("event", event);
            bundle.putString("event_name", event.getName());
            bundle.putString("event_location", event.getLocation());
            bundle.putString("event_organizer", event.getOrganizer());

            String formattedDate = (event.getDate() != null) ? fmt.format(event.getDate()) : "Date not available";
            bundle.putString("event_time", formattedDate);
            bundle.putLong("event_date_millis", event.getDate().getTime());

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
                    applyFilters();
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
                    applyFilters();
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
        searchQuery = "";
        searchEditText.setText("");
        applyFilters();
    }

    /**
     * Apply set filters to list of events
     */
    private void applyFilters() {
        if (events.isEmpty()) {
            adapter.updateList(new ArrayList<>());
            return;
        }

        // Filter date + name
        List<Event> filtered = new ArrayList<>();
        for (Event event : events) {
            // Date filter
            Date d = event.getDate();
            Date now = new Date();
            if (d.before(now)) continue;
            if (startDateFilter != null && d.before(startDateFilter)) continue;
            if (endDateFilter != null && d.after(endDateFilter)) continue;

            // Name + description filter
            String eventName = event.getName();
            String description = event.getDescription();
            boolean match = true;
            if (!searchQuery.isEmpty()) {
                match = false;
                if (eventName.toLowerCase(Locale.getDefault()).contains(searchQuery.toLowerCase())) {
                    match = true;
                }
                if (description.toLowerCase(Locale.getDefault()).contains(searchQuery.toLowerCase())) {
                    match = true;
                }
            }

            if (match) filtered.add(event);
        }
        adapter.updateList(filtered);
    }
}
