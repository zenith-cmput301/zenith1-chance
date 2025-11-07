package com.example.zenithchance.navigation;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.zenithchance.R;
import com.example.zenithchance.fragments.AllEventsFragment;
import com.example.zenithchance.fragments.EntrantEventsFragment;
import com.example.zenithchance.fragments.ProfileFragment;
import com.example.zenithchance.models.Entrant;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Utility class to handle fragment navigation for entrant users.
 * The helper ensures the correct fragment is displayed when a tab is selected and
 * passes necessary data (like the current entrant) to fragments as arguments.
 * <p>
 * Provides a helper method to set up a {@link BottomNavigationView} with three main tabs:
 * <ul>
 *     <li>All Events</li>
 *     <li>My Events</li>
 *     <li>Profile</li>
 * </ul>
 *
 * @author Kiran
 * @version 1.0
 * @see androidx.fragment.app.Fragment
 * @see androidx.fragment.app.FragmentManager
 */
public class EntrantNavigationHelper {

    /**
     * Sets up bottom navigation for an entrant user in the provided activity.
     * <p>
     * Replaces the fragment container with the corresponding fragment when a tab is selected.
     * The default selected tab is "All Events".
     * </p>
     *
     * @param activity       The AppCompatActivity containing the fragment container and BottomNavigationView.
     * @param currentEntrant The currently logged-in entrant, passed to the My Events tab.
     */
    public static void setupBottomNav(AppCompatActivity activity, Entrant currentEntrant) {

        BottomNavigationView bottomNav = activity.findViewById(R.id.bottomNavigationView);

        // Set tab selection listener
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selected = null;

            int id = item.getItemId();
            if (id == R.id.nav_all_events) {
                selected = new AllEventsFragment();
            } else if (id == R.id.nav_my_events) {
                selected = new EntrantEventsFragment();
                Bundle args = new Bundle();
                args.putSerializable("entrant", currentEntrant);
                selected.setArguments(args);
            } else if (id == R.id.nav_profile) {
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
