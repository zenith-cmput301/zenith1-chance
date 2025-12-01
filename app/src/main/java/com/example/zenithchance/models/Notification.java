package com.example.zenithchance.models;

import java.util.ArrayList;
import java.util.List;

/**
 * The representative class for all Notifications.
 *
 * @author Lauren, Percy
 * @version 1.2
 */

public class Notification {
    private String eventName;
    private String status;
    private String entrant;
    private String toDisplay;

    /**
     * Empty constructor for class Notification.
     *
     * @return an instance of the Notification object
     */
    public Notification() {}

    /**
     * Constructor to create a new Notificatio class
     *
     * @param eventName Name of event
     * @param status    Status of entrant on event
     * @param entrant   Name of entrant
     */
    public Notification (String eventName, String status, String entrant) {
        this.entrant = entrant;
        this.eventName = eventName;
        this.status = status;
        this.toDisplay = "You have been " + status + " for " + eventName;
    }

    /**
     * Getters
     */
    public String getEventName() {
        return eventName;
    }

    public String getStatus() {
        return status;
    }

    public String getEntrant() {
        return entrant;
    }

    public String getToDisplay() {
        return toDisplay;
    }

    /**
     * Setters
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setEntrant(String entrant) {
        this.entrant = entrant;
    }

    public void setToDisplay(String toDisplay) {
        this.toDisplay = toDisplay;
    }
}
