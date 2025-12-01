package com.example.zenithchance.models;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.List;


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
    private List<String> notifications = new ArrayList<>();
    private Boolean notificationStatus = true;

    public User() {}

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

    // ADDED FOR NOTIFICATION THINGS:

    public List<String> getNotifications() {
        return notifications;
    }

    public Boolean getNotificationStatus() {
        return notificationStatus;
    }

    public void updateNotificationStatus(Boolean notificationStatus) {
        this.notificationStatus = !notificationStatus;
    }
}
