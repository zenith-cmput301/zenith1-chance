package com.example.zenithchance;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * This class represents an Entrant's My Events page.
 *
 * @author Percy
 * @version 1.0
 * @see EntrantEventListFragment
 */
public class EntrantEventsActivity extends AppCompatActivity {

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

        // initialize list fragment into container
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.eventsFragmentContainer, new EntrantEventListFragment())
                    .commit();
        }
    }
}

