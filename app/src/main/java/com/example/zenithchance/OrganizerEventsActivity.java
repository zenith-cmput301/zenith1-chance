package com.example.zenithchance;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;


/**
 * This class represents an Organizers's My Events page.
 *
 * @author Emerson
 * @version 1.0
 * @see OrganizerCreateEventFragment
 */
public class OrganizerEventsActivity extends AppCompatActivity {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_my_events);
        System.out.println("Made it to activity creation");

        Bundle bundle = new Bundle();

        // For testing purposes
        Event sampleEvent = createSampleEvent();

        bundle.putSerializable("event", sampleEvent);

        OrganizerCreateEventFragment createFragment = new OrganizerCreateEventFragment();
        createFragment.setArguments(bundle);


        // initialize list fragment into container
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.eventCreateFragmentContainer, createFragment)
                    .commit();
        }
    }


    /**
     * Method creates sample event for testing display of pre-existing values
     */
    private Event createSampleEvent() {

        Event sampleEvent = new Event(new Date(1), "Sample Event", "U of A", "waiting", "Me", "Test Description", Boolean.TRUE, new Date(2), 10);
        return sampleEvent;
    }
}
