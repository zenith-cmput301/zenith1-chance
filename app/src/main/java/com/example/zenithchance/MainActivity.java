package com.example.zenithchance;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zenithchance.activities.OrganizerEventsActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Go straight to My Events page
        startActivity(new Intent(this, OrganizerEventsActivity.class));

    }

}