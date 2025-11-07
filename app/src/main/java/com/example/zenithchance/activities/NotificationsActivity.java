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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    ImageButton backArrow;
    ToggleButton notificationToggle;

    ArrayList notificationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notifications);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize buttons
        backArrow = findViewById(R.id.backButton);
        notificationToggle = findViewById(R.id.toggleButton);

        // === intent + extras handling ===
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {
            notificationList = extras.getStringArrayList("notificationList");
        }

        if (notificationList == null) {
            // Provide a backup list
            notificationList = new ArrayList<>();
            notificationList.add("No notifications available");
        }

        // === Setting up ListView ===
        ListView notificationsListView = findViewById(R.id.notificationListView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.notification_items,
                R.id.notificationText,
                notificationList
        );
        notificationsListView.setAdapter(adapter);

        // === Implementing Back Button ===
        backArrow.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putStringArrayListExtra("notificationList", notificationList);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}
