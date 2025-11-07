package com.example.zenithchance.models;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;


/**
 * This class represents a User object.
 * This class is the superclass for entrants, organizers, and admins.
 *
 * @author Sabrina
 */
public abstract class User {

    @DocumentId
    private String id; // Firestore document ID == deviceId

    private String email;
    private String name;
    private String type;

    public User() {}

    // Optional convenience getter so other code can still call getUserId()
    @Exclude
    public String getUserId() { return id; }

    @Exclude
    public void setUserId(String userId) { this.id = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
