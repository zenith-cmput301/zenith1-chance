package com.example.zenithchance;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EntrantEventDetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_event_details);

        TextView name = findViewById(R.id.event_name);
        TextView location = findViewById(R.id.location);
        // TextView time = findViewById(R.id.time);
        // TextView desc = findViewById(R.id.description);

        Intent i = getIntent();
        name.setText(i.getStringExtra("event_name"));
        location.setText(i.getStringExtra("event_location"));
        // status.setText(i.getStringExtra("event_status"));
        // time.setText(i.getStringExtra("event_time"));
        // desc.setText(i.getStringExtra("event_description"));
    }
}
