package com.example.zenithchance.navigation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.zenithchance.R;
import com.example.zenithchance.fragments.AllEventsFragment;
import com.example.zenithchance.fragments.AdminMenuFragment;
import com.example.zenithchance.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminNavigationHelper {

    public static void setupBottomNav(AppCompatActivity activity) {
        BottomNavigationView bottomNav = activity.findViewById(R.id.adminBottomNavigationView);

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selected = null;

            int id = item.getItemId();
            if (id == R.id.nav_admin_all_events) {
                selected = new AllEventsFragment();
            } else if (id == R.id.nav_admin_menu) {
                selected = new AdminMenuFragment();
            } else if (id == R.id.nav_admin_profile) {
                selected = new ProfileFragment();
            }

            if (selected != null) {
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.adminFragmentContainer, selected)
                        .commit();
                return true;
            }

            return false;
        });
    }
}
