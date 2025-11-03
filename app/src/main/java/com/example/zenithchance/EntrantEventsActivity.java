package com.example.zenithchance;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

/**
 * This class represents an Entrant's My Events page.
 *
 * @author Percy
 * @version 1.0
 * @see EntrantEventListFragment
 */
public class EntrantEventsActivity extends AppCompatActivity {
    private EntrantEventListFragment eventListFrag;

    /**
     * This method initialize My Events page.
     * It also loads the list fragment (with actual data) to the container in XML.
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
        }

        // wire buttons
        Button upcomingButton = findViewById(R.id.upcoming_events); // default
        Button pastButton = findViewById(R.id.past_events);

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
}

