package com.example.zenithchance.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.zenithchance.R;
/**
 * Creates the fragment and has methods to populate UI
 *
 */
public class OrganizerEventDetailsFragment extends Fragment {

    private TextView tvEventName, tvEventDate, tvEventTime, tvLocation, tvOrganizer, tvAboutDescription;
    private Button btnPeople, btnEdit;

    private String eventId, eventName, eventDate, eventTime, eventLocation, eventOrganizer, eventDescription;

    public OrganizerEventDetailsFragment() {
        // Required empty public constructor
    }
/**
 * onCreateView
 *
 * @param inflater
 * @param container
 * @param savedInstanceState
 * @return View
 */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout
        return inflater.inflate(R.layout.organizer_event_details, container, false);
    }
/**
 * onViewCreated
 *
 * @param view
 * @param savedInstanceState
 * @return void
 */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        tvEventName = view.findViewById(R.id.tvEventName);
        tvEventDate = view.findViewById(R.id.tvEventDate);
        tvEventTime = view.findViewById(R.id.tvEventTime);
        tvLocation = view.findViewById(R.id.tvLocation);
        tvOrganizer = view.findViewById(R.id.tvOrganizer);
        tvAboutDescription = view.findViewById(R.id.tvAboutDescription);
        btnPeople = view.findViewById(R.id.btnPeople);
        btnEdit = view.findViewById(R.id.btnEdit);

        // Retrieve arguments
        if (getArguments() != null) {
            eventId = getArguments().getString("event_doc_id");
            eventName = getArguments().getString("event_name");
            eventDate = getArguments().getString("event_date");
            eventTime = getArguments().getString("event_time");
            eventLocation = getArguments().getString("event_location");
            eventOrganizer = getArguments().getString("event_organizer");
            eventDescription = getArguments().getString("event_description");
        }

        // Populate UI
        populateUI();

        // Button listeners
        btnPeople.setOnClickListener(v ->
                Toast.makeText(requireContext(), "Open People screen (to be implemented)", Toast.LENGTH_SHORT).show()
        );

        btnEdit.setOnClickListener(v ->
                Toast.makeText(requireContext(), "Edit event (to be implemented)", Toast.LENGTH_SHORT).show()
        );
    }
/**
 * populateUI, sets text for UI
 *
 * @return void
 */
    private void populateUI() {
        tvEventName.setText(eventName != null ? eventName : "No Event Name");
        tvEventDate.setText(eventDate != null ? eventDate : "No Date");
        tvEventTime.setText(eventTime != null ? eventTime : "No Time");
        tvLocation.setText(eventLocation != null ? eventLocation : "No Location");
        tvOrganizer.setText(eventOrganizer != null ? eventOrganizer : "No Organizer");
        tvAboutDescription.setText(eventDescription != null ? eventDescription : "No Description");
    }
}
