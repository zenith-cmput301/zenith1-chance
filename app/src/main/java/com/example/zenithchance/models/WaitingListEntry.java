package com.example.zenithchance.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;

public class WaitingListEntry implements Serializable {
    private String entryId;           // Firestore document ID
    private String eventId;           // Event document ID
    private String userId;            // User/Entrant document ID
    private GeoPoint eventLocation;   // Where the event is
    private GeoPoint entrantLocation; // Where entrant joined from
    private Timestamp joinedAt;       // When they joined

    // Empty constructor required for Firestore
    public WaitingListEntry() {}

    public WaitingListEntry(String eventId, String userId,
                            GeoPoint eventLocation, GeoPoint entrantLocation) {
        this.eventId = eventId;
        this.userId = userId;
        this.eventLocation = eventLocation;
        this.entrantLocation = entrantLocation;
        this.joinedAt = Timestamp.now();
    }

    // Getters and Setters
    public String getEntryId() { return entryId; }
    public void setEntryId(String entryId) { this.entryId = entryId; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public GeoPoint getEventLocation() { return eventLocation; }
    public void setEventLocation(GeoPoint eventLocation) { this.eventLocation = eventLocation; }

    public GeoPoint getEntrantLocation() { return entrantLocation; }
    public void setEntrantLocation(GeoPoint entrantLocation) { this.entrantLocation = entrantLocation; }

    public Timestamp getJoinedAt() { return joinedAt; }
    public void setJoinedAt(Timestamp joinedAt) { this.joinedAt = joinedAt; }
}
