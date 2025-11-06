package com.example.zenithchance.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.zenithchance.activities.EntrantEventDetailsActivity;
import com.example.zenithchance.adapters.AllEventsAdapter;
import com.example.zenithchance.adapters.EventsAdapter;
import com.example.zenithchance.interfaces.EntrantProviderInterface;
import com.example.zenithchance.models.Event;
import com.example.zenithchance.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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

        // Create adapter
        adapter = new AllEventsAdapter(requireContext(), events, event -> {

            EntrantEventDetailsFragment fragment = new EntrantEventDetailsFragment();

            Bundle bundle = new Bundle();
            bundle.putString("event_name", event.getName());
            bundle.putString("event_location", event.getLocation());
            bundle.putString("event_organizer", event.getOrganizer());
            bundle.putString("event_time", fmt.format(event.getDate()));
            bundle.putString("event_description", event.getDescription());
            bundle.putString("event_image_url", event.getImageUrl());
            bundle.putString("event_doc_id", event.getDocId());
            fragment.setArguments(bundle);

            if (requireActivity() instanceof EntrantProviderInterface) {
                EntrantProviderInterface provider = (EntrantProviderInterface) requireActivity();
                fragment.setCurrentEntrant(provider.getCurrentEntrant());
            }

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        recyclerView.setAdapter(adapter);

        loadAllEvents();
        return view;
    }

    private void loadAllEvents() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

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

