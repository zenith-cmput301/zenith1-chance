//package com.example.zenithchance.activities;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.widget.Button;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.zenithchance.R;
//import com.example.zenithchance.fragments.OrganizerCreateEventFragment;
//import com.example.zenithchance.fragments.OrganizerEventsFragment;
//import com.example.zenithchance.fragments.ProfileFragment;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//public class OrganizerEventDetailsActivity extends AppCompatActivity {
//
//    private TextView tvEventName, tvEventDate, tvEventTime, tvLocation, tvOrganizer, tvAboutDescription;
//    private Button btnPeople, btnEdit, btnMyEvents, btnProfile;
//
//    private FirebaseFirestore db;
//    private String eventId;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.organizer_event_details);
//
//        db = FirebaseFirestore.getInstance();
//
//        eventId = getIntent().getStringExtra("eventId");
//        if (eventId == null) {
//            eventId = "MoKFp23CsnhegmhYe5Ue";
//        }
//
//        tvEventName = findViewById(R.id.tvEventName);
//        tvEventDate = findViewById(R.id.tvEventDate);
//        tvEventTime = findViewById(R.id.tvEventTime);
//        tvLocation = findViewById(R.id.tvLocation);
//        tvOrganizer = findViewById(R.id.tvOrganizer);
//        tvAboutDescription = findViewById(R.id.tvAboutDescription);
//
//        btnPeople = findViewById(R.id.btnPeople);
//        btnEdit = findViewById(R.id.btnEdit);
//
//        loadEventDetails(eventId);
//
//        btnPeople.setOnClickListener(view -> {
//            Intent intent = new Intent(OrganizerEventDetailsActivity.this, PeopleActivity.class);
//            intent.putExtra("eventId", eventId);
//            startActivity(intent);
//        });
//
//        btnEdit.setOnClickListener(view -> {
//            OrganizerCreateEventFragment createFragment = new OrganizerCreateEventFragment();
//
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.fragment_container, createFragment)
//                    .addToBackStack(null)
//                    .commit();
//        });
//
//        btnMyEvents.setOnClickListener(view -> {
//            OrganizerEventsFragment createFragment = new OrganizerEventsFragment();
//
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.fragment_container, createFragment)
//                    .addToBackStack(null)
//                    .commit();
//        });
//
//        btnProfile.setOnClickListener(view -> {
//            ProfileFragment createFragment = new ProfileFragment();
//
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.fragment_container, createFragment)
//                    .addToBackStack(null)
//                    .commit();
//        });
//
//
//    }
//
//    private void loadEventDetails(String eventId) {
//        DocumentReference eventRef = db.collection("events").document(eventId);
//        eventRef.get()
//                .addOnSuccessListener(documentSnapshot -> {
//                    if (documentSnapshot.exists()) {
//                        populateUI(documentSnapshot);
//                    } else {
//                        Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .addOnFailureListener(e ->
//                        Toast.makeText(this, "Error loading event: " + e.getMessage(), Toast.LENGTH_SHORT).show()
//                );
//    }
//
//    private void populateUI(DocumentSnapshot documentSnapshot) {
//        String name = documentSnapshot.getString("name");
//        String date = documentSnapshot.getString("date");
//        String time = documentSnapshot.getString("time");
//        String description = documentSnapshot.getString("description");
//        String location = documentSnapshot.getString("location");
//        String organizer = documentSnapshot.getString("organizer");
//
//        tvEventName.setText(name != null ? name : "Event name not assigned");
//        tvEventDate.setText(date != null ? date : "No Date");
//        tvEventTime.setText(time != null ? time : "No Time");
//        tvLocation.setText(location != null ? location : "No Location");
//        tvOrganizer.setText(organizer != null ? organizer : "Organizer not assigned");
//        tvAboutDescription.setText(description != null ? description : "No Description");
//    }
//}
