package com.example.zenithchance.managers;

import android.util.Log; // Add this import

import com.example.zenithchance.models.Admin;
import com.example.zenithchance.models.Entrant;
import com.example.zenithchance.models.Notification;
import com.example.zenithchance.models.Organizer;
import com.example.zenithchance.models.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


/**
 * This class reads and writes Users to the Firestore database.
 * @author Sabrina, Lauren, Percy
 */
public final class UserManager {
    // Singleton
    private static final UserManager shared = new UserManager();

    public static UserManager getInstance() {
        return shared;
    }

    private User currentUser;

    private UserManager() {
    }

    private final List<Entrant> entrants = new ArrayList<>();
    private final List<Organizer> organizers = new ArrayList<>();
    private final List<Admin> admins = new ArrayList<>();
    private final CollectionReference userCollection = FirebaseFirestore.getInstance().collection("users");
    private final CollectionReference notificationsCollection = FirebaseFirestore.getInstance().collection("notifications");

    private ListenerRegistration listener;

    public User getCurrentUser() {
        return currentUser;
    }
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }


    /**
     * Starts a listener; useful for operations that require real-time updates.
     *
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
                    if (e != null) {
                        e.setUserId(doc.getId());
                        entrants.add(e);
                    }
                } else if ("organizer".equals(type)) {
                    Organizer o = doc.toObject(Organizer.class);
                    if (o != null) {
                        o.setUserId(doc.getId());
                        organizers.add(o);
                    }
                } else if ("admin".equals(type)) {
                    Admin a = doc.toObject(Admin.class);
                    if (a != null) {
                        a.setUserId(doc.getId());
                        admins.add(a);
                    }
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
     * Adds a user to the Firestore "users" collection with the deviceId as the docId.
     *
     * @param user This is the user to be added to the users collection.
     */
    public Task<User> addUser(User user) {
        String type = user.getType();
        if (type == null) return Tasks.forException(new IllegalArgumentException("type is null"));
        type = type.toLowerCase(Locale.US);
        user.setType(type);

        String userId = user.getUserId();
        if (userId == null || userId.isEmpty()) {
            return Tasks.forException(new IllegalArgumentException("userId is required"));
        }

        DocumentReference docRef = userCollection.document(userId);

        return docRef.set(user).continueWith(task -> {
            if (!task.isSuccessful()) throw task.getException();
            return user;
        });
    }


    /**
     * Deletes a user from the Firestore "users" collection using the userId.
     *
     * @param userId This is the userId of the user to be deleted from the users collection.
     */
    public void deleteUserById(String userId) {
        if (userId == null || userId.isEmpty()) {
            System.out.println("deleteUserById called with empty ID");
            return;
        }

        userCollection.document(userId).delete()
                .addOnSuccessListener(aVoid -> System.out.println("Deleted user " + userId))
                .addOnFailureListener(e -> System.err.println("deleteUserById failed: " + e.getMessage()));
    }


    /**
     * Updates a user's name in the Firestore "users" collection using their document id.
     *
     * @param user This is the user to have their name updated.
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
     *
     * @param user This is the user to have their email updated.
     */
    public void updateUserEmail(User user) {
        String id = user.getUserId();
        if (id == null || id.isEmpty()) return;
        userCollection.document(id).update("email", user.getEmail())
                .addOnSuccessListener(aVoid -> System.out.println("E-mail updated"))
                .addOnFailureListener(e -> System.err.println("Failed: " + e.getMessage()));
    }

    // asynchronous functions that fetch users. Start a background network call immediately.
    public Task<List<Entrant>> fetchEntrants() {
        return userCollection.whereIn("type", Arrays.asList("entrant","Entrant"))
                .get()
                .continueWith(t -> {
                    if (!t.isSuccessful()) throw Objects.requireNonNull(t.getException());
                    List<Entrant> out = new ArrayList<>();
                    for (DocumentSnapshot d : t.getResult().getDocuments()) {
                        Entrant e = d.toObject(Entrant.class);
                        if (e != null) { e.setUserId(d.getId()); e.setType("entrant"); out.add(e); }
                    }
                    System.out.println("Fetched entrants: " + out.size());
                    return out;
                });
    }

    public Task<List<Organizer>> fetchOrganizers() {
        return userCollection.whereIn("type", Arrays.asList("organizer","Organizer"))
                .get()
                .continueWith(t -> {
                    if (!t.isSuccessful()) throw Objects.requireNonNull(t.getException());
                    List<Organizer> out = new ArrayList<>();
                    for (DocumentSnapshot d : t.getResult().getDocuments()) {
                        Organizer o = d.toObject(Organizer.class);
                        if (o != null) { o.setUserId(d.getId()); o.setType("organizer"); out.add(o); }
                    }
                    System.out.println("Fetched organizers: " + out.size());
                    return out;
                });
    }
// NEW ADDITIONS FOR NOTIFICATIONS HERE:
    /**
     * Updates a user's Notification Status in the Firestore "users" collection using their document id.
     *
     * @param user This is the user to have their NotificationStatus updated.
     */
    public void updateNotificationStatus(User user) {
        String id = user.getUserId();
        if (id == null || id.isEmpty()) return;
        userCollection.document(id).update("notificationStatus", user.getNotificationStatus())
                .addOnSuccessListener(aVoid -> System.out.println("notificationStatus updated"))
                .addOnFailureListener(e -> System.err.println("Failed: " + e.getMessage()));
    }
    /**
     * Updates a user's Notifications in the Firestore "users" collection using their document id.
     * Credit: Gemini AI for debugging purposes
     * @author Lauren
     *
     * @param user This is the user to have their Notifications updated.
     */
    public void updateUserNotifications(User user) {
        String id = user.getUserId();

        // Checks the ID
        if (id == null || id.isEmpty()) {
            Log.e("UserManager", "CRITICAL ERROR: User ID is NULL or Empty. Cannot update.");
            return;
        }

        // Checks the List Content
        List<String> currentList = user.getNotifications();
        if (currentList == null) {
            Log.e("UserManager", "CRITICAL ERROR: getNotifications() returned null.");
            return;
        }

        // Create the fresh copy
        List<String> listForFirestore = new ArrayList<>(currentList);

        userCollection.document(id)
                .update("notifications", listForFirestore)
                .addOnSuccessListener(aVoid -> Log.d("UserManager", "SUCCESS: Firestore confirms update for Doc: " + id))
                .addOnFailureListener(e -> Log.e("UserManager", "FAILURE: Firestore rejected update.", e));
    }

    public void sendNotification(String eid, String status, String uid){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(uid);
        DocumentReference evRef = db.collection("events").document(eid);

        userRef.get().addOnSuccessListener(userSnap -> {
            // don't send noti if user blocked noti
            Boolean enabled = userSnap.getBoolean("notificationStatus");
            if (!enabled) return;

            evRef.get().addOnSuccessListener(eventSnap -> {
                // preps for notification
                String eventName = eventSnap.getString("name");
                String entrantName = userSnap.getString("name");
                Notification notification = new Notification(eventName, status, entrantName);
                String display = notification.getToDisplay();

                // push to firebase
                userRef.update("notifications", FieldValue.arrayUnion(display)); // to entrant's noti list
                notificationsCollection.add(notification); // to general noti collection
            });
        });
    }
}
