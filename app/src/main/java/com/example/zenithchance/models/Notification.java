package com.example.zenithchance.models;

public class Notification {
    private String eventName;
    private String status;
    private String uid; // recipient entrant's Firebase id
    private String toDisplay;

    public Notification (String eventName, String status, String uid) {
        this.uid = uid;
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

    public String getUid() {
        return uid;
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

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setToDisplay(String toDisplay) {
        this.toDisplay = toDisplay;
    }
}
