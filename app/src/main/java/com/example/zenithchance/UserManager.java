package com.example.zenithchance;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;


// reads and writes users from Firebase database
public final class UserManager {
    // Singleton
    private static final UserManager shared = new UserManager();
    public static UserManager getInstance() { return shared; }
    private UserManager() {}

    private final List<Entrant> entrants = new ArrayList<>();
    private final List<Organizer> organizers = new ArrayList<>();
    private final List<Admin> admins = new ArrayList<>();

    private final CollectionReference userCollection =
            FirebaseFirestore.getInstance().collection("users");
    private ListenerRegistration listener;

    /** Listener for user collection. */
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
    public void stopListener() {
        if (listener != null) {
            listener.remove();
            listener = null;
        }
    }


    /** Add a user to Firestore */
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

    /** Delete a user from Firestore */
    public void deleteUser(User user) {
        String id = user.getUserId();
        if (id == null || id.isEmpty()) {
            System.out.println("deleteUser called with empty userId");
            return;
        }
        userCollection.document(id).delete()
                .addOnFailureListener(e -> System.err.println("deleteUser failed: " + e.getMessage()));
    }

    /** Update name */
    public void updateUserName(User user) {
        String id = user.getUserId();
        if (id == null || id.isEmpty()) return;
        userCollection.document(id).update("name", user.getName());
    }

    /** Update email */
    public void updateUserEmail(User user) {
        String id = user.getUserId();
        if (id == null || id.isEmpty()) return;
        userCollection.document(id).update("email", user.getEmail());
    }

    public List<Entrant> getEntrants() { return entrants; }
    public List<Organizer> getOrganizers() { return organizers; }
    public List<Admin> getAdmins() { return admins; }
}
