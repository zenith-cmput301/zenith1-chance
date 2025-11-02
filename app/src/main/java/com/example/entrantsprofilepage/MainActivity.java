package com.example.entrantsprofilepage;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selected = null;
            switch (item.getItemId()) {
                case R.id.nav_all_events:
                    selected = new AllEventsFragment();
                    break;
                case R.id.nav_my_events:
                    selected = new MyEventsFragment();
                    break;
                case R.id.nav_profile:
                    selected = new ProfileFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, selected)
                    .commit();
            return true;
        });

        // Default fragment
        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_all_events);
        }
    }
}