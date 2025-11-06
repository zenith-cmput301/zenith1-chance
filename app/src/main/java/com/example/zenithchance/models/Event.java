package com.example.zenithchance.models;

import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;

/**
 * The representative class for all Events.
 * All information pertaining to an event should be found here.
 *
 * @author Percy
 * @version 1.1
 */

public class Event implements Serializable {
    private Date date;
    private String name;
    private String location;
    private String organizer;
    private String status;
    private String description;
    private Boolean geolocation_required;
    private Date registration_date;
    private Integer max_entrants;
    private String imageUrl;
    private String docId;
    private ArrayList<Entrant> waitingList = new ArrayList<Entrant>();

//    private ArrayList<Entrant> waiting_list;

//    Unique event ID for routing during QR code scanning to be implemented down the line
//    private Integer event_id;

    /**
     * Empty constructor for class Event.
     *
     * @return an instance of the Event object
     */
    public Event() {} // needed for Firestore

    /**
     * Constructor to create a new Event instance
     *
     * @param date                  date of event
     * @param name                  name of event
     * @param location              location of event
     * @param status                entrant's status on event (waiting, chosen, accepted, declined)
     * @param organizer             organizer of the event
     * @param description           event description
     * @param geolocation_required  boolean representing if geolocation is toggled
     * @param registration_date     date in which to close registration
     * @param max_entrants          the maximum number of entrants allowed to attend the event
     * @return an instance of the Event object
     */
    public Event(Date date, String name, String location, String status, String organizer, String description, Boolean geolocation_required, Date registration_date, Integer max_entrants, String imageUrl) {
        this.date = date;
        this.name = name;
        this.location = location;
        this.status = status;
        this.organizer = organizer;
        this.description = description;
        this.geolocation_required = geolocation_required;
        this.registration_date = registration_date;
        this.max_entrants = max_entrants;
    }

    /**
     * Adds entrant to this event's waiting list
     */
    public void addWaiting(Entrant entrant) {
        // TODO: given entrant, add to local waiting list AND on Firebase's document "events", this event's array field "waitingList"
    }

    /**
     *
     * Getters
     *
     */
    public Date getDate() { return this.date; }
    public String getName() { return this.name; }
    public String getLocation() { return this.location; }
    public String getStatus() { return this.status; }
    public String getOrganizer() { return this.organizer; }
    public String getDescription() { return this.description; }
    public Boolean getGeolocationRequired() { return this.geolocation_required; }
    public Date getRegistrationDate() { return this.registration_date; }
    public Integer getMaxEntrants() { return this.max_entrants; }
    public String getImageUrl() { return this.imageUrl; }
    public String getDocId() { return docId; }

    /**
     *
     * Setters
     *
     */

    public void setMaxEntrants(Integer max_entrants) { this.max_entrants = max_entrants; }
    public void setRegistrationDate(Date registration_date) { this.registration_date = registration_date; }
    public void setGeolocationRequired(Boolean geolocation_required) { this.geolocation_required = geolocation_required; }
    public void setDescription(String description) { this.description = description; }
    public void setStatus(String status) { this.status = status; }
    public void setOrganizer(String organizer) { this.organizer = organizer; }
    public void setLocation(String location) { this.location = location; }
    public void setName(String name) { this.name = name; }
    public void setDate(Date date) { this.date = date; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setDocId(String docId) { this.docId = docId; }
}
