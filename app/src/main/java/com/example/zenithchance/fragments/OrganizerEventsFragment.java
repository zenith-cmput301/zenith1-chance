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
        String uid = organizer.getUserId();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(uid).get()
                .addOnSuccessListener(userSnap -> {
                    // Get the orgEvents list from the user document
                    List<String> orgEvents = (List<String>) userSnap.get("orgEvents");

                    // Handle null or empty case
                    if (orgEvents == null || orgEvents.isEmpty()) {
                        eventListFrag.setEvents(new ArrayList<>());
                        return;
                    }

                    // Split orgEvents into chunks of 10 for whereIn queries
                    List<Task<QuerySnapshot>> tasks = new ArrayList<>();
                    for (int i = 0; i < orgEvents.size(); i += 10) {
                        int end = Math.min(i + 10, orgEvents.size());
                        List<String> chunk = orgEvents.subList(i, end);
                        Task<QuerySnapshot> t = db.collection("events")
                                .whereIn(FieldPath.documentId(), chunk)
                                .get();
                        tasks.add(t);
                    }

                    // Wait for all queries to complete and combine results
                    Tasks.whenAllSuccess(tasks)
                            .addOnSuccessListener(results -> {
                                List<Event> merged = new ArrayList<>();
                                Set<String> seen = new HashSet<>();

                                for (Object obj : results) {
                                    QuerySnapshot qs = (QuerySnapshot) obj;
                                    for (DocumentSnapshot d : qs) {
                                        if (seen.add(d.getId())) {
                                            Event e = d.toObject(Event.class);
                                            if (e != null) {
                                                e.setDocId(d.getId());
                                                merged.add(e);
                                            }
                                        }
                                    }
                                }

                                // Update your fragment with the loaded events

                                Log.d("Org", "Setting the eventListFrag with " + String.valueOf(merged.size()));
                                eventListFrag.setEvents(merged);
                            })
                            .addOnFailureListener(e ->
                                    Log.e("Firestore", "Error fetching events", e)
                            );
                })
                .addOnFailureListener(e ->
                        Log.e("Firestore", "Error fetching user document", e)
                );
    }
}