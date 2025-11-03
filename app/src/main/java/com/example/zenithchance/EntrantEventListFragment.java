package com.example.zenithchance;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class for the list of events inside Entrant's My Events page.
 *
 * @author Percy
 * @version 1.0
 * @see Event
 * @see EventsAdapter
 */
public class EntrantEventListFragment extends Fragment {
    private boolean upcoming = true;
    private EventsAdapter adapter;
    private RecyclerView rv;
    private List<Event> list = new ArrayList<>();

    /**
     * Method to inflate fragment and attach adapter.
     *
     * @param inflater              The LayoutInflater object that can be used to inflate
     *                              any views in the fragment,
     * @param container             If non-null, this is the parent view that the fragment's
     *                              UI should be attached to.
     *                              The fragment should not add the view itself,
     *                              but this can be used to generate the
     *                              LayoutParams of the view.
     * @param savedInstanceState    If non-null, this fragment is being re-constructed
     *                              from a previous saved state as given here.
     *
     * @return                      Inflated view.
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // inflates fragment
        View frag = inflater.inflate(R.layout.entrant_event_list_fragment, container, false);

        // set fragment as vertical scroll list
        rv = frag.findViewById(R.id.recycler_events);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        // attach adapter to fragment
        adapter = new EventsAdapter();
        rv.setAdapter(adapter);

        return frag;
    }

    /**
     * This method updates the fragment with given list of events.
     *
     * @param events List of events to display.
     */
    public void setEvents(List<Event> events) {
        list.clear();
        list.addAll(events);
        filter();
    }

    /**
     * This method filters displaying data based on upcoming or past events.
     */
    private void filter() {
        List<Event> filtered = new ArrayList<>();
        Date now = new Date();

        for (Event e : list) {
            boolean isUpcoming = !e.date.before(now);   // today is also counted as "Upcoming"
            if (upcoming) { if (isUpcoming) filtered.add(e); }
            else { if (!isUpcoming) filtered.add(e); }
        }
        adapter.setItems(filtered);
    }

    /**
     * This method set the correct filter that user choser.
     *
     * @param newFilter The chosen filter.
     */
    public void setFilter(boolean newFilter) {
        upcoming = newFilter;
        filter();
    }
}

