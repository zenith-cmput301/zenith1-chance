package com.example.zenithchance.activities;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

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

    ArrayList notificationList;

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



        // Initialize buttons
        backArrow = findViewById(R.id.backButton);
        notificationToggle = findViewById(R.id.toggleButton);
        if(!notificationToggle.isChecked()){
        // This is temporary, as it will be replaced later on
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {
            notificationList = extras.getStringArrayList("notificationList");
        }

        if (notificationList == null) {
            // Provide a backup list if there isn't one already
            notificationList = new ArrayList<>();
            notificationList.add("No notifications available");
        }}else{
            notificationList = new ArrayList<>();
            notificationList.add("Notifications Blocked");
        }

        notificationToggle.setOnCheckedChangeListener((buttonView, isChecked)-> {
                    if(!notificationToggle.isChecked()){
                        // Same as above, will turn into a function once NotificationManager is fixed
                        Intent intent = getIntent();
                        Bundle extras = intent.getExtras();

                        if (extras != null) {
                            notificationList = extras.getStringArrayList("notificationList");
                        }

                        if (notificationList == null) {
                            // Provide a backup list
                            notificationList = new ArrayList<>();
                            notificationList.add("No notifications available");
                        }}else{
                        notificationList = new ArrayList<>();
                        notificationList.add("Notifications Blocked");
                    }});

        // Setting up ListView
        ListView notificationsListView = findViewById(R.id.notificationListView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.notification_items,
                R.id.notificationText,
                notificationList
        );
        notificationsListView.setAdapter(adapter);

        // Implementing Back Button OnClickListener
        backArrow.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putStringArrayListExtra("notificationList", notificationList);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
                    }

    private boolean isRunningInTest() {
        return android.app.ActivityManager.isRunningInTestHarness()
                || "true".equals(System.getProperty("IS_TEST"));
    }}

