package com.example.zenithchance.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.zenithchance.OrganizerMainActivity;
import com.example.zenithchance.R;
import com.example.zenithchance.models.Event;
import com.example.zenithchance.models.Organizer;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for the Organizer Events page logic
 *
 * @author Emerson, Lauren, Percy
 * @version 1.1
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
    Button runLotteriesButton;

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
        runLotteriesButton = view.findViewById(R.id.run_lotteries_button);

        // Replaces the EventsFragment with a CreateEvent fragment when the create button is clicked

        initCreateEventButton();
        runLotteriesCheckDeadline();

        organizer.checkAndRunLotteries();
        organizer.checkFinalDeadlines();

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

            OrganizerCreateEventFragment createFragment = new OrganizerCreateEventFragment();
//            QRScannerFragment createFragment = new QRScannerFragment();
            createFragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, createFragment)
                    .commit();
        });
    }

    private void runLotteriesCheckDeadline() {
        runLotteriesButton.setOnClickListener(v -> {
            if (organizer == null && getActivity() instanceof OrganizerMainActivity) {
                organizer = ((OrganizerMainActivity) getActivity()).getOrganizer();
            }

            // Manually trigger both checks
            organizer.checkAndRunLotteries();
            organizer.checkAndRedraw();
            // Check if invitation deadline has passed
            organizer.checkFinalDeadlines();

            Toast.makeText(getContext(), "Lotteries ran and invitation deadlines checked", Toast.LENGTH_SHORT).show();
        });
    }

}