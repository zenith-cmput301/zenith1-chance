package com.example.zenithchance.models;

import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private Date finalDeadline;
    private Integer max_entrants;
    private String imageUrl;
    private ArrayList<String> waitingList = new ArrayList<String>();
    private ArrayList<String> invitedList = new ArrayList<String>();
    private ArrayList<String> acceptedList = new ArrayList<String>();
    private ArrayList<String> declinedList = new ArrayList<String>();

    private String docId;

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
     * @param finalDeadline         date of finalizing attendees
     * @param max_entrants          the maximum number of entrants allowed to attend the event
     * @return an instance of the Event object
     */
    public Event(Date date, String name, String location, String status, String organizer, String description, Boolean geolocation_required, Date registration_date, Date finalDeadline, Integer max_entrants, String imageUrl) {
        this.date = date;
        this.name = name;
        this.location = location;
        this.status = status;
        this.organizer = organizer;
        this.description = description;
        this.geolocation_required = geolocation_required;
        this.registration_date = registration_date;
        this.finalDeadline = finalDeadline;
        this.max_entrants = max_entrants;
    }

    public void addWaitingList(String uid) {
        if (!waitingList.contains(uid)) waitingList.add(uid);
    }

    public void removeFromWaitingList(String uid) {
        this.waitingList.remove(uid);
    }

    public void addInvitedList(String uid) {
        if (!invitedList.contains(uid)) invitedList.add(uid);
    }

    public void removeFromInvitedList(String uid) {
        this.invitedList.remove(uid);
    }

    public void addAcceptedList(String uid) {
        if (!acceptedList.contains(uid)) acceptedList.add(uid);
    }

    public void addDeclinedList(String uid) {
        if (!declinedList.contains(uid)) declinedList.add(uid);
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
    public Date getFinalDeadline() { return this.finalDeadline; }
    public Integer getMaxEntrants() { return this.max_entrants; }
    public String getImageUrl() { return this.imageUrl; }
    public String getDocId() { return docId; }

    /**
     *
     * Setters
     *
     */

    public void setMaxEntrants(Integer max_entrants) { this.max_entrants = max_entrants; }
    public void setDescription(String description) { this.description = description; }
    public void setStatus(String status) { this.status = status; }
    public void setOrganizer(String organizer) { this.organizer = organizer; }
    public void setLocation(String location) { this.location = location; }
    public void setName(String name) { this.name = name; }
    public void setDocId(String docId) { this.docId = docId; }
}
