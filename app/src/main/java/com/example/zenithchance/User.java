package com.example.zenithchance;

import com.google.firebase.firestore.Exclude;


public abstract class User {

    private String email;
    private String name;
    private String deviceId;
    private String type;

    @Exclude
    private String userId; // matches Firestore doc ID, not stored as field

    public User() { } // required empty constructor for Firestore


    public String getUserId() { return userId; }
    public void setUserId(String firestoreDocId) { this.userId = firestoreDocId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

}
