package com.example.zenithchance;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class EntrantEventsActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_my_events);

        // Load list fragment into the container
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.eventsFragmentContainer, new EntrantEventListFragment())
                    .commit();
        }
    }
}

