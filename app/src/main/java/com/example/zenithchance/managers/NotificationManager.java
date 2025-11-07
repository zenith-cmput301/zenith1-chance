package com.example.zenithchance.managers;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.zenithchance.models.Notification;
import com.example.zenithchance.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


/**
 * This class reads and writes notifications to the Firestore database.
 */
public final class NotificationManager {

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
            if (!task.isSuccessful()) throw task.getException();
            return notification;
        });
    }

    // This might be wrong!! CHECK HERE
    public CollectionReference getUsersNotifications(User user){
        DocumentReference docRef = notificationCollection.document();

        docRef.collection("notification").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    return docRef;}

    public boolean isUserBlocked(User user){ // Returns false if user is blocked

        if (FirebaseDatabase.getInstance().getReference("notifications/"+user.getUserId()) == null){
            return false;
        } else {
            return true;
        }
    }
}

