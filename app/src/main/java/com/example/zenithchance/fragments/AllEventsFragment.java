package com.example.zenithchance.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.zenithchance.adapters.AllEventsAdapter;
import com.example.zenithchance.interfaces.UserProviderInterface;
import com.example.zenithchance.models.Event;
import com.example.zenithchance.models.User;
import com.example.zenithchance.navigation.EntrantNavigationHelper;
import com.example.zenithchance.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * This fragment displays all available events to Entrants
 *
 * @author Kira, Percy
 * @version 1.0
 * @see AllEventsAdapter
 * @see EntrantNavigationHelper
 * @see AdminMenuFragment
 */
public class AllEventsFragment extends Fragment {
    private RecyclerView recyclerView;
    private AllEventsAdapter adapter;
    private List<Event> events = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * This method defines what happens when this fragment is created
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return View to display
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_events, container, false);

        recyclerView = view.findViewById(R.id.recycler_all_events);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        SimpleDateFormat fmt = new SimpleDateFormat("EEE, MMM d • h:mm a", Locale.getDefault());

        adapter = new AllEventsAdapter(requireContext(), events, event -> {

            User currentUser = null;
            if (requireActivity() instanceof UserProviderInterface) {
                currentUser = ((UserProviderInterface) requireActivity()).getCurrentUser();
            }

            if (currentUser == null) {
                return;
            }

            // Bundle data to send to EventDetailsFragment
            Bundle bundle = new Bundle();
            bundle.putString("event_name", event.getName());
            bundle.putString("event_location", event.getLocation());
            bundle.putString("event_organizer", event.getOrganizer());

            String formattedDate = (event.getDate() != null)
                    ? fmt.format(event.getDate())
                    : "Date not available";
            bundle.putString("event_time", formattedDate);

            bundle.putString("event_description", event.getDescription());
            bundle.putString("event_image_url", event.getImageUrl());
            bundle.putString("event_doc_id", event.getDocId());

            // Decide which EventDetailsFragment to show based on user type
            Fragment targetFragment;
            if ("admin".equalsIgnoreCase(currentUser.getType())) {
                targetFragment = new AdminEventDetailsFragment();
            } else {
                targetFragment = new EntrantEventDetailsFragment();
            }

            targetFragment.setArguments(bundle);

            // Determine which container to replace based on activity layout
            int containerId;
            if (requireActivity().findViewById(R.id.adminFragmentContainer) != null) {
                containerId = R.id.adminFragmentContainer;
            } else {
                containerId = R.id.fragmentContainer;
            }

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(containerId, targetFragment)
                    .addToBackStack(null)
                    .commit();
        });

        recyclerView.setAdapter(adapter);

        loadAllEvents();
        return view;
    }

    /**
     * This method fetches all events from Firestore to local list of events
     */
    private void loadAllEvents() {
        db.collection("events")
                .orderBy("date")
                .get()
                .addOnSuccessListener(snaps -> {
                    List<Event> eventList = new ArrayList<>();
                    for (DocumentSnapshot doc : snaps) {
                        Event event = doc.toObject(Event.class);
                        if (event != null) {
                            event.setDocId(doc.getId());
                            eventList.add(event);
                        }
                    }
                    adapter.updateList(eventList);
                })
                .addOnFailureListener(e -> Log.e("AllEventsFragment", "Error fetching events", e));
    }
}
