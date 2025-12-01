package com.example.zenithchance.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;

import androidx.fragment.app.Fragment;

import com.example.zenithchance.AdminMainActivity;
import com.example.zenithchance.R;
import com.example.zenithchance.fragments.AdminBrowseProfilesFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminMenuFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_menu, container, false);

        Button browseProfilesBtn = view.findViewById(R.id.browseProfilesButton);
        Button browseEventsBtn = view.findViewById(R.id.browseEventsButton);
        Button browseImagesBtn = view.findViewById(R.id.browseImagesButton);
        Button browseNotificationsBtn = view.findViewById(R.id.browseNotificationsButton);

//        BROWSE PROFILE -> Switch to AdminBrowseProfiles Fragment
        browseProfilesBtn.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.adminFragmentContainer, new AdminBrowseProfilesFragment())
                    .addToBackStack(null)
                    .commit();
        });

//        BROWSE EVENTS -> Switch tab to All Events
        browseEventsBtn.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.adminFragmentContainer, new AllEventsFragment())
                    .addToBackStack(null)
                    .commit();
        });

//        BROWSE IMAGES

        browseImagesBtn.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.adminFragmentContainer, new AdminBrowseImagesFragment())
                    .addToBackStack(null)
                    .commit();
        });

//        BROWSE NOTIFICATIONS
        browseNotificationsBtn.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.adminFragmentContainer, new AdminNotificationLogsFragment())
                    .addToBackStack(null)
                    .commit();
        });




        ((AdminMainActivity) requireActivity()).showBackButton(false);
        return view;
    }
}
