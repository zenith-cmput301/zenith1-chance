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
 * Tests the UserManager class to ensure that Entrant, Organizer, and Admin
 * users can be added to and deleted from Firestore successfully.
 * @author Sabrina
 */
public class UserManagerTest {

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

    /**
     * Tests that an Entrant user can be deleted from Firestore.
     * Adds a temporary Entrant document, confirms it exists, deletes it,
     * and verifies that the document no longer exists.
     *
     * @throws Exception if Firestore operations fail
     */
    @Test
    public void deleteEntrantRemovesFromFirestore() throws Exception {
        Entrant e = new Entrant();
        e.setUserId("TEST_Entrant_Delete");
        e.setType("Entrant");
        e.setName("Delete Entrant");
        e.setEmail("delentrant@test.com");
        Tasks.await(users.addUser(e));

        DocumentSnapshot before = Tasks.await(db.collection("users").document("TEST_Entrant_Delete").get());
        assertTrue(before.exists());

        users.deleteUserById("TEST_Entrant_Delete");
        Thread.sleep(2000);

        DocumentSnapshot after = Tasks.await(db.collection("users").document("TEST_Entrant_Delete").get());
        assertFalse(after.exists());
    }


    /**
     * Tests that an Organizer user can be added to Firestore.
     * Creates an Organizer, adds it using addUser, and checks that the
     * Firestore document exists with the correct name and type fields.
     *
     * @throws Exception if Firestore operations fail
     */
    @Test
    public void addOrganizerAddsToFirestore() throws Exception {
        Organizer o = new Organizer();
        o.setUserId("TEST_Organizer_Add");
        o.setType("organizer");
        o.setName("New Organizer");
        o.setEmail("new@organizer.com");

        Tasks.await(users.addUser(o));

        DocumentSnapshot doc = Tasks.await(db.collection("users").document("TEST_Organizer_Add").get());
        assertTrue(doc.exists());
        assertEquals("organizer", doc.getString("type"));
        assertEquals("New Organizer", doc.getString("name"));
    }


    /**
     * Tests that an Organizer user can be deleted from Firestore.
     * Adds a temporary Organizer document, confirms it exists, deletes it,
     * and verifies that the document no longer exists.
     *
     * @throws Exception if Firestore operations fail
     */
    @Test
    public void deleteOrganizerRemovesFromFirestore() throws Exception {
        Organizer o = new Organizer();
        o.setUserId("TEST_Organizer_Delete");
        o.setType("Organizer");
        o.setName("Delete Organizer");
        o.setEmail("delorg@test.com");
        Tasks.await(users.addUser(o));

        DocumentSnapshot before = Tasks.await(db.collection("users").document("TEST_Organizer_Delete").get());
        assertTrue(before.exists());

        users.deleteUserById("TEST_Organizer_Delete");
        Thread.sleep(2000);

        DocumentSnapshot after = Tasks.await(db.collection("users").document("TEST_Organizer_Delete").get());
        assertFalse(after.exists());
    }

    /**
     * Tests that an Admin user can be added to Firestore.
     * Creates an Admin, adds it using addUser, and checks that the
     * Firestore document exists with the correct name and type fields.
     *
     * @throws Exception if Firestore operations fail
     */
    @Test
    public void addAdminAddsToFirestore() throws Exception {
        Admin a = new Admin();
        a.setUserId("TEST_Admin_Add");
        a.setType("admin");
        a.setName("New Admin");
        a.setEmail("new@admin.com");

        Tasks.await(users.addUser(a));

        DocumentSnapshot doc = Tasks.await(db.collection("users").document("TEST_Admin_Add").get());
        assertTrue(doc.exists());
        assertEquals("admin", doc.getString("type"));
        assertEquals("New Admin", doc.getString("name"));
    }


    /**
     * Tests that an Admin user can be deleted from Firestore.
     * Adds a temporary Admin document, confirms it exists, deletes it,
     * and verifies that the document no longer exists.
     *
     * @throws Exception if Firestore operations fail
     */
    @Test
    public void deleteAdminRemovesFromFirestore() throws Exception {
        Admin a = new Admin();
        a.setUserId("TEST_Admin_Delete");
        a.setType("Admin");
        a.setName("Delete Admin");
        a.setEmail("deladmin@test.com");
        Tasks.await(users.addUser(a));

        DocumentSnapshot before = Tasks.await(db.collection("users").document("TEST_Admin_Delete").get());
        assertTrue(before.exists());

        users.deleteUserById("TEST_Admin_Delete");
        Thread.sleep(2000);

        DocumentSnapshot after = Tasks.await(db.collection("users").document("TEST_Admin_Delete").get());
        assertFalse(after.exists());
    }
    /**
     * Tests that notifications successfully send to Firestore.
     * Creates an Entrant, adds it using addUser, and checks that the
     * Firestore document exists with the correct name and type fields.
     * Credit: Gemini AI tools for debugging purposes
     * @author Lauren
     */
    @Test
    public void testSendNotification() throws ExecutionException, InterruptedException {
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

        //users.sendNotification("Test Event", "Test Status", e);
        Thread.sleep(3000); // Wait 3 seconds for Firestore to process

        // Fetching the UPDATED document from Firestore to verify the change actually happened
        DocumentSnapshot updatedDoc = Tasks.await(db.collection("users").document("TEST_Entrant_Add").get());

        List<String> notifications = (List<String>) updatedDoc.get("notifications");

        assertNotNull("Notifications list should not be null", notifications);
        assertEquals("Should have 1 notification", 1, notifications.size());
        assertTrue("Notification content mismatch", notifications.get(0).contains("Test Status"));
        assertEquals(1, e.getNotifications().size());
    }

    /**
     * Deletes all test documents created during the tests.
     * This runs automatically after each test.
     *
     * @throws Exception if Firestore operations fail
     */
    @After
    public void cleanUpTestDocuments() throws Exception {
        List<String> testIds = Arrays.asList(
                "TEST_Entrant_Add", "TEST_Entrant_Delete",
                "TEST_Organizer_Add", "TEST_Organizer_Delete",
                "TEST_Admin_Add", "TEST_Admin_Delete"
        );

        for (String id : testIds) {
            Tasks.await(db.collection("users").document(id).delete());
        }
    }


}
