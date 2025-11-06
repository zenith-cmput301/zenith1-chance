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
    private ArrayList<Event> onWaiting = new ArrayList<>();
    private ArrayList<Event> onInvite = new ArrayList<Event>();
    private ArrayList<Event> onAccepted = new ArrayList<Event>();

    public Entrant() { setType("entrant"); }

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
     * @return          true if yes, false otherwise
     */
    public boolean isInAnyListByName(String eventName) {
        return containsByName(onWaiting, eventName)
                || containsByName(onInvite, eventName)
                || containsByName(onAccepted, eventName);
    }

    /**
     * Check if entrant is in this event's waiting list
     *
     * @param eventName Event to check
     * @return          true if yes, false otherwise
     */
    public boolean isInWaitingList(String eventName) {
        return containsByName(onWaiting, eventName);
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

        // Record enrollment on Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(uid);
        DocumentReference eventRef = db.collection("events").document(eventDocId);

        WriteBatch batch = db.batch();
        batch.update(userRef,  "onWaiting",  FieldValue.arrayUnion(event.getName()));
        batch.update(eventRef, "waitingList", FieldValue.arrayUnion(uid));

        batch.commit().addOnSuccessListener(v -> {
            if (!containsByName(onWaiting, event.getName())) {
                onWaiting.add(event);
                event.addWaitingList(uid);
            }
            if (onSuccess != null) onSuccess.run();
        }).addOnFailureListener(e -> {
            if (onError != null) onError.accept(e);
        });
    }

    /**
     * Drop entrant out of event's waiting list
     *
     * @param event         Event to enroll in
     * @param eventDocId    Event's Firestore document id
     * @param onSuccess
     * @param onError
     */
    public void dropWaiting(Event event, String eventDocId, Runnable onSuccess,
                                java.util.function.Consumer<Exception> onError) {
        String uid = getUserId();
        String targetName = event.getName();

        // drop on firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef  = db.collection("users").document(uid);
        DocumentReference eventRef = db.collection("events").document(eventDocId);

        WriteBatch batch = db.batch();
        batch.update(userRef,  "onWaiting",  FieldValue.arrayRemove(targetName));
        batch.update(eventRef, "waitingList", FieldValue.arrayRemove(uid));

        batch.commit().addOnSuccessListener(v -> {
            onWaiting.removeIf(e -> targetName.equals(e.getName()));
            event.removeFromWaitingList(uid);
            if (onSuccess != null) onSuccess.run();
        }).addOnFailureListener(e -> {
            if (onError != null) onError.accept(e);
        });
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
