package com.example.zenithchance.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.zenithchance.OrganizerMainActivity;
import com.example.zenithchance.R;
import com.example.zenithchance.models.Event;
import com.example.zenithchance.models.Organizer;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OrganizerEventsFragment extends Fragment {
    private OrganizerEventListFragment eventListFrag;
    private List<Event> eventList = new ArrayList<>();

    private Organizer organizer;

    Button createEventButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_organizer_my_events, container, false);

        // Inflate
        FragmentManager fm = getChildFragmentManager(); // IMPORTANT: child fragment
        eventListFrag = new OrganizerEventListFragment();

        fm.beginTransaction()
                .replace(R.id.eventsFragmentContainer, eventListFrag)
                .commit();
        fm.executePendingTransactions();


        // Sets the organizer to be the organizer signed in
        if (getActivity() instanceof OrganizerMainActivity) {
            organizer = ((OrganizerMainActivity) getActivity()).getOrganizer();
        }

        // Fetch all events
        getEvents();


        // buttons
//        TODO: Handle if no upcoming/past events
//        TODO: Show event details on clicking an event
        Button upcomingButton = view.findViewById(R.id.upcoming_events);
        Button pastButton = view.findViewById(R.id.past_events);
        createEventButton = view.findViewById(R.id.create_event_button);
        upcomingButton.setEnabled(false);

        upcomingButton.setOnClickListener(v -> {
//            eventListFrag.setFilter(true);
            upcomingButton.setEnabled(false);
            pastButton.setEnabled(true);
        });

        pastButton.setOnClickListener(v -> {
//            eventListFrag.setFilter(false);
            pastButton.setEnabled(false);
            upcomingButton.setEnabled(true);
        });

        // Replaces the EventsFragment with a CreateEvent fragment when the create button is clicked

        initCreateEventButton();

        getEvents();

        return view;
    }

    private void initCreateEventButton() {
        createEventButton.setOnClickListener(v -> {

            // Initializing a bundle to pass the organizer user
            Bundle bundle = new Bundle();
            bundle.putSerializable("organizer", organizer);

            OrganizerCreateEventFragment createFragment = new OrganizerCreateEventFragment();
            createFragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, createFragment)
                    .commit();
        });

    }

    private void getEvents() {
        // 1. Ensure the organizer object and its UID are valid before querying.
        if (organizer == null || organizer.getUserId() == null || organizer.getUserId().isEmpty()) {
            Log.e("OrganizerEventsFragment", "Cannot fetch events: Organizer or Organizer UID is null.");
            // If the organizer is invalid, show an empty list.
            if (eventListFrag != null) {
                // Assuming the method is named setItems based on our previous discussion
                eventListFrag.setEvents(new ArrayList<>());
            }
            return;
        }

        String organizerId = organizer.getUserId();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Log.d("OrganizerEventsFragment", "Fetching events from Firestore for organizerId: " + organizerId);

        // 2. Query the "events" collection directly.
        db.collection("events")

                .whereEqualTo("organizer", organizer.getName())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    List<Event> events = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Event event = document.toObject(Event.class);
                        if (event != null) {
                            event.setDocId(document.getId());
                            events.add(event);
                            Log.d("eventname", String.valueOf(events.size()));
                        } else {
                            Log.w("OrganizerEventsFragment", "Document " + document.getId() + " could not be converted to an Event object.");
                        }
                    }

                    eventListFrag.setEvents(events);
                });

    }
}