package com.example.zenithchance.navigation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.zenithchance.R;
import com.example.zenithchance.fragments.AllEventsFragment;
import com.example.zenithchance.fragments.AdminMenuFragment;
import com.example.zenithchance.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Class to simplify fragment navigation between Admin Menu and Profile tabs
 * uses BottomNavigationView for better design and utility.
 * This class ensures that the selected tab is being displayed.
 * @author Kiran
 * @version 1.0
 * @see androidx.fragment.app.Fragment
 * @see androidx.fragment.app.FragmentManager
 */
public class AdminNavigationHelper {
    public static void setupBottomNav(AppCompatActivity activity) {
        BottomNavigationView bottomNav = activity.findViewById(R.id.adminBottomNavigationView);

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selected = null;

            int id = item.getItemId();
            if (id == R.id.nav_admin_menu) {
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

        bottomNav.setSelectedItemId(R.id.nav_admin_menu);
    }
}
