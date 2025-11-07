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
import com.example.zenithchance.interfaces.EntrantProviderInterface;
import com.example.zenithchance.interfaces.UserProviderInterface;
import com.example.zenithchance.models.Entrant;
import com.example.zenithchance.models.Event;
import com.example.zenithchance.R;
import com.example.zenithchance.models.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AllEventsFragment extends Fragment {

    private RecyclerView recyclerView;
    private AllEventsAdapter adapter;
    private List<Event> events = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

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
            if (currentUser == null) return;

            String type = currentUser.getType();

            // Bundle to pass into fragments
            Bundle bundle = new Bundle();
            bundle.putString("event_doc_id", event.getDocId());
            bundle.putString("event_name", event.getName());
            bundle.putString("event_location", event.getLocation());
            bundle.putString("event_organizer", event.getOrganizer());
            bundle.putString("event_time", fmt.format(event.getDate()));
            bundle.putString("event_description", event.getDescription());
            bundle.putString("event_image_url", event.getImageUrl());

            Fragment nextFragment;

            if ("admin".equalsIgnoreCase(type)) {
                nextFragment = new AdminEventDetailsFragment();
                nextFragment.setArguments(bundle);

            } else { // Entrant
                EntrantEventDetailsFragment entrantFragment = new EntrantEventDetailsFragment();
                entrantFragment.setArguments(bundle);

                if (requireActivity() instanceof EntrantProviderInterface) {
                    EntrantProviderInterface provider = (EntrantProviderInterface) requireActivity();
                    entrantFragment.setCurrentEntrant(provider.getCurrentEntrant());
                }

                nextFragment = entrantFragment;
            }

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, nextFragment)
                    .addToBackStack(null)
                    .commit();
        });


        recyclerView.setAdapter(adapter);

        loadAllEvents();
        return view;
    }

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
