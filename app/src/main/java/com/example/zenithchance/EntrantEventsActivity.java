package com.example.zenithchance;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents an Entrant's My Events page.
 *
 * @author Percy
 * @version 1.0
 * @see EntrantEventListFragment
 */
public class EntrantEventsActivity extends AppCompatActivity {
    private EntrantEventListFragment eventListFrag;
    private List<Event> eventList = new ArrayList<>();

    /**
     * This method initialize My Events page.
     * 1. Loads the list fragment (with actual data) to the container in XML.
     * 2. Fetch all events from Firebase.
     * 3. Wire buttons with functionalities.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_my_events);

        // inflate/recover the fragment
        FragmentManager fm = getSupportFragmentManager();
        eventListFrag = (EntrantEventListFragment) fm.findFragmentByTag("entrant_event_list");

        if (eventListFrag == null) {
            eventListFrag = new EntrantEventListFragment();
            fm.beginTransaction()
                    .replace(R.id.eventsFragmentContainer, eventListFrag, "entrant_event_list")
                    .commit();
            fm.executePendingTransactions(); // ensures fragment is attached before we assign data to it (from experiences)
        }

        // fetch all events
        getEvents();

        // wire buttons
        Button upcomingButton = findViewById(R.id.upcoming_events); // default
        Button pastButton = findViewById(R.id.past_events);
        upcomingButton.setEnabled(false); // by default upcoming events are shown first

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
    }

    /**
     * This function fetches all events from Firebase.
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
                    eventListFrag.setEvents(eventList);
                })
                .addOnFailureListener(e -> Log.e("EntrantEventsActivity", "Firestore load failed", e));
    }
}

