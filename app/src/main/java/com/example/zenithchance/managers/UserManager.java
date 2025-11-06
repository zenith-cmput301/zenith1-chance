package com.example.zenithchance.managers;

import com.example.zenithchance.models.Admin;
import com.example.zenithchance.models.Entrant;
import com.example.zenithchance.models.Organizer;
import com.example.zenithchance.models.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;


/**
 * This class reads and writes Users to the Firestore database.
 */
public final class UserManager {
    // Singleton
    private static final UserManager shared = new UserManager();
    public static UserManager getInstance() { return shared; }

    private User currentUser;

    private UserManager() {}

    private final List<Entrant> entrants = new ArrayList<>();
    private final List<Organizer> organizers = new ArrayList<>();
    private final List<Admin> admins = new ArrayList<>();

    private final CollectionReference userCollection =
            FirebaseFirestore.getInstance().collection("users");
    private ListenerRegistration listener;

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }


    /**
     * Starts a listener; useful for operations that require real-time updates.
     * @return ListenerRegistration
     * This is a listener for the Firebase "users" collection,
     */
    public ListenerRegistration startListener() {
        listener = userCollection.addSnapshotListener((value, error) -> {
            if (error != null) {
                System.err.println("Listen failed: " + error.getMessage());
                return;
            }
            if (value == null) return;

            entrants.clear();
            organizers.clear();
            admins.clear();

            for (DocumentSnapshot doc : value.getDocuments()) {
                String type = doc.getString("type");
                if ("entrant".equals(type)) {
                    Entrant e = doc.toObject(Entrant.class);
                    if (e != null) { e.setUserId(doc.getId()); entrants.add(e); }
                } else if ("organizer".equals(type)) {
                    Organizer o = doc.toObject(Organizer.class);
                    if (o != null) { o.setUserId(doc.getId()); organizers.add(o); }
                } else if ("admin".equals(type)) {
                    Admin a = doc.toObject(Admin.class);
                    if (a != null) { a.setUserId(doc.getId()); admins.add(a); }
                } else {
                    System.out.println("User doc with missing/unknown type: " + doc.getId());
                }
            }

            System.out.println("Synced users — entrants: " + entrants.size()
                    + ", organizers: " + organizers.size()
                    + ", admins: " + admins.size());
        });
        return listener;
    }

    /**
     * Stops a listener that has been started.
     */
    public void stopListener() {
        if (listener != null) {
            listener.remove();
            listener = null;
        }
    }


    /**
     * Adds a user to the Firestore "users" collection.
     * @param user
     * This is the user to be added to the users collection.
     */
    public void addUser(User user) {
        String type = user.getType();
        if (type == null) { System.out.println("addUser: type is null"); return; }
        type = type.toLowerCase();
        if (!(type.equals("entrant") || type.equals("organizer") || type.equals("admin"))) {
            System.out.println("addUser: invalid type: " + type);
            return;
        }
        user.setType(type);

        DocumentReference docRef = userCollection.document();
        user.setUserId(docRef.getId());
        docRef.set(user)
                .addOnFailureListener(e -> System.err.println("addUser failed: " + e.getMessage()));
    }

    /**
     * Deletes a user to the Firestore "users" collection.
     * @param user
     * This is the user to be deleted from the users collection.
     */
    public void deleteUser(User user) {
        String id = user.getUserId();
        if (id == null || id.isEmpty()) {
            System.out.println("deleteUser called with empty userId");
            return;
        }
        userCollection.document(id).delete()
                .addOnFailureListener(e -> System.err.println("deleteUser failed: " + e.getMessage()));
    }

    /**
     * Updates a user's name in the Firestore "users" collection using their document id.
     * @param user
     * This is the user to have their name updated.
     */
    public void updateUserName(User user) {
        String id = user.getUserId();
        if (id == null || id.isEmpty()) return;
        userCollection.document(id).update("name", user.getName())
                .addOnSuccessListener(aVoid -> System.out.println("Name updated"))
                .addOnFailureListener(e -> System.err.println("Failed: " + e.getMessage()));
    }

    /**
     * Updates a user's email in the Firestore "users" collection using their document id.
     * @param user
     * This is the user to have their email updated.
     */
    public void updateUserEmail(User user) {
        String id = user.getUserId();
        if (id == null || id.isEmpty()) return;
        userCollection.document(id).update("email", user.getEmail())
                .addOnSuccessListener(aVoid -> System.out.println("E-mail updated"))
                .addOnFailureListener(e -> System.err.println("Failed: " + e.getMessage()));
    }

    public List<Entrant> getEntrants() { return entrants; }
    public List<Organizer> getOrganizers() { return organizers; }
    public List<Admin> getAdmins() { return admins; }
}
