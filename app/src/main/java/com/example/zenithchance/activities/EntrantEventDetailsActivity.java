package com.example.zenithchance.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.zenithchance.R;

/*
 * WORK TO DO: Connect this page with All Events
 * - User should see sign-up, cancel waitlist etc options on clicking event details
 * - Show event details when an event is clicked from All Events or My Events or Organizer My Events
 * */

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

        //  To display the image/poster in Event Details
        ImageView image = findViewById(R.id.header_image);
        String imageUrl = i.getStringExtra("event_image_url");
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_my_events)
                .into(image);

        name.setText(i.getStringExtra("event_name"));
        location.setText(i.getStringExtra("event_location"));
        organizer.setText(i.getStringExtra("event_organizer"));
        time.setText(i.getStringExtra("event_time"));
        desc.setText(i.getStringExtra("event_description"));
    }
}
