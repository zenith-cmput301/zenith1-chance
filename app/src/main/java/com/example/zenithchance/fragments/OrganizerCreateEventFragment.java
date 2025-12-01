package com.example.zenithchance.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.firestore.GeoPoint;
import  com.example.zenithchance.BuildConfig;

import java.util.Arrays;
import java.util.UUID;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.zenithchance.models.Event;
import com.example.zenithchance.R;
import com.example.zenithchance.models.Organizer;

import com.google.firebase.firestore.FirebaseFirestore;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Class for the UI used in event creation and modification
 *
 * @author Emerson, Sabrina
 * @version 1.0
 * @see Event
 */
public class OrganizerCreateEventFragment extends Fragment implements OnMapReadyCallback {

    EditText eventName;
    Button eventDateButton;
    Button eventRegistrationButton;
    EditText eventLocation;
    EditText eventMaxEntrants;
    EditText eventDescription;
    CheckBox eventGeolocationRequired;

    Button discardButton, submitButton;

    Organizer organizerId;

    private FirebaseFirestore db;


    // image launcher
    private ImageView eventImage;
    private Uri selectedImageUri;

    private ActivityResultLauncher<String> pickImageLauncher;

    private Double eventLat = null;
    private Double eventLng = null;

    private GoogleMap mMap;
    private Marker eventMarker;

