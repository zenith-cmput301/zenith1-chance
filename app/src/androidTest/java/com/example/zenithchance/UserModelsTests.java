package com.example.zenithchance;
import org.junit.Test;
import static org.junit.Assert.*;

import com.example.zenithchance.models.Admin;
import com.example.zenithchance.models.Entrant;
import com.example.zenithchance.models.Organizer;
import com.example.zenithchance.models.User;


/**
 * Tests the User Model (superclass of Entrant, Admin, Organizer).
 * Ensures that each user type works smoothly.
 */
public class UserModelsTests {

    @Test
    public void testEntrantInfo() {
        User entrant = new Entrant();
        entrant.setName("John");
        entrant.setUserId("john123");
        entrant.setType("entrant");

        assertEquals("John", entrant.getName());
        assertEquals("john123", entrant.getUserId());
        assertEquals("entrant", entrant.getType());
    }

    @Test
    public void testOrganizerInfo() {
        User organizer = new Organizer();
        organizer.setName("John");
        organizer.setUserId("john123");
        organizer.setType("entrant");

        assertEquals("John", organizer.getName());
        assertEquals("john123", organizer.getUserId());
        assertEquals("entrant", organizer.getType());
    }

    @Test
    public void testAdminInfo() {
        User admin = new Admin();
        admin.setName("John");
        admin.setUserId("john123");
        admin.setType("entrant");

        assertEquals("John", admin.getName());
        assertEquals("john123", admin.getUserId());
        assertEquals("entrant", admin.getType());
    }
}
