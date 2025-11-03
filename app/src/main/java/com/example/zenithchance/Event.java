package com.example.zenithchance;

import java.util.Date;

/**
 * The representative class for all Events.
 * All information pertaining to an event should be found here.
 *
 * @author Percy
 * @version 1.0
 */

public class Event {
    public Date date;
    public String name;
    public String location;
    public String status;

    /**
     * Empty constructor for class Event.
     *
     * @return an instance of the Event object
     */
    public Event() {} // needed for Firestore

    /**
     * Constructor to create a new Event instance
     *
     * @param date      date of event
     * @param name      name of event
     * @param location  location of event
     * @param status    entrant's status on event (waiting, chosen, accepted, declined)
     * @return an instance of the Event object
     */
    public Event(Date date, String name, String location, String status) {
        this.date = date;
        this.name = name;
        this.location = location;
        this.status = status;
    }

}