    private ActivityResultLauncher<Intent> autocompleteLauncher;




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;
                        eventImage.setImageURI(uri);
                    }
                }
        );

        autocompleteLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {

                    // Re-enable the field
                    eventLocation.setEnabled(true);
                    eventLocation.setHint("Event Location");

                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Place place = Autocomplete.getPlaceFromIntent(result.getData());
                        LatLng latLng = place.getLatLng();
                        if (latLng != null) {
                            eventLat = latLng.latitude;
                            eventLng = latLng.longitude;

                            // Update text field
                            if (eventLocation != null) {
                                // You can choose name or address; address is usually nicer
                                eventLocation.setText(place.getAddress());
                            }

                            // Update marker + camera
                            if (mMap != null) {
                                if (eventMarker != null) eventMarker.remove();
                                eventMarker = mMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .title(place.getName()));

                                mMap.animateCamera(
                                        CameraUpdateFactory.newLatLngZoom(latLng, 14f),
                                        1000, // duration in ms
                                        new GoogleMap.CancelableCallback() {
                                            @Override
                                            public void onFinish() {
                                                // Optional: pulse the marker or show info window
                                                eventMarker.showInfoWindow();
                                            }

                                            @Override
                                            public void onCancel() {}
                                        }
                                );
                            }
                        }
                    } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                        // user backed out, ignore
                    } else if (result.getData() != null) {
                        Status status = Autocomplete.getStatusFromIntent(result.getData());
                        Toast.makeText(requireContext(),
                                "Place error: " + status.getStatusMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    /*
     The following function is from OpenAI, ChatGPT, "How to add Google maps fragment to my code?", 2025-11-30
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng defaultLatLng = new LatLng(53.5461, -113.4938);
        float defaultZoom = 10f;

        // Existing coordinates from event
        if (eventLat != null && eventLng != null) {
            LatLng eventLatLng = new LatLng(eventLat, eventLng);
            eventMarker = mMap.addMarker(new MarkerOptions()
                    .position(eventLatLng)
                    .title("Event location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLatLng, 14f));
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, defaultZoom));
        }

        // POI clicks
        mMap.setOnPoiClickListener(poi -> {
            if (eventMarker != null) eventMarker.remove();

            eventMarker = mMap.addMarker(new MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name));

            eventLat = poi.latLng.latitude;
            eventLng = poi.latLng.longitude;

            // Show place name only
            if (eventLocation != null) {
                eventLocation.setText(poi.name);
            }

            // Show info window for visual feedback
            if (eventMarker != null) {
                eventMarker.showInfoWindow();
            }
        });

        // Map click fallback
        mMap.setOnMapClickListener(latLng -> {
            if (eventMarker != null) eventMarker.remove();

            eventMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Pinned location"));

            eventLat = latLng.latitude;
            eventLng = latLng.longitude;

            updateLocationFieldFromLatLng(latLng);

            // Show info window
            if (eventMarker != null) {
                eventMarker.showInfoWindow();
            }
        });
    }



    /**
     * This method defines what happens when this fragment is created
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return View to display
     */

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // inflates fragment
        View root = inflater.inflate(R.layout.organizer_create_event, container, false);

        // gets input arguments
        Bundle args = getArguments();
        organizerId = (Organizer) args.getSerializable("organizer");

        db = FirebaseFirestore.getInstance();

        // Initializing views that may require modification
        eventImage = root.findViewById(R.id.organizer_event_image);
        eventName = root.findViewById(R.id.event_name_box);
        eventDateButton = root.findViewById(R.id.event_date_button);
        eventRegistrationButton = root.findViewById(R.id.event_registration_button);
        eventLocation = root.findViewById(R.id.event_location_box);
        eventMaxEntrants = root.findViewById(R.id.event_max_entrants_box);
        eventDescription = root.findViewById(R.id.event_description_box);
        eventGeolocationRequired = root.findViewById(R.id.event_geolocation_box);

        attachDateTimePicker(eventDateButton);
        attachDateTimePicker(eventRegistrationButton);

        // Initializing buttons
        discardButton = root.findViewById(R.id.event_creation_discard_button);
        submitButton = root.findViewById(R.id.event_creation_save_button);

        // tap the image button to view from gallery
        eventImage.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        // Updated fields to display existing values if they exist
        updateToExisting(args);
        setupDiscardButton();
        setupSubmitButton();

        // Setup the map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.event_map);
        if (mapFragment != null) { mapFragment.getMapAsync(this); }

        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), BuildConfig.MAPS_API_KEY);

        }
        PlacesClient placesClient = Places.createClient(requireContext());

        eventLocation.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_search, 0);

        // Show progress when launching
        eventLocation.setOnClickListener(v -> {
            eventLocation.setEnabled(false);
            eventLocation.setHint("Opening location search...");

            List<Place.Field> fields = Arrays.asList(
                    Place.Field.ID,
                    Place.Field.NAME,
                    Place.Field.ADDRESS,
                    Place.Field.LAT_LNG
            );

            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.OVERLAY,
                    fields
            ).setInitialQuery(eventLocation.getText().toString()) // Pre-fill with current text
                    .build(requireContext());

            autocompleteLauncher.launch(intent);
        });

        return root;


    }

    /**
     * This method sets up the submit button to for creation or updating
     */
    private void setupSubmitButton() {
        submitButton.setOnClickListener(v -> {
            Bundle args = getArguments();
            if (args != null && args.getSerializable("event") != null) {
                updateEventFields(args);
            } else {
                createNewEvent();
            }
        });
    }


    /**
     * This method sets up the discard button to  return the user to the events fragment
     */
    private void setupDiscardButton() {
        discardButton.setOnClickListener(v -> {
            OrganizerEventsFragment fragment = new OrganizerEventsFragment();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .commit();
        });
    }



    /*
     The following function is from Anthropic, Claude, "How to add Google maps fragment to my code?", 2025-11-30
     */
    private void updateLocationFieldFromLatLng(LatLng latLng) {
        if (latLng == null || eventLocation == null) return;

        eventLocation.setText("Finding address...");

        // Run geocoding in background thread to avoid blocking UI
        new Thread(() -> {
            Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
            String finalAddress;

            try {
                List<Address> addresses = geocoder.getFromLocation(
                        latLng.latitude,
                        latLng.longitude,
                        1
                );

                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    StringBuilder addrStr = new StringBuilder();

                    if (address.getThoroughfare() != null) {
                        addrStr.append(address.getThoroughfare()).append(" ");
                    }
                    if (address.getSubThoroughfare() != null) {
                        addrStr.append(address.getSubThoroughfare()).append(", ");
                    }
                    if (address.getLocality() != null) {
                        addrStr.append(address.getLocality()).append(", ");
                    }
                    if (address.getAdminArea() != null) {
                        addrStr.append(address.getAdminArea()).append(" ");
                    }
                    if (address.getPostalCode() != null) {
                        addrStr.append(address.getPostalCode());
                    }

                    finalAddress = addrStr.toString().trim();
                    if (finalAddress.isEmpty() && address.getFeatureName() != null) {
                        finalAddress = address.getFeatureName();
                    }
                } else {
                    finalAddress = String.format(Locale.getDefault(),
                            "%.6f, %.6f",
                            latLng.latitude,
                            latLng.longitude);
                }
            } catch (IOException e) {
                finalAddress = String.format(Locale.getDefault(),
                        "%.6f, %.6f",
                        latLng.latitude,
                        latLng.longitude);
            }

            // Update UI on main thread
            String addressToSet = finalAddress;
            requireActivity().runOnUiThread(() -> {
                eventLocation.setText(addressToSet);
            });
        }).start();
    }

    /**
     * Updates the text within the selection boxes to the pre-existing values if they exist
     * @param args the bundle of arguments passed to the fragment
     */

    private void updateToExisting(Bundle args) {
        if (args == null || args.getSerializable("event") == null) {
            return;
        }

        Event event = (Event) args.getSerializable("event");
        if (event == null) return;

        eventName.setText(event.getName());
        eventLocation.setText(event.getLocation()); // String, no cast needed
        eventMaxEntrants.setText(String.valueOf(event.getMaxEntrants()));
        eventDescription.setText(event.getDescription());
        eventGeolocationRequired.setChecked(event.getGeolocationRequired());

        // get coordinates from locationPoint
        GeoPoint point = event.getLocationPoint();
        if (point != null) {
            eventLat = point.getLatitude();
            eventLng = point.getLongitude();

        }

        SimpleDateFormat fmt =
                new SimpleDateFormat("MMMM d, yyyy 'at' h:mm:ss a z", Locale.getDefault());

        if (event.getDate() != null) {
            eventDateButton.setText(fmt.format(event.getDate()));
        }

        if (event.getRegistrationDate() != null) {
            eventRegistrationButton.setText(fmt.format(event.getRegistrationDate()));
        }

        if (event.getImageUrl() != null) {
            Glide.with(this)
                    .load(event.getImageUrl())
                    .into(eventImage);
        }
    }



    /**
     * Updates the event values to the field values currently selected
     * @param args the bundle of arguments passed to the fragment
     */

    private void updateEventFields(Bundle args) {
        if (args == null || args.getSerializable("event") == null) {
            return;
        }

        String locText = eventLocation.getText().toString().trim();
        if (eventLat == null || eventLng == null || locText.isEmpty()) {
            eventLocation.setError("Location is required");
            eventLocation.requestFocus();
            Toast.makeText(getContext(), "Please select a location", Toast.LENGTH_LONG).show();
            return;
        }

        Event event = (Event) args.getSerializable("event");
        if (event == null) return;

        SimpleDateFormat fmt =
                new SimpleDateFormat("MMMM d, yyyy 'at' h:mm:ss a z", Locale.getDefault());

        try {
            Date eventDate = fmt.parse(eventDateButton.getText().toString());
            Date registrationDate = fmt.parse(eventRegistrationButton.getText().toString());
            if (eventDate != null) event.setDate(eventDate);
            if (registrationDate != null) event.setRegistrationDate(registrationDate);
        } catch (ParseException e) {
            Toast.makeText(getContext(), "Invalid Date, Try Again", Toast.LENGTH_LONG).show();
            return;
        }

        event.setName(eventName.getText().toString());
        event.setLocation(locText);
        event.setDescription(eventDescription.getText().toString());
        event.setGeolocationRequired(eventGeolocationRequired.isChecked());

        String text = eventMaxEntrants.getText().toString().trim();
        int maxEntrants = 0;
        if (!text.isEmpty()) {
            try {
                maxEntrants = Integer.parseInt(text);
            } catch (NumberFormatException ignored) {}
        }
        event.setMaxEntrants(maxEntrants);

        // always save GeoPoint
        event.setLocationPoint(new GeoPoint(eventLat, eventLng));

        if (selectedImageUri != null) {
            uploadImageAndUpdateEvent(event);
        } else {
            saveUpdatedEventToFirestore(event);
        }
    }


    /**
     * Creates the event using the fields selected by the user and updates the users FireStore document
     */
    private void createNewEvent() {
        // location must be set
        String locText = eventLocation.getText().toString().trim();
        if (eventLat == null || eventLng == null || locText.isEmpty()) {
            eventLocation.setError("Location is required");
            eventLocation.requestFocus();
            Toast.makeText(getContext(), "Please select a location", Toast.LENGTH_LONG).show();
            return;
        }

        SimpleDateFormat fmt =
                new SimpleDateFormat("MMMM d, yyyy 'at' h:mm:ss a z", Locale.getDefault());

        Date eventdate;
        Date registrationdate;

        try {
            eventdate = fmt.parse(eventDateButton.getText().toString());
            registrationdate = fmt.parse(eventRegistrationButton.getText().toString());
        } catch (ParseException e) {
            Toast.makeText(getContext(), "Invalid Date, Try Again", Toast.LENGTH_LONG).show();
            return;
        }

        String text = eventMaxEntrants.getText().toString().trim();
        int maxEntrants = 0;

        if (!text.isEmpty()) {
            try {
                maxEntrants = Integer.parseInt(text);
            } catch (NumberFormatException ignored) {}
        }

        Event newEvent = new Event(
                eventdate,
                eventName.getText().toString(),
                locText,
                "waiting",
                organizerId.getName(),
                eventDescription.getText().toString(),
                eventGeolocationRequired.isChecked(),
                registrationdate,
                registrationdate,
                maxEntrants
        );

        newEvent.setLocationPoint(new GeoPoint(eventLat, eventLng));

        if (selectedImageUri != null) {
            uploadImageAndCreateEvent(newEvent);
        } else {
            saveNewEventToFirestore(newEvent);
        }
    }

    private void attachDateTimePicker(Button targetButton) {

        targetButton.setOnClickListener(v -> {

            final Calendar calendar = Calendar.getInstance();

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Pick date
            DatePickerDialog datePicker = new DatePickerDialog(
                    requireContext(),
                    (view, selectedYear, selectedMonth, selectedDay) -> {

                        calendar.set(Calendar.YEAR, selectedYear);
                        calendar.set(Calendar.MONTH, selectedMonth);
                        calendar.set(Calendar.DAY_OF_MONTH, selectedDay);

                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
                        int minute = calendar.get(Calendar.MINUTE);

                        // Pick time
                        TimePickerDialog timePicker = new TimePickerDialog(
                                requireContext(),
                                (tpView, selectedHour, selectedMinute) -> {

                                    calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                                    calendar.set(Calendar.MINUTE, selectedMinute);
                                    calendar.set(Calendar.SECOND, 0);

                                    SimpleDateFormat fmt =
                                            new SimpleDateFormat(
                                                    "MMMM d, yyyy 'at' h:mm:ss a z",
                                                    Locale.getDefault()
                                            );

                                    // Final formatted string
                                    String formatted = fmt.format(calendar.getTime());
                                    targetButton.setText(formatted);
                                },
                                hour,
                                minute,
                                false
                        );

                        timePicker.show();

                    },
                    year,
                    month,
                    day
            );

            datePicker.show();
        });
    }

    private void uploadImageAndCreateEvent(Event newEvent) {
        if (selectedImageUri == null) {
            saveNewEventToFirestore(newEvent);
            return;
        }

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imgRef = storageRef.child("event_images/" + UUID.randomUUID() + ".jpg");

        imgRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot ->
                        imgRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            newEvent.setImageUrl(uri.toString());
                            saveNewEventToFirestore(newEvent);
                        })
                )
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Image upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    saveNewEventToFirestore(newEvent);
                });
    }


    private void saveNewEventToFirestore(Event newEvent) {
        db.collection("events")
                .add(newEvent)
                .addOnSuccessListener(documentReference -> {

                    String docId = documentReference.getId();

                    // Updates docId field to include the unique docId

                    documentReference.update("docId", docId);

                    Toast.makeText(getContext(), "Event Created!", Toast.LENGTH_SHORT).show();

                    ArrayList<String> organizerEventList = organizerId.getOrgEvents();
                    organizerEventList.add(docId);

                    db.collection("users")
                            .document(organizerId.getUserId())
                            .update("orgEvents", organizerEventList);

                    organizerId.addOrgEvent(docId);

                    OrganizerEventsFragment fragment = new OrganizerEventsFragment();
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainer, fragment)
                            .commit();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error creating event. Please try again.", Toast.LENGTH_LONG).show();
                });
    }

    private void uploadImageAndUpdateEvent(Event event) {
        if (selectedImageUri == null) {
            saveUpdatedEventToFirestore(event);
            return;
        }

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imgRef = storageRef.child("event_images/" + UUID.randomUUID() + ".jpg");

        imgRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot ->
                        imgRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            event.setImageUrl(uri.toString());
                            saveUpdatedEventToFirestore(event);
                        })
                )
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Image upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    saveUpdatedEventToFirestore(event);
                });
    }

    private void saveUpdatedEventToFirestore(Event event) {
        db.collection("events")
                .document(event.getDocId())
                .set(event)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(getContext(), "Event updated", Toast.LENGTH_SHORT).show();
                    selectedImageUri = null;  // Reset after successful save
                    requireActivity().getSupportFragmentManager().popBackStack();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(),
                            "Error updating event: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }
}
