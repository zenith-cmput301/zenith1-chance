package com.example.zenithchance.models;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;


/**
 * This class represents a User object.
 * This class is the superclass for entrants, organizers, and admins.
 * Firestore document ID == deviceId == userId.
 */
public abstract class User {

    protected String email;
    protected String name;
    protected String type;

    @DocumentId
    @Exclude
    protected String userId;  // same as deviceId (Firestore doc ID)

    public User() { }

    public String getUserId() { return userId; }
    public void setUserId(String firestoreDocId) { this.userId = firestoreDocId; }

    public String getDeviceId() { return userId; }
    public void setDeviceId(String deviceId) { this.userId = deviceId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
