package com.example.zenithchance;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class EntrantEventListFragment extends Fragment {
    private EventsAdapter adapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.entrant_event_list_fragment, container, false);

        RecyclerView rv = root.findViewById(R.id.recycler_events);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new EventsAdapter();
        rv.setAdapter(adapter);

        loadEventsOnce(); // simple one-shot fetch; we can switch to realtime later
        return root;
    }

    private void loadEventsOnce() {
        FirebaseFirestore.getInstance()
                .collection("events")
                .orderBy("date") // earliest → latest
                .get()
                .addOnSuccessListener(snaps -> {
                    List<Event> list = new ArrayList<>();
                    for (DocumentSnapshot d : snaps) {
                        Event e = d.toObject(Event.class);
                        if (e != null) list.add(e);
                    }
                    adapter.setItems(list);
                })
                .addOnFailureListener(e -> Log.e("EventsList", "Firestore load failed", e));
    }
}

