package com.example.zenithchance;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.example.zenithchance.managers.UserManager;
import com.example.zenithchance.models.Admin;
import com.example.zenithchance.models.Entrant;
import com.example.zenithchance.models.Organizer;
import com.example.zenithchance.models.User;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Tests the Organizer class to ensure that Entrant, Organizer, and Admin
 * users can be added to and deleted from Firestore successfully.
 * @author Lauren
 */
public class OrganizerTest {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final UserManager users = UserManager.getInstance();

    /**
     * Tests that an Entrant user can be added to Firestore.
     * Creates an Entrant, adds it using addUser, and checks that the
     * Firestore document exists with the correct name and type fields.
     *
     * @throws Exception if Firestore operations fail
     */
    @Test
    public void addEntrantAddsToFirestore() throws Exception {
        Entrant e = new Entrant();
        e.setUserId("TEST_Entrant_Add");
        e.setType("entrant");
        e.setName("New Entrant");
        e.setEmail("new@entrant.com");

        Tasks.await(users.addUser(e));

        DocumentSnapshot doc = Tasks.await(db.collection("users").document("TEST_Entrant_Add").get());
        assertTrue(doc.exists());
        assertEquals("entrant", doc.getString("type"));
        assertEquals("New Entrant", doc.getString("name"));
    }
    @Test
    public void addEventToFirestore() throws Exception {
    
    }
}