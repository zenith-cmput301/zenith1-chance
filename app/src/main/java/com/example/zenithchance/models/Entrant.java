package com.example.zenithchance.models;


import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents Entrant users.
 */
public class Entrant extends User {
    private ArrayList<Event> onWaiting = new ArrayList<Event>();
    private ArrayList<Event> onInvite = new ArrayList<Event>();
    private ArrayList<Event> onAccepted = new ArrayList<Event>();

    public Entrant() { setType("entrant"); }

    /**
     * This method get this entrant's events
     */
    public void fetchUserEvents() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // fetch user's events' names
        db.collection("users").document(getUserId())
                .get()
                .addOnSuccessListener(userDoc -> {
                    List<String> waitingNames  = (List<String>) userDoc.get("onWaiting");
                    List<String> inviteNames   = (List<String>) userDoc.get("onInvite");
                    List<String> acceptedNames = (List<String>) userDoc.get("onAccepted");

                    if (waitingNames  == null) waitingNames  = new ArrayList<>();
                    if (inviteNames   == null) inviteNames   = new ArrayList<>();
                    if (acceptedNames == null) acceptedNames = new ArrayList<>();

                    // get all events (to later filter them)
                    List<String> finalAcceptedNames = acceptedNames;
                    List<String> finalInviteNames = inviteNames;
                    List<String> finalWaitingNames = waitingNames;
                    db.collection("events")
                            .orderBy("date")
                            .get()
                            .addOnSuccessListener(snaps -> {
                                onWaiting.clear();
                                onInvite.clear();
                                onAccepted.clear();

                                // filter them to appropriate lists
                                for (DocumentSnapshot d : snaps) {
                                    Event e = d.toObject(Event.class);
                                    if (e == null) continue;
                                    String name = e.getName();
                                    if (name == null) continue;

                                    if (finalAcceptedNames.contains(name)) {
                                        onAccepted.add(e);
                                    } else if (finalInviteNames.contains(name)) {
                                        onInvite.add(e);
                                    } else if (finalWaitingNames.contains(name)) {
                                        onWaiting.add(e);
                                    }
                                }
                            });
                });
    }

    /**
     * Check if event is in given list
     *
     * @param list          List to check in
     * @param eventName     Event to check
     * @return              true if yes, false otherwise
     */
    private boolean containsByName(ArrayList<Event> list, String eventName) {
        if (eventName == null) return false;
        for (Event e : list) {
            if (e != null && eventName.equals(e.getName())) return true;
        }
        return false;
    }

    /**
     * Check if event is currently associated with entrant
     *
     * @param eventName Event to check
     * @return          true if yes, false otherwsie
     */
    public boolean isInAnyListByName(String eventName) {
        return containsByName(onWaiting, eventName)
                || containsByName(onInvite, eventName)
                || containsByName(onAccepted, eventName);
    }

    /**
     * Enrolls entrant to given event
     *
     * @param event         Event to enroll in
     * @param eventDocId    Event's Firestore document id
     * @param onSuccess
     * @param onError
     */
    public void enrollInWaiting(Event event, String eventDocId, Runnable onSuccess,
                                java.util.function.Consumer<Exception> onError) {

        String uid = getUserId();

        // Enrolls locally
        if (!containsByName(onWaiting, event.getName())) onWaiting.add(event);

        // Record enrollment on Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(uid);
        DocumentReference eventRef = db.collection("events").document(eventDocId);

        WriteBatch batch = db.batch();
        batch.update(userRef,  "onWaiting",  FieldValue.arrayUnion(event.getName()));
        batch.update(eventRef, "waitingList", FieldValue.arrayUnion(uid));

        batch.commit().addOnSuccessListener(v -> { if (onSuccess != null) onSuccess.run(); });
    }


    /**
     * Getters
     * @return list of events
     */
    public ArrayList<Event> getOnWaiting() {
        return onWaiting;
    }

    public ArrayList<Event> getOnInvite() {
        return onInvite;
    }

    public ArrayList<Event> getOnAccepted() {
        return onAccepted;
    }

    /**
     * Setters
     */
    public void setOnWaiting(ArrayList<Event> onWaiting) {
        this.onWaiting = onWaiting;
    }

    public void setOnInvite(ArrayList<Event> onInvite) {
        this.onInvite = onInvite;
    }

    public void setOnAccepted(ArrayList<Event> onAccepted) {
        this.onAccepted = onAccepted;
    }
}
