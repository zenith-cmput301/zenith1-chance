package com.example.zenithchance.models;

import java.util.ArrayList;
import java.util.List;

public class Notification {
    private String eventName;
    private String status;
    private String entrant;
    private String toDisplay;

    public Notification() {}

    public Notification (String eventName, String status, String entrant) {
        this.entrant = entrant;
        this.eventName = eventName;
        this.status = status;
        this.toDisplay = "You have been " + status + " for " + eventName;
    }

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
