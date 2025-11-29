package com.example.zenithchance.managers;

import com.example.zenithchance.models.Notification;
import com.example.zenithchance.models.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.Locale;
import java.util.Objects;


/**
 * This class reads and writes Users to the Firestore database.
 */
public final class NotificationManager {
    // Singleton
    private static final NotificationManager shared = new NotificationManager();

    public static NotificationManager getInstance() {
        return shared;
    }

    private Notification currentNotification;

    private NotificationManager() {
    }
    private final CollectionReference notificationCollection =
            FirebaseFirestore.getInstance().collection("notifications");
    private ListenerRegistration listener;

    public Notification getCurrentNotification() {
        return currentNotification;
    }

    public void setCurrentNotification(Notification notification) {
        this.currentNotification = notification;
    }

    public Task<Notification> addNotification(Notification notification) {
        String uid = notification.getUid();
        if (uid == null) return Tasks.forException(new IllegalArgumentException("uid is null"));
        notification.setUid(uid);

        DocumentReference docRef = notificationCollection.document();

        return docRef.set(notification).continueWith(task -> {
            if (!task.isSuccessful()) throw Objects.requireNonNull(task.getException());
            return notification;
        });
    }

    public boolean isUserBlocked(User user){ // Returns false if user is blocked
        return !Objects.equals(user.getUserId(), notificationCollection.getId());
    }
}
