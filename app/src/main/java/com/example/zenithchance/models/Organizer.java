package com.example.zenithchance.models;

import android.util.Log;
import android.widget.Toast;

import com.example.zenithchance.R;
import com.example.zenithchance.fragments.OrganizerEventsFragment;
import com.example.zenithchance.managers.UserManager;
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
 * The representative class for all Organizers.
 *
 * @author Emerson, Aayush, Percy, Sabrina
 * @version 1.2
 */
public class Organizer extends User implements Serializable {
    private ArrayList<String> orgEvents = new ArrayList<String>(); // Firestore document ids

    /**
     * Constructor for class Event.
     */
    public Organizer() {
        setType("organizer");
    }

    /**
     * This method checks and runs any event qualifying to run lottery at the creation of
     * organizer's My Events page.
     */
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
                    for (DocumentSnapshot  doc : snap.getDocuments()) {
                        String eventId = doc.getId();
                        runLottery(doc.getId(), false).addOnSuccessListener(winners -> {
                            if (winners == null) return; // no entrants selected
                            for (String uid : winners) { // send notifications to selected entrants
                                UserManager.getInstance().sendNotification(eventId, "chosen", uid);
                            }
                        });
                    }
                })
                .addOnFailureListener(snap -> {
                    Log.d("Organizer", "No event qualified.");
                });
    }


    // called when an invited entrant declines

    /**
     * This method checks and redraws for any event of this organizer that needs to redraw.
     */
    public void checkAndRedraw() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("events")
                .whereEqualTo("organizer", this.getName())
                .whereEqualTo("needRedraw", true)
                .get()
                .addOnSuccessListener(snap -> {
                    for (DocumentSnapshot d : snap.getDocuments()) {
                        String eventId = d.getId();
                        runLottery(eventId, true).addOnSuccessListener(winners -> {
                            if (winners == null) return; // no entrants selected
                            for (String uid : winners) { // send notifications to selected entrants
                                UserManager.getInstance().sendNotification(eventId, "chosen", uid);
                            }
                        });
                    }
                });
    }

    /**
     * This method runs lottery on an event's waiting list.
     *
     * @param eventId   Firebase doc id of event
     * @param rollOne   true if redraw, false if it's the first time running lottery
     * @return          Firebase doc id of selected entrants
     */
    public Task<List<String>> runLottery(String eventId, boolean rollOne) {
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
            if (slots == 0 || waiting.isEmpty()) return null;

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

                trans.update(userRef,
                        "onWaiting", FieldValue.arrayRemove(eventId),
                        "onInvite",  FieldValue.arrayUnion(eventId)
                );
            }

            return winners;
        });
    }

    /**
     * This method randomly sample ids from the waiting list. Called by the above method.
     *
     * @param waiting   List of waiting list entrants' Firebase doc id
     * @param slots     Number of entrants to be sampled
     * @return          Firebase doc id of selected entrants
     */
    public List<String> randomSample(List<String> waiting, int slots) {
        List<String> pool = new ArrayList<>(waiting); // copy to not affect original waiting list
        Collections.shuffle(pool);
        return new ArrayList<>(pool.subList(0, Math.min(slots, pool.size())));
    }

    /**
     * This method checks for any events that has passed the deadline to respond
     * to invitations. For those events, decline the people unresponsive to invites.
     */
    public void checkFinalDeadlines() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Timestamp now = Timestamp.now();

        db.collection("events")
                .whereEqualTo("organizer", this.getName())
                .whereLessThanOrEqualTo("finalDeadline", now)
                .orderBy("finalDeadline")
                .get()
                .addOnSuccessListener(snap -> {
                    for (DocumentSnapshot doc : snap.getDocuments()) {
                        expireInvitesForEvent(doc.getId());
                    }
                });
    }

    /**
     * This method declines people still haven't respond to invitation passed
     * the final deadline. Called by the above method.
     *
     * @param eventId   Firebase doc id of event
     * @return
     */
    private Task<Void> expireInvitesForEvent(String eventId) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference evRef = db.collection("events").document(eventId);

        return db.runTransaction(trans -> {
            DocumentSnapshot ev = trans.get(evRef);

            // get current lists
            List<String> invited = (List<String>) ev.get("invitedList");
            List<String> declined = (List<String>) ev.get("declinedList");

            if (invited == null || invited.isEmpty()) return null;
            if (declined == null) declined = new ArrayList<>(); // because empty lists in firebase return null

            // update local events
            List<String> newDeclined = new ArrayList<>(declined);
            newDeclined.addAll(invited);

            Map<String, Object> updates = new HashMap<>();
            updates.put("invitedList", new ArrayList<String>()); // clear invited
            updates.put("declinedList", newDeclined); // append newly declined to list of declines

            trans.update(evRef, updates);

            // update online
            for (String entrantId : invited) {
                DocumentReference userRef = db.collection("users").document(entrantId);
                trans.update(userRef,
                        "onInvite", FieldValue.arrayRemove(eventId),
                        "onDeclined", FieldValue.arrayUnion(eventId)
                );
            }
            return null;
        });
    }

    /**
     * Set this organizer's events
     * @param orgEvents List of event's Firebase doc id
     */
    public void setOrgEvents(ArrayList<String> orgEvents) {
        this.orgEvents = orgEvents;
    }

    /**
     * Get this organizer's events
     * @return  List of event's Firebase doc id
     */
    public ArrayList<String> getOrgEvents() {
        return orgEvents;
    }

    /**
     * Add an event to this organizer's events
     * @param orgEvent Event to be added
     */
    public void addOrgEvent(String orgEvent) {
        this.orgEvents.add(orgEvent);
    }

    }
