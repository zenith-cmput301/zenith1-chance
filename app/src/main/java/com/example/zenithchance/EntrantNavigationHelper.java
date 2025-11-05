package com.example.zenithchance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.zenithchance.fragments.AllEventsFragment;
import com.example.zenithchance.fragments.EntrantEventListFragment;
import com.example.zenithchance.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EntrantNavigationHelper {

    public static void setupBottomNav(AppCompatActivity activity) {

        BottomNavigationView bottomNav = activity.findViewById(R.id.bottomNavigationView);

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selected = null;

            int id = item.getItemId();
            if (id == R.id.nav_all_events) {
                selected = new AllEventsFragment();
            }
            else if (id == R.id.nav_my_events) {
                selected = new EntrantEventListFragment();
            }
            else if (id == R.id.nav_profile) {
                selected = new ProfileFragment();
            }

            if (selected != null) {
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, selected)
                        .commit();
                return true;
            }

            return false;
        });

        // Set default tab to All Events
        bottomNav.setSelectedItemId(R.id.nav_all_events);
    }
}
