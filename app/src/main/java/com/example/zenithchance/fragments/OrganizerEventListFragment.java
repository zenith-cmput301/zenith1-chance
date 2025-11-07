package com.example.zenithchance.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zenithchance.activities.EntrantEventDetailsActivity;
import com.example.zenithchance.R;
import com.example.zenithchance.adapters.AllEventsAdapter;
import com.example.zenithchance.adapters.EventsAdapter;
import com.example.zenithchance.interfaces.EntrantProviderInterface;
import com.example.zenithchance.models.Event;
import com.example.zenithchance.models.Organizer;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Class for the list of events inside Organizer's My Events page.
 *
 * @author Emerson
 * @version 1.0
 * @see Event
 * @see EventsAdapter
 */
public class OrganizerEventListFragment extends Fragment {
//    private boolean upcoming = true;
//    private EventsAdapter adapter;
//    private RecyclerView rv;
//    private List<Event> list = new ArrayList<>();
//
//    /**
//     * Method to inflate fragment and attach adapter.
//     *
//     * @param inflater              The LayoutInflater object that can be used to inflate
//     *                              any views in the fragment,
//     * @param container             If non-null, this is the parent view that the fragment's
//     *                              UI should be attached to.
//     *                              The fragment should not add the view itself,
//     *                              but this can be used to generate the
//     *                              LayoutParams of the view.
//     * @param savedInstanceState    If non-null, this fragment is being re-constructed
//     *                              from a previous saved state as given here.
//     *
//     * @return                      Inflated view.
//     */
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        SimpleDateFormat fmt = new SimpleDateFormat("EEE, MMM d • h:mm a", Locale.getDefault());
//        View frag = inflater.inflate(R.layout.entrant_event_list_fragment, container, false);
//
//        // set fragment as vertical scroll list
//        rv = frag.findViewById(R.id.recycler_events);
//        rv.setLayoutManager(new LinearLayoutManager(getContext()));
//
//        // attach adapter to fragment
//        adapter = new EventsAdapter(new ArrayList<>(), event -> {
//            EntrantEventDetailsFragment fragment = new EntrantEventDetailsFragment();
//            // get info to fill
//            Bundle bundle = new Bundle();
//            bundle.putString("event_name", event.getName());
//            bundle.putString("event_location", event.getLocation());
//            bundle.putString("event_organizer", event.getOrganizer());
//            bundle.putString("event_time", fmt.format(event.getDate()));
//            bundle.putString("event_description", event.getDescription());
//            bundle.putString("event_image_url", event.getImageUrl());
//            bundle.putString("event_doc_id", event.getDocId());
//            fragment.setArguments(bundle);
//
//            // boot up fragment
//            requireActivity().getSupportFragmentManager()
//                    .beginTransaction()
//                    .replace(R.id.fragmentContainer, fragment)
//                    .addToBackStack(null)
//                    .commit();
//        });
//        rv.setAdapter(adapter);
//
//        return frag;
//    }
//
//    /**
//     * This method updates the fragment with given list of events.
//     *
//     * @param events List of events to display.
//     */
//    public void setEvents(List<Event> events) {
//        list.clear();
//        list.addAll(events);
//        filter();
//    }
//
//    /**
//     * This method filters displaying data based on upcoming or past events.
//     */
//    private void filter() {
//        List<Event> filtered = new ArrayList<>();
//        Date now = new Date();
//
//        for (Event e : list) {
//            boolean isUpcoming = !e.getDate().before(now);   // today is also counted as "Upcoming"
//            if (upcoming) { if (isUpcoming) filtered.add(e); }
//            else { if (!isUpcoming) filtered.add(e); }
//        }
//        adapter.setItems(filtered);
//    }
//
//    /**
//     * This method set the correct filter that user have chosen.
//     *
//     * @param newFilter The chosen filter. true if upcoming.
//     */
//    public void setFilter(boolean newFilter) {
//        upcoming = newFilter;
//        filter();
//    }
//}
//
    private RecyclerView recyclerView;
    private AllEventsAdapter adapter;
    private List<Event> events = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Organizer organizer;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_events, container, false);

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
