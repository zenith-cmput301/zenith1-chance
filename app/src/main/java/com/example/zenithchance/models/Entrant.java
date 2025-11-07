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
    private ArrayList<String> onWaiting = new ArrayList<String>();
    private ArrayList<String> onInvite = new ArrayList<String>();
    private ArrayList<String> onAccepted = new ArrayList<String>();
    private ArrayList<String> onDeclined = new ArrayList<String>();

    public Entrant() { setType("entrant"); }

    private boolean containsId(ArrayList<String> list, String id) {
        return list != null && list.contains(id);
    }

    public boolean isInAnyList(String eventDocId) {
        return containsId(onWaiting, eventDocId)
                || containsId(onInvite, eventDocId)
                || containsId(onAccepted, eventDocId)
                || containsId(onDeclined, eventDocId);
    }

    public boolean isInWaitingList(String eventDocId) {
        return containsId(onWaiting, eventDocId);
    }

    public boolean isInInvitedList(String eventDocId) {
        return containsId(onInvite, eventDocId);
    }

    public boolean isInAcceptedList(String eventDocId) {
        return containsId(onAccepted, eventDocId);
    }

    public boolean isInDeclinedList(String eventDocId) {
        return containsId(onDeclined, eventDocId);
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
        batch.update(userRef,  "onWaiting",  FieldValue.arrayUnion(eventDocId));
        batch.update(eventRef, "waitingList", FieldValue.arrayUnion(uid));

        batch.commit().addOnSuccessListener(v -> {
            if (!onWaiting.contains(eventDocId)) onWaiting.add(eventDocId);
            if (event != null) event.addWaitingList(uid);
            if (onSuccess != null) onSuccess.run();
        }).addOnFailureListener(e -> { if (onError != null) onError.accept(e); });
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
        batch.update(userRef,  "onWaiting", FieldValue.arrayRemove(eventDocId));
        batch.update(eventRef, "waitingList", FieldValue.arrayRemove(uid));

        batch.commit().addOnSuccessListener(v -> {
            onWaiting.remove(eventDocId);
            event.removeFromWaitingList(uid);
            if (onSuccess != null) onSuccess.run();
        }).addOnFailureListener(e -> { if (onError != null) onError.accept(e); });
    }

    public void acceptInvite(Event event, String eventDocId, Runnable onSuccess,
                             java.util.function.Consumer<Exception> onError) {
        String uid = getUserId();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(uid);
        DocumentReference eventRef = db.collection("events").document(eventDocId);

        WriteBatch batch = db.batch();
        batch.update(userRef,  "onInvite", FieldValue.arrayRemove(eventDocId));
        batch.update(userRef,  "onAccepted", FieldValue.arrayUnion(eventDocId));
        batch.update(eventRef, "invitedList", FieldValue.arrayRemove(uid));
        batch.update(eventRef, "acceptedList", FieldValue.arrayUnion(uid));

        batch.commit().addOnSuccessListener(v -> {
            onInvite.remove(eventDocId);
            if (!onAccepted.contains(eventDocId)) onAccepted.add(eventDocId);
            if (event != null) {
                event.removeFromInvitedList(uid);
                event.addAcceptedList(uid);
            }
            if (onSuccess != null) onSuccess.run();
        }).addOnFailureListener(e -> { if (onError != null) onError.accept(e); });
    }

    public void declineInvite(Event event, String eventDocId, Runnable onSuccess,
                              java.util.function.Consumer<Exception> onError) {
        String uid = getUserId();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef  = db.collection("users").document(uid);
        DocumentReference eventRef = db.collection("events").document(eventDocId);

        WriteBatch batch = db.batch();
        batch.update(userRef,  "onInvite",  FieldValue.arrayRemove(eventDocId));
        batch.update(userRef, "onDeclined", FieldValue.arrayUnion(eventDocId));
        batch.update(eventRef, "invitedList", FieldValue.arrayRemove(uid));
        batch.update(eventRef, "declinedList", FieldValue.arrayUnion(uid));

        batch.commit().addOnSuccessListener(v -> {
            onInvite.remove(eventDocId);
            onDeclined.add(eventDocId);
            if (event != null) {
                event.removeFromInvitedList(uid);
                event.addDeclinedList(uid);
            }
            if (onSuccess != null) onSuccess.run();
        }).addOnFailureListener(e -> { if (onError != null) onError.accept(e); });
    }


    /**
     * Getters
     * @return list of events
     */
    public ArrayList<String> getOnWaiting() {
        return onWaiting;
    }

    public ArrayList<String> getOnInvite() {
        return onInvite;
    }

    public ArrayList<String> getOnAccepted() {
        return onAccepted;
    }

    public ArrayList<String> getOnDeclined() {
        return onDeclined;
    }

}
