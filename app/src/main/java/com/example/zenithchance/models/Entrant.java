package com.example.zenithchance.models;


import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.WriteBatch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents Entrant users.
 *
 * @author Percy
 * @version 1.0
 */
public class Entrant extends User implements Serializable {
    private ArrayList<String> onWaiting = new ArrayList<String>();
    private ArrayList<String> onInvite = new ArrayList<String>();
    private ArrayList<String> onAccepted = new ArrayList<String>();
    private ArrayList<String> onDeclined = new ArrayList<String>();

    private List<String> notifications = new ArrayList<>(); // Do Not Delete, used for notifications

    public Entrant() { setType("entrant"); }

    /**
     * Check if id is contained in given list
     *
     * @param list  List to check from
     * @param id    Id to compare with
     *
     * @return      true is yes, false otherwise
     */
    private boolean containsId(ArrayList<String> list, String id) {
        return list != null && list.contains(id);
    }

    /**
     * Check if event is in any of entrant's list
     *
     * @param eventDocId Firebase document id of event
     * @return           true if yes, false if no
     */
    public boolean isInAnyList(String eventDocId) {
        return containsId(onWaiting, eventDocId)
                || containsId(onInvite, eventDocId)
                || containsId(onAccepted, eventDocId)
                || containsId(onDeclined, eventDocId);
    }

    /**
     * Check if event is in entrant's waiting list
     *
     * @param eventDocId Firebase document id of event
     * @return           true if yes, false if no
     */
    public boolean isInWaitingList(String eventDocId) {
        return containsId(onWaiting, eventDocId);
    }

    /**
     * Check if event is in entrant's invited list
     *
     * @param eventDocId Firebase document id of event
     * @return           true if yes, false if no
     */
    public boolean isInInvitedList(String eventDocId) {
        return containsId(onInvite, eventDocId);
    }

    /**
     * Check if event is in entrant's accepted list
     *
     * @param eventDocId Firebase document id of event
     * @return           true if yes, false if no
     */
    public boolean isInAcceptedList(String eventDocId) {
        return containsId(onAccepted, eventDocId);
    }

    /**
     * Check if event is in entrant's declined list
     *
     * @param eventDocId Firebase document id of event
     * @return           true if yes, false if no
     */
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
        batch.update(userRef, "onWaiting", FieldValue.arrayUnion(eventDocId));
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

    /**
     * Allows entrant to accept invitation
     *
     * @param event         Event to respond to
     * @param eventDocId    Firebase document id of event
     * @param onSuccess     A callback to run if the Firestore update succeeds
     * @param onError       Signal that contains error if update fail
     */
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

    /**
     * Allows entrant to decline invitation
     *
     * @param event         Event to respond to
     * @param eventDocId    Firebase document id of event
     * @param onSuccess     A callback to run if the Firestore update succeeds
     * @param onError       Signal that contains error if update fail
     */
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

            // redraw another
            FirebaseFirestore.getInstance()
                    .collection("events").document(eventDocId)
                    .update("needRedraw", true);

        }).addOnFailureListener(e -> { if (onError != null) onError.accept(e); });
    }

    /**
     * Allows entrant to decline accepted spot
     *
     * @param event         Event to respond to
     * @param eventDocId    Firebase document id of event
     * @param onSuccess     A callback to run if the Firestore update succeeds
     * @param onError       Signal that contains error if update fail
     */
    public void cancelAccepted(Event event, String eventDocId, Runnable onSuccess,
                               java.util.function.Consumer<Exception> onError) {

        String uid = getUserId();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef  = db.collection("users").document(uid);
        DocumentReference eventRef = db.collection("events").document(eventDocId);

        WriteBatch batch = db.batch();
        batch.update(userRef,  "onAccepted", FieldValue.arrayRemove(eventDocId));
        batch.update(userRef, "onDeclined", FieldValue.arrayUnion(eventDocId));
        batch.update(eventRef, "acceptedList", FieldValue.arrayRemove(uid));
        batch.update(eventRef, "declinedList", FieldValue.arrayUnion(uid));

        batch.commit().addOnSuccessListener(v -> {
            onAccepted.remove(eventDocId);
            onDeclined.add(eventDocId);
            if (event != null) {
                event.removeFromAcceptedList(uid);
                event.addDeclinedList(uid);
            }

            if (onSuccess != null) onSuccess.run();

            // redraw
            FirebaseFirestore.getInstance()
                    .collection("events").document(eventDocId)
                    .update("needRedraw", true);

        }).addOnFailureListener(e -> { if (onError != null) onError.accept(e); });;
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

    /**
     * Queue setters
     * @param onWaiting list of events (Firebase ids)
     */
    public void setOnWaiting(ArrayList<String> onWaiting) {
        this.onWaiting = (onWaiting != null) ? onWaiting : new ArrayList<>();
    }

    public void setOnInvite(ArrayList<String> onInvite) {
        this.onInvite = (onInvite != null) ? onInvite : new ArrayList<>();
    }

    public void setOnAccepted(ArrayList<String> onAccepted) {
        this.onAccepted = (onAccepted != null) ? onAccepted : new ArrayList<>();
    }

    public void setOnDeclined(ArrayList<String> onDeclined) {
        this.onDeclined = (onDeclined != null) ? onDeclined : new ArrayList<>();
    }

}
