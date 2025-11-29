package com.example.zenithchance.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.zenithchance.R;
//import com.example.zenithchance.managers.NotificationManager;
import com.example.zenithchance.managers.UserManager;
import com.example.zenithchance.models.Notification;
import com.example.zenithchance.models.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class to display notifications for users. Does not currently display anything, but will in a future release
 *
 * @author Lauren
 * @version 4.0
 *
 */
public class NotificationsActivity extends AppCompatActivity {

    ImageButton backArrow;
    ToggleButton notificationToggle;
    private final CollectionReference userCollection =
            FirebaseFirestore.getInstance().collection("users");


    /**
     * OnCreate
     *
     * @param savedInstanceState Saved data to use with the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        if (!isRunningInTest()) {
            EdgeToEdge.enable(this);
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
        User myUser = UserManager.getInstance().getCurrentUser();
        userCollection.document(myUser.getUserId())
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null || snapshot == null || !snapshot.exists()) return;

                    myUser.setNotifications((List<String>) snapshot.get("notifications"));
                    // Update notifications automatically
                });

        // Initialize buttons
        backArrow = findViewById(R.id.backButton);
        notificationToggle = findViewById(R.id.toggleButton);
        notificationToggle.setChecked(myUser.getNotificationStatus());

        notificationToggle.setOnClickListener(v -> {
                    myUser.updateNotificationStatus(myUser.getNotificationStatus());
                    UserManager.getInstance().updateNotificationStatus(myUser);
                });

        // Setting up ListView
        ListView notificationsListView = findViewById(R.id.notificationListView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.notification_items,
                R.id.notificationText,
                myUser.getNotifications()
        );
        notificationsListView.setAdapter(adapter);

        // Implementing Back Button OnClickListener
        backArrow.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            setResult(RESULT_OK, resultIntent);
            finish();
        });
                    }

    private boolean isRunningInTest() {
        return android.app.ActivityManager.isRunningInTestHarness()
                || "true".equals(System.getProperty("IS_TEST"));
    }}

