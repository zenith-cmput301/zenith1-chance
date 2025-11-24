package com.example.zenithchance.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.zenithchance.AdminMainActivity;
import com.example.zenithchance.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Fragment representing the main menu for admin users.
 * Provides buttons for navigating to other admin features such as: Browse Profiles, Browse Events
 *
 * Handles navigation by replacing fragments in the {@link AdminMainActivity}'s container.
 *
 * Future Updates:
 * We will add Browse Notification logs and Browse Images in this Menu
 */
public class AdminMenuFragment extends Fragment {

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views.
     * @param container          If non-null, this is the parent view that the fragment's UI should attach to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_menu, container, false);

        // Initialize UI components
        Button browseProfilesBtn = view.findViewById(R.id.browseProfilesButton);
        Button browseEventsBtn = view.findViewById(R.id.browseEventsButton);

        // Navigate to AdminBrowseProfilesFragment when button clicked
        browseProfilesBtn.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.adminFragmentContainer, new AdminBrowseProfilesFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Navigate to AllEventsFragment when button clicked
        browseEventsBtn.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.adminFragmentContainer, new AllEventsFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Hide back button in AdminMainActivity
        ((AdminMainActivity) requireActivity()).showBackButton(false);

        return view;
    }
}
