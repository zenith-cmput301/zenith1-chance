package com.example.zenithchance;

import com.google.firebase.firestore.Exclude;


/**
 * This class represents a User object.
 * This class is the superclass for entrants, organizers, and admins.
 */
public abstract class User {

    protected String email;
    protected String name;
    protected String deviceId;
    protected String type;

    @Exclude
    protected String userId; // matches Firestore doc ID, not stored as field

    public User() { } // required empty constructor for Firestore


    public String getUserId() { return userId; }
    public void setUserId(String firestoreDocId) { this.userId = firestoreDocId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDeviceId() { return deviceId; }

    public void setDeviceId(String userDeviceId) { this.deviceId = userDeviceId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

}
