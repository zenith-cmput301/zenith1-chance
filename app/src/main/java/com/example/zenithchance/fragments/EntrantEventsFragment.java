package com.example.zenithchance.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.zenithchance.R;
import com.example.zenithchance.models.Event;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class EntrantEventsFragment extends Fragment {

    private EntrantEventListFragment eventListFrag;
    private List<Event> eventList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_entrant_my_events, container, false);

        // Inflate
        FragmentManager fm = getChildFragmentManager(); // IMPORTANT: child fragment
        eventListFrag = (EntrantEventListFragment) fm.findFragmentByTag("entrant_event_list");

        if (eventListFrag == null) {
            eventListFrag = new EntrantEventListFragment();
            fm.beginTransaction()
                    .replace(R.id.eventsFragmentContainer, eventListFrag, "entrant_event_list")
                    .commit();
            fm.executePendingTransactions();
        }

        // Fetch all events
        getEvents();

        // buttons
//        TODO: Handle if no upcoming/past events
//        TODO: Show event details on clicking an event
        Button upcomingButton = view.findViewById(R.id.upcoming_events);
        Button pastButton = view.findViewById(R.id.past_events);
        upcomingButton.setEnabled(false);

        upcomingButton.setOnClickListener(v -> {
            eventListFrag.setFilter(true);
            upcomingButton.setEnabled(false);
            pastButton.setEnabled(true);
        });

        pastButton.setOnClickListener(v -> {
            eventListFrag.setFilter(false);
            pastButton.setEnabled(false);
            upcomingButton.setEnabled(true);
        });

        return view;
    }

    /**
     * This method fetch all events on Firestore.
     */
    private void getEvents() {
        FirebaseFirestore.getInstance()
                .collection("events")
                .orderBy("date")
                .get()
                .addOnSuccessListener(snaps -> {
                    eventList.clear();
                    for (DocumentSnapshot d : snaps) {
                        Event e = d.toObject(Event.class);
                        if (e != null) eventList.add(e);
                    }
                    if (eventListFrag != null) eventListFrag.setEvents(eventList);
                });
    }
}
