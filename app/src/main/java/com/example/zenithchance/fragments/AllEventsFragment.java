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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.zenithchance.activities.EntrantEventDetailsActivity;
import com.example.zenithchance.adapters.EventsAdapter;
import com.example.zenithchance.models.Event;
import com.example.zenithchance.adapters.EventAdapter;
import com.example.zenithchance.EventList;
import com.example.zenithchance.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AllEventsFragment extends Fragment {

    private RecyclerView recyclerView;
    private EventsAdapter adapter;
    private List<Event> allEvents = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_events, container, false);

        recyclerView = view.findViewById(R.id.recycler_all_events);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        SimpleDateFormat fmt = new SimpleDateFormat("EEE, MMM d • h:mm a", Locale.getDefault());

        adapter = new EventsAdapter(new ArrayList<>(), event -> {
            Intent i = new Intent(requireContext(), EntrantEventDetailsActivity.class);
            i.putExtra("event_name", event.getName());
            i.putExtra("event_location", event.getLocation());
            i.putExtra("event_status", event.getStatus());
            i.putExtra("event_organizer", event.getOrganizer());
            i.putExtra("event_time", fmt.format(event.getDate()));
            i.putExtra("event_description", event.getDescription());
            startActivity(i);
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
                    allEvents.clear();
                    for (DocumentSnapshot doc : snaps) {
                        Event event = doc.toObject(Event.class);
                        if (event != null) {
                            allEvents.add(event);
                        }
                    }
                    adapter.setItems(allEvents);
                })
                .addOnFailureListener(e -> Log.e("AllEventsFragment", "Error fetching events", e));
    }
}

