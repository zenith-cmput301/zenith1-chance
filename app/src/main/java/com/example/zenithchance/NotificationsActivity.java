package com.example.zenithchance;

import static android.widget.AdapterView.*;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

        Adapter notificationAdapter = new ArrayAdapter<>(this, R.layout.activity_notifications, notificationList);

        // Initializing Buttons:
        backArrow = findViewById(R.id.backButton);
        notificationToggle = findViewById(R.id.toggleButton);

        // This sends you to the profileActivity page!

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putStringArrayListExtra("notificationList", notificationList); // Might be able to delete this
                setResult(RESULT_OK, resultIntent);
                finish();
            }});

    }}