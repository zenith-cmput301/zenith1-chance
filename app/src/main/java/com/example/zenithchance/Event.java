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
    private Date date;
    private String name;
    private String location;
    private String organizer;
    private String status;
    private String description;

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
    public Event(Date date, String name, String location, String status, String organizer, String description) {
        this.date = date;
        this.name = name;
        this.location = location;
        this.status = status;
        this.organizer = organizer;
        this.description = description;
    }

    public Date getDate() { return this.date; }
    public String getName() { return this.name; }
    public String getLocation() { return this.location; }
    public String getStatus() { return this.status; }
    public String getOrganizer() { return this.organizer; }
    public String getDescription() { return this.description; }

}
