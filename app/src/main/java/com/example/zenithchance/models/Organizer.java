package com.example.zenithchance.models;


import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents Organizer users.
 */
public class Organizer extends User implements Serializable {
    private ArrayList<String> orgEvents = new ArrayList<String>(); // Firestore document ids

    public Organizer() {
        setType("organizer");
    }

    public ArrayList<String> getOrgEvents() {
        return orgEvents;
    }

    public void checkAndRunLotteries() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Timestamp now = Timestamp.now();

        db.collection("events")
                .whereEqualTo("organizer", this.getName())
                .whereLessThanOrEqualTo("registrationDate", now)
                .whereEqualTo("lotteryRan", false) // only events not drawn yet
                .orderBy("registrationDate")
                .get()
                .addOnSuccessListener(snap -> {
                    for (var doc : snap.getDocuments()) {
                        Log.d("Organizer", "Qualifying event detected");
                        runLottery(doc.getId(), false);
                    }
                })
                .addOnFailureListener(snap -> {
                    Log.d("Organizer", "No event qualified.");
                });
    }

    // called when an invited entrant declines
    public void checkAndRedraw() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("events")
                .whereEqualTo("organizer", this.getName())
                .whereEqualTo("needRedraw", true)
                .get()
                .addOnSuccessListener(snap -> {
                    List<Task<Void>> tasks = new ArrayList<>();
                    for (DocumentSnapshot d : snap.getDocuments()) {
                        tasks.add(this.runLottery(d.getId(), true));
                    }
                });
    }

    public Task<Void> runLottery(String eventId, boolean rollOne) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference evRef = db.collection("events").document(eventId);

        return db.runTransaction(trans -> {
            DocumentSnapshot ev = trans.get(evRef);

            // get relevant fields that we need
            List<String> waiting = (List<String>) ev.get("waitingList");
            List<String> invited  = (List<String>) ev.get("invitedList");
            List<String> accepted = (List<String>) ev.get("acceptedList");
            Long capL = ev.getLong("maxEntrants");

            // prep for random sampling
            int slots;
            if (rollOne) slots = 1;
            else slots = Math.max(0, capL.intValue() - accepted.size() - invited.size()); // get remaining slots
            Map<String, Object> updates = new HashMap<>();

            // if no one left to sample
            if (slots == 0 || waiting.isEmpty()) {
                trans.update(evRef, updates);
                return null;
            }

            // random sampling
            List<String> winners = randomSample(waiting, slots);
            if (rollOne) updates.put("needRedraw", false);

            // move winners
            List<String> newInvited = new ArrayList<>(invited);
            newInvited.addAll(winners);
            ArrayList<String> newWaiting = new ArrayList<>(waiting);
            newWaiting.removeAll(winners);

            updates.put("invitedList", newInvited);
            updates.put("waitingList", newWaiting);
            updates.put("lotteryRan", true);

            trans.update(evRef, updates);

            // move event from user's onWaiting to onInvite
            for (String entrantId : winners) {
                DocumentReference userRef = db.collection("users").document(entrantId);
                // no need to read user doc: use atomic array transforms
                trans.update(userRef,
                        "onWaiting", FieldValue.arrayRemove(eventId),
                        "onInvite",  FieldValue.arrayUnion(eventId)
                );
            }

            return null;
        });
    }

    public List<String> randomSample(List<String> waiting, int slots) {
        List<String> pool = new ArrayList<>(waiting); // copy to not affect original waiting list
        Collections.shuffle(pool);
        return new ArrayList<>(pool.subList(0, Math.min(slots, pool.size())));
    }

    public void setOrgEvents(ArrayList<String> orgEvents) {
        this.orgEvents = orgEvents;
    }

    public void addOrgEvent(String orgEvent) {
        this.orgEvents.add(orgEvent);
    }

    // NEW NOTIFICATION THINGS:
    public void sendNotification(String eventName, String status, Entrant recipient){
        if(recipient.getNotificationStatus()==true){
            recipient.addNotification(new Notification(eventName, status, recipient.getUserId() ));
        }
    }
}
