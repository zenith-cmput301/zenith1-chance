package com.example.zenithchance.activities;


import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.zenithchance.R;
import com.example.zenithchance.models.WaitingListEntry;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.List;

/*
This class lets the organizer view where entrants joined the waiting list from, for a certain event.
*/

public class ViewEntrantsWaitingListLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String eventDocId;
    private String eventName;
    private FirebaseFirestore db;
    private final List<LatLng> entrantLocations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_entrants_waiting_list_location);

        // Get event data from intent
        eventDocId = getIntent().getStringExtra("eventDocId");
        eventName = getIntent().getStringExtra("eventName");

        if (eventDocId == null) {
            Toast.makeText(this, "Error: No event data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        db = FirebaseFirestore.getInstance();

        // Setup toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(eventName != null ? eventName : "Entrant Locations");
        toolbar.setNavigationOnClickListener(v -> finish());

        // Setup map
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);

        // Load entrant locations
        loadEntrantLocations();
    }

    /**
     * Load all WaitingListEntry docs for this event and display their entrantLocation.
     */
    private void loadEntrantLocations() {
        db.collection("waitingListEntries")
                .whereEqualTo("eventId", eventDocId)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    // if there are no entrants on the waiting list of this event
                    if (querySnapshot.isEmpty()) {
                        Toast.makeText(this,
                                "No entrants have joined the waiting list yet.",
                                Toast.LENGTH_SHORT).show();

                        // Center map to a default (or event location)
                        LatLng defaultLocation = new LatLng(53.5461, -113.4938);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10f));

                        return;
                    }

                    int total = querySnapshot.size();
                    int[] loaded = {0};

                    for (DocumentSnapshot doc : querySnapshot) {
                        WaitingListEntry entry = doc.toObject(WaitingListEntry.class);

                        if (entry == null || entry.getEntrantLocation() == null) {
                            loaded[0]++;
                            if (loaded[0] == total) adjustCameraToShowAllMarkers();
                            continue;
                        }

                        GeoPoint gp = entry.getEntrantLocation();
                        LatLng latLng = new LatLng(gp.getLatitude(), gp.getLongitude());
                        entrantLocations.add(latLng);

                        String userId = entry.getUserId();

                        db.collection("users").document(userId)
                                .get()
                                .addOnSuccessListener(userDoc -> {
                                    String userName = userDoc.exists()
                                            ? userDoc.getString("name")
                                            : "Entrant";

                                    if (userName == null || userName.isEmpty()) {
                                        userName = "Entrant";
                                    }

                                    mMap.addMarker(new MarkerOptions()
                                            .position(latLng)
                                            .title(userName)
                                            .snippet("Joined from here"));

                                    loaded[0]++;
                                    if (loaded[0] == total) adjustCameraToShowAllMarkers();
                                })
                                .addOnFailureListener(e -> {
                                    mMap.addMarker(new MarkerOptions()
                                            .position(latLng)
                                            .title("Entrant")
                                            .snippet("Joined from here"));

                                    loaded[0]++;
                                    if (loaded[0] == total) adjustCameraToShowAllMarkers();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this,
                            "Error loading entrant locations: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();

                    LatLng defaultLocation = new LatLng(53.5461, -113.4938);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10f));
                });
    }


    /**
     * Adjust the map camera to show all entrant markers.
     */
    private void adjustCameraToShowAllMarkers() {
        if (entrantLocations.isEmpty()) {
            Toast.makeText(this, "No location data available for entrants",
                    Toast.LENGTH_SHORT).show();
            // Show default location
            LatLng defaultLocation = new LatLng(53.5461, -113.4938);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10f));
            return;
        }

        if (entrantLocations.size() == 1) {
            // Only one location, zoom to it
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    entrantLocations.get(0), 14f));
        } else {
            // Multiple locations, show all
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (LatLng location : entrantLocations) {
                builder.include(location);
            }
            LatLngBounds bounds = builder.build();

            int padding = 100;
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
        }
    }
}
