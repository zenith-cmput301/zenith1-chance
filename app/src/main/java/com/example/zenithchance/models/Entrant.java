package com.example.zenithchance.models;

import java.util.ArrayList;

/**
 * This class represents Entrant users.
 */

public class Entrant extends User {
    private ArrayList<Event> events;

    public Entrant() {
        setType("entrant");
        events = new ArrayList<Event>();
    }
}
