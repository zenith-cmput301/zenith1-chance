package com.example.entrantsprofilepage;
import static java.security.AccessController.getContext;

import android.provider.Settings.Secure;

import java.util.UUID;

// abstract User class. Entrant, Admin and Organizer will extend from this class.
// Represents a user object
public abstract class User {

        private String email;
        private String name;

        // REFERENCE: https://stackoverflow.com/questions/1389736/how-do-i-create-a-unique-id-in-java
        private final String userId;
        private String deviceId;

        public User(String userEmail, String userName) {
            this.email = userEmail;
            this.name = userName;
            this.userId = UUID.randomUUID().toString();
        }
        public void setEmail(String userEmail) {
            email = userEmail;
        }

        public void setName(String userName) {
            name = userName;
        }

        public String getUserId() {
            return userId;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        // method for returning deviceId


}
