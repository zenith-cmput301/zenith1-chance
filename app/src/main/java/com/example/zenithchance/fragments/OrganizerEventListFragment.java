package com.example.zenithchance.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zenithchance.R;
import com.example.zenithchance.activities.OrganizerEventDetailsFragment;
import com.example.zenithchance.adapters.AllEventsAdapter;
import com.example.zenithchance.models.Event;
import com.example.zenithchance.models.Organizer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Fragment that displays the list of events in Organizer's My Events page.
 */
public class OrganizerEventListFragment extends Fragment {

    private boolean upcoming = true;
    private AllEventsAdapter adapter;
    private RecyclerView rv;
    private ArrayList<Event> list = new ArrayList<>();
    private Organizer organizer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SimpleDateFormat fmt = new SimpleDateFormat("EEE, MMM d • h:mm a", Locale.getDefault());
        View frag = inflater.inflate(R.layout.fragment_organizer_events, container, false);

        rv = frag.findViewById(R.id.recycler_all_events);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new AllEventsAdapter(getContext(), new ArrayList<>(), event -> {
            // Create OrganizerEventDetailsFragment and pass event info
            OrganizerEventDetailsFragment fragment = new OrganizerEventDetailsFragment();
            Bundle bundle = new Bundle();
            bundle.putString("event_name", event.getName());
            bundle.putString("event_location", event.getLocation());
            bundle.putString("event_organizer", event.getOrganizer());
            bundle.putString("event_time", fmt.format(event.getDate()));
            bundle.putString("event_description", event.getDescription());
            bundle.putString("event_image_url", event.getImageUrl());
            bundle.putString("event_doc_id", event.getDocId());
            fragment.setArguments(bundle);

            // Open fragment
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        rv.setAdapter(adapter);

        // Get organizer from arguments (passed from parent activity)
        if (getArguments() != null) {
            organizer = (Organizer) getArguments().getSerializable("organizer");
        }

        // Load organizer events if organizer is not null
        if (organizer != null) {
            loadOrganizerEvents();
        }

        return frag;
    }

    /**
     * Sets or refreshes the list of events in the adapter.
     */
    public void setEvents(List<Event> events) {
        list.clear();
        list.addAll(events);
        filter();
    }

    /**
     * Filters the events based on upcoming/past selection.
     */
    private void filter() {
        List<Event> filtered = new ArrayList<>();
        Date now = new Date();

        for (Event e : list) {
            boolean isUpcoming = !e.getDate().before(now);
            if (upcoming && isUpcoming) filtered.add(e);
            else if (!upcoming && !isUpcoming) filtered.add(e);
        }
        adapter.updateList(filtered); // assuming AllEventsAdapter has updateList()
    }

    /**
     * Sets the filter (true = upcoming, false = past).
     */
    public void setFilter(boolean newFilter) {
        upcoming = newFilter;
        filter();
    }

    /**
     * Loads events for this organizer. Implement your Firestore query here.
     */
    private void loadOrganizerEvents() {
        // TODO: implement Firestore fetch for organizer's events
        // Once fetched, call setEvents(fetchedEvents);
    }
}
