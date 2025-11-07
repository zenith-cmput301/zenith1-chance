package com.example.zenithchance.models;


import com.example.zenithchance.interfaces.LotteryService;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class represents Organizer users.
 */
public class Organizer extends User implements Serializable, LotteryService {
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
                .get()
                .addOnSuccessListener(snap -> {
                    for (var doc : snap.getDocuments()) {
                        runLotteryForEvent(doc.getId());
                    }
                });
    }
    public Task<Void> runLotteryForEvent(String eventId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference evRef = db.collection("events").document(eventId);

        return db.runTransaction(trans -> {
            DocumentSnapshot ev = trans.get(evRef);


            return null;
        });
    }

    public void setOrgEvents(ArrayList<String> orgEvents) {
        this.orgEvents = orgEvents;
    }

    public void addOrgEvent(String orgEvent) {
        this.orgEvents.add(orgEvent);
    }
}
