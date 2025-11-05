package com.example.zenithchance.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zenithchance.R;

public class EntrantEventDetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_event_details);

        TextView name = findViewById(R.id.event_name);
        TextView location = findViewById(R.id.location);
        TextView organizer = findViewById(R.id.organizer_name);
        TextView time = findViewById(R.id.time);
        TextView desc = findViewById(R.id.description);

        Intent i = getIntent();
        name.setText(i.getStringExtra("event_name"));
        location.setText(i.getStringExtra("event_location"));
        organizer.setText(i.getStringExtra("event_organizer"));
        time.setText(i.getStringExtra("event_time"));
        desc.setText(i.getStringExtra("event_description"));
    }
}
