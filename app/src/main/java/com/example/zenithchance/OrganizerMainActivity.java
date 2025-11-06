package com.example.zenithchance;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import com.example.zenithchance.fragments.OrganizerCreateEventFragment;
import com.example.zenithchance.models.Event;
import com.example.zenithchance.navigation.EntrantNavigationHelper;
import com.example.zenithchance.navigation.OrganizerNavigationHelper;

import java.util.Date;


/**
 * This class represents an Organizers's My Events page.
 *
 * @author Emerson
 * @version 1.0
 * @see OrganizerCreateEventFragment
 */
public class OrganizerMainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_main);

        // Set up the bottom navigation using the helper
        OrganizerNavigationHelper.setupBottomNav(this);
    }
}

//
//@Override protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.organizer_my_events);
//
//        Bundle bundle = new Bundle();
//
//        Button createButton = findViewById(R.id.create_event_button);
//
//        createButton.setOnClickListener(v->{
//            // For testing purposes
//            Event sampleEvent = createSampleEvent();
//
//            bundle.putSerializable("event", sampleEvent);
//
//            OrganizerCreateEventFragment createFragment = new OrganizerCreateEventFragment();
//            createFragment.setArguments(bundle);
//
//            // initialize list fragment into container
//            if (savedInstanceState == null) {
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.eventCreateFragmentContainer, createFragment)
//                        .commit();
//            }
//
//            findViewById(R.id.organizer_events_main_layout).setVisibility(View.INVISIBLE);
//        });
//
//    }
//
//
//    /**
//     * Method creates sample event for testing display of pre-existing values
//     */
//    private Event createSampleEvent() {
//
//        Event sampleEvent = new Event(new Date(1), "Sample Event", "U of A", "waiting", "Me", "Test Description", Boolean.TRUE, new Date(2), 10, null);
//        return sampleEvent;
//    }
//}
