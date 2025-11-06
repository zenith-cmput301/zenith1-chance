package com.example.zenithchance.navigation;

// TODO : To be implemented. See EntrantMainActivity, EntrantNavigationHelper, and entrant_nav_menu.xml for example.-->

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.zenithchance.R;
import com.example.zenithchance.fragments.AllEventsFragment;
import com.example.zenithchance.fragments.EntrantEventsFragment;
import com.example.zenithchance.fragments.OrganizerEventsFragment;
import com.example.zenithchance.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Class to simplify fragment navigation between My Events and Profile tabs
 * uses BottomNavigationView for better design and utility.
 * This class ensures that the selected tab is being displayed.
 * @author Emerson
 * @version 1.0
 * @see androidx.fragment.app.Fragment
 * @see androidx.fragment.app.FragmentManager
 */
public class OrganizerNavigationHelper {

    public static void setupBottomNav(AppCompatActivity activity) {

        BottomNavigationView bottomNav = activity.findViewById(R.id.bottomNavigationView);

//        MUST USE FRAGMENTS for main tabs
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selected = null;

            int id = item.getItemId();
//            if (id == R.id.nav_all_events) {
//                selected = new AllEventsFragment();
//            }
            if (id == R.id.nav_my_events) {
                selected = new OrganizerEventsFragment();
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
        bottomNav.setSelectedItemId(R.id.nav_profile);
    }
}
