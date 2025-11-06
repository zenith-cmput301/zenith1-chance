package com.example.zenithchance.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.DocumentReference;

public class OrganizerEventDetailsActivity extends AppCompatActivity {
import com.example.zenithchance.R;

public class OrganizerEventDetailsActivity extends AppCompatActivity {

    private TextView tvEventName, tvEventDate, tvEventTime, tvLocation, tvOrganizer, tvAboutDescription;
    private ImageView imageHeader;
    private Button btnPeople, btnEdit, btnMap;

    private FirebaseFirestore db;
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Replace with your XML name if different

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Retrieve eventId passed from previous activity or fallback for testing
        eventId = getIntent().getStringExtra("eventId");
        if (eventId == null) {
            eventId = "MoKFp23CsnhegmhYe5Ue"; // Example from your Firestore screenshot
        }

        // Initialize UI components
        tvEventName = findViewById(R.id.tvEventName);
        tvEventDate = findViewById(R.id.tvEventDate);
        tvEventTime = findViewById(R.id.tvEventTime);
        tvLocation = findViewById(R.id.tvLocation);
        tvOrganizer = findViewById(R.id.tvOrganizer);
        tvAboutDescription = findViewById(R.id.tvAboutDescription);
        imageHeader = findViewById(R.id.imageHeader);

        btnPeople = findViewById(R.id.btnPeople);
        btnEdit = findViewById(R.id.btnEdit);
        btnMap = findViewById(R.id.btnMap);

        // Load event details from Firestore
        loadEventDetails(eventId);

        // Button listeners
        btnPeople.setOnClickListener(view -> {
            Intent intent = new Intent(this, PeopleActivity.class);
            intent.putExtra("eventId", eventId);
            startActivity(intent);
        });
    }

    private void loadEventDetails(String eventId) {
        DocumentReference eventRef = db.collection("events").document(eventId);
        eventRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                populateUI(documentSnapshot);
            } else {
                Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
        btnPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrganizerEventDetailsActivity.this, PeopleActivity.class);
                startActivity(intent);
            }
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Error loading event: " + e.getMessage(), Toast.LENGTH_SHORT).show()
        );
    }

    private void populateUI(DocumentSnapshot documentSnapshot) {
        String name = documentSnapshot.getString("name");
        String date = documentSnapshot.getString("date");
        String description = documentSnapshot.getString("description");
        String location = documentSnapshot.getString("location");
        String organizer = documentSnapshot.getString("organizer");
        String status = documentSnapshot.getString("status");

        tvEventName.setText(name != null ? name : "Unnamed Event");
        tvEventDate.setText(date != null ? date : "No Date");
        tvEventTime.setText(status != null ? "Status: " + status : "No Status");
        tvLocation.setText(location != null ? location : "No Location");
        tvOrganizer.setText(organizer != null ? organizer : "Unknown Organizer");
        tvAboutDescription.setText(description != null ? description : "No Description");
    }
}
