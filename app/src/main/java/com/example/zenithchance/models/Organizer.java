package com.example.zenithchance.models;


import java.io.Serializable;
import java.util.ArrayList;

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

    public void setOrgEvents(ArrayList<String> orgEvents) {
        this.orgEvents = orgEvents;
    }

    public void addOrgEvent(String orgEvent) {
        this.orgEvents.add(orgEvent);
    }
}
