package com.example.zenithchance.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zenithchance.R;
import com.example.zenithchance.adapters.AllEventsAdapter;
import com.example.zenithchance.adapters.EventsAdapter;
import com.example.zenithchance.models.Event;
import com.example.zenithchance.models.Organizer;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Class for the list of events inside Organizer's My Events page.
 * Utilizes logic and recyclerView from AllEvents
 *
 * @author Emerson
 * @version 1.0
 * @see Event
 * @see EventsAdapter
 */
public class OrganizerEventListFragment extends Fragment {
    private RecyclerView recyclerView;
    private AllEventsAdapter adapter;
    private List<Event> events = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Organizer organizer;


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
        View view = inflater.inflate(R.layout.fragment_organizer_events, container, false);

        recyclerView = view.findViewById(R.id.recycler_all_events);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        SimpleDateFormat fmt = new SimpleDateFormat("EEE, MMM d • h:mm a", Locale.getDefault());

        // Create adapter
        adapter = new AllEventsAdapter(requireContext(), events, event -> {

            /**
             *
             * Aayush, this is where the events details can be populated, currently it's using the Entrant details
             * but you can change it to inflate your fragments instead.
             *
             */

            EntrantEventDetailsFragment fragment = new EntrantEventDetailsFragment();

            Bundle bundle = new Bundle();
            bundle.putString("event_name", event.getName());
            bundle.putString("event_location", event.getLocation());
            bundle.putString("event_organizer", event.getOrganizer());
            bundle.putString("event_time", fmt.format(event.getDate()));
            bundle.putString("event_description", event.getDescription());
            bundle.putString("event_image_url", event.getImageUrl());
            bundle.putString("event_doc_id", event.getDocId());
            fragment.setArguments(bundle);


            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        recyclerView.setAdapter(adapter);

        Bundle args = getArguments();
        organizer = (Organizer) args.getSerializable("organizer");

        getEvents();

        return view;
    }


    /**
     * This method queries FireBase and adds all events that an Organizer has contained in their orgEvents field
     * to the MyEvents display.
     */
    private void getEvents() {

    String uid = organizer.getUserId();

    // Get list of orgEvents from user collection

    db.collection("users").document(uid).get()
            .addOnSuccessListener(userDocument -> {

                ArrayList<String> organizerEventIds = (ArrayList<String>) userDocument.get("orgEvents");

                // If orgEvents is empty, populated an empty list and returns

                if (organizerEventIds == null || organizerEventIds.isEmpty()) {
                    adapter.updateList(new ArrayList<>());
                    return;
                }

                // Finds all events in the database

                db.collection("events")
                        .orderBy("date")
                        .get()
                        .addOnSuccessListener(allEventsSnapshot -> {
                            ArrayList<Event> filteredList = new ArrayList<>();

                            // Loops through all events and checks if they exist in the orgEvents

                            for (DocumentSnapshot eventDoc : allEventsSnapshot) {

                                if (organizerEventIds.contains(eventDoc.getId())) {
                                    Event event = eventDoc.toObject(Event.class);
                                    if (event != null) {
                                        event.setDocId(eventDoc.getId());
                                        filteredList.add(event);
                                    }
                                }
                            }
                            adapter.updateList(filteredList);

                        });
            });
    }
}
