package com.example.zenithchance.models;


import java.io.Serializable;

/**
 * This class represents Organizer users.
 */
public class Organizer extends User implements Serializable {

    public Organizer() {
        setType("organizer");
    }
}
