package com.example.zenithchance.models;


import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents Entrant users.
 */

public class Entrant extends User {
    private ArrayList<Event> onWaiting;
    private ArrayList<Event> onInvite;
    private ArrayList<Event> onAccepted;

    public Entrant() {
        setType("entrant");
        onWaiting = new ArrayList<Event>();
        onInvite = new ArrayList<Event>();
        onAccepted = new ArrayList<Event>();

        getEvents();
    }

    /**
     * This method get this entrant's events
     */
    public void getEvents() {
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
