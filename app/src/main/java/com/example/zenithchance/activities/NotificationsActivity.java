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


        // Initializing Buttons:
        backArrow = findViewById(R.id.backButton);
        notificationToggle = findViewById(R.id.toggleButton);

        // This sends you to the profileActivity page!
        Intent intent = getIntent();
        notificationList = intent.getExtras().getStringArrayList("notificationList");
        if (notificationList == null){
            notificationList = new ArrayList<>();
        }
        ListView notificationsListView= findViewById(R.id.notificationListView);
        ArrayAdapter notificationAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                notificationList
        );
        notificationsListView.setAdapter(notificationAdapter);


        ArrayList finalNotificationList = notificationList;
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putStringArrayListExtra("notificationList", finalNotificationList); // Might be able to delete this
                setResult(RESULT_OK, resultIntent);
                finish();
            }});

    }}