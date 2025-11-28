package com.example.zenithchance.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.zenithchance.OrganizerMainActivity;
import com.example.zenithchance.R;
import com.example.zenithchance.adapters.EventsAdapter;
import com.example.zenithchance.models.Event;
import com.example.zenithchance.models.Organizer;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class for the Organizer Events page logic
 *
 * @author Emerson
 * @version 1.0
 * @see Event
 * @see Organizer
 * @see OrganizerEventListFragment
 * @see OrganizerCreateEventFragment
 */
public class OrganizerEventsFragment extends Fragment {
    private OrganizerEventListFragment eventListFrag;
    private List<Event> eventList = new ArrayList<>();

    private Organizer organizer;

    Button createEventButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_organizer_my_events, container, false);

        // Sets the organizer to be the organizer signed in
        if (getActivity() instanceof OrganizerMainActivity) {
            organizer = ((OrganizerMainActivity) getActivity()).getOrganizer();
        }

        // Inflate
        FragmentManager fm = getChildFragmentManager(); // IMPORTANT: child fragment
        eventListFrag = new OrganizerEventListFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable("organizer", organizer);

        eventListFrag.setArguments(bundle);

        fm.beginTransaction()
                .replace(R.id.eventsFragmentContainer, eventListFrag)
                .commit();
        fm.executePendingTransactions();

        createEventButton = view.findViewById(R.id.create_event_button);

        // Replaces the EventsFragment with a CreateEvent fragment when the create button is clicked

        initCreateEventButton();

        organizer.checkAndRunLotteries();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (organizer == null && getActivity() instanceof OrganizerMainActivity) {
            organizer = ((OrganizerMainActivity) getActivity()).getOrganizer();
        }
        if (organizer == null) return;

        organizer.checkAndRedraw();
    }

    private void initCreateEventButton() {
        createEventButton.setOnClickListener(v -> {

            // Initializing a bundle to pass the organizer user
            Bundle bundle = new Bundle();
            bundle.putSerializable("organizer", organizer);

//            OrganizerCreateEventFragment createFragment = new OrganizerCreateEventFragment();
            QRScannerFragment createFragment = new QRScannerFragment();
            createFragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, createFragment)
                    .commit();
        });

    }

}