package com.example.zenithchance.models;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

/**
 * This class handles all lottery tasks.
 * It will be initialized at the start of the app, notifying current entrant
 * if they have been chosen for any event and handle subsequent interaction
 * with that invitation.
 *
 * An instance of LotterySystem serves the entrant accessing the app.
 *
 * @author Percy
 * @version 1.0
 */
public class LotterySystem {
    private ArrayList<Event> entrantEvents;

    public LotterySystem() {
        this.entrantEvents = new ArrayList<Event>();
    }

    /**
     * This method fetches all events this user is participating on Firestore
     * and store it in self.entrantEvents
     */
    public void getEvents() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(uid).get()
                .addOnSuccessListener(userSnap -> {
                    if (!userSnap.exists() || !"entrant".equals(userSnap.getString("type"))) { return; }

                    // fetch event ids
                    List<String> ids = (List<String>) userSnap.get("events");

                    // weird thing is that firestore can only fetch 10 items at a time
                    // so splits into chunks
                    List<List<String>> chunks = chunk(ids, 10);
                    ArrayList<Event> collected = new ArrayList<>();
                    final int[] remaining = {chunks.size()}; // tracks #chunks remain unfinished

                    // fetch 10 events at a time
                    for (List<String> c : chunks) {
                        db.collection("events")
                                .whereIn(FieldPath.documentId(), c)
                                .get()
                                .addOnSuccessListener(qs -> {
                                    for (DocumentSnapshot d : qs) {
                                        Event e = d.toObject(Event.class);
                                        if (e != null) collected.add(e);
                                    }
                                })
                                .addOnCompleteListener(t -> {
                                    // decrement the counter of remaining chunks
                                    if (--remaining[0] == 0) {
                                        entrantEvents.clear();
                                        entrantEvents.addAll(collected);
                                    }
                                });
                    }
                });
    }

    /**
     * Helper function to getEvents().
     * It splits a list into chunks.
     *
     * @param list List to be splitted
     * @param size Size of chunk
     * @return
     * @param <T>
     */
    private <T> List<List<T>> chunk(List<T> list, int size) {
        List<List<T>> out = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            // subList(fromIndex, toIndex) creates a lightweight view of the list
            out.add(list.subList(i, Math.min(i + size, list.size())));
        }
        return out;
    }
}
