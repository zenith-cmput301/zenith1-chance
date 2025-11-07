package com.example.zenithchance.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.zenithchance.OrganizerMainActivity;
import com.example.zenithchance.models.Event;
import com.example.zenithchance.R;
import com.example.zenithchance.models.Organizer;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Class for the UI used in event creation and modification
 *
 * @author Emerson
 * @version 1.0
 * @see Event
 */
public class OrganizerCreateEventFragment extends Fragment {

    ImageView eventImage;
    EditText eventName;
    EditText eventDate;
    EditText eventRegistration;
    EditText eventLocation;
    NumberPicker eventMaxEntrants;
    EditText eventDescription;
    CheckBox eventGeolocationRequired;

    Button discardButton, submitButton;

    Organizer organizerId;

    private FirebaseFirestore db;


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
        eventDate = root.findViewById(R.id.event_date_box);
        eventRegistration = root.findViewById(R.id.event_registration_box);
        eventLocation = root.findViewById(R.id.event_location_box);
        eventMaxEntrants = root.findViewById(R.id.event_max_entrants_box);
        eventDescription = root.findViewById(R.id.event_description_box);
        eventGeolocationRequired = root.findViewById(R.id.event_geolocation_box);

        // Initializing buttons
        discardButton = root.findViewById(R.id.event_creation_discard_button);
        submitButton = root.findViewById(R.id.event_creation_save_button);

        // Sets bounds for numberpicker
        eventMaxEntrants.setMinValue(0);
        eventMaxEntrants.setMaxValue(100);

        // Updated fields to display existing values if they exist
        updateToExisting(args);


        setupDiscardButton();
        setupSubmitButton();


        return root;
    }

    /**
     * This method sets up the submit button to for creation or updating
     */
    private void setupSubmitButton(){

        Bundle args = getArguments();

        submitButton.setOnClickListener(v -> {
            if (args.getSerializable("event") != null){
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
    /**
     * Updates the text within the selection boxes to the pre-existing values if they exist
     * @param args the bundle of arguments passed to the fragment
     */

    private void updateToExisting(Bundle args) {

        if (args.getSerializable("event") != null) {
            Event event = (Event) getArguments().getSerializable("event");

//            eventImage = PLACEHOLDER;

            eventName.setText(event.getName());
            eventDate.setText(event.getDate().toString());
            eventRegistration.setText(event.getRegistrationDate().toString());
            eventLocation.setText(event.getLocation());
            eventMaxEntrants.setValue(event.getMaxEntrants());
            eventDescription.setText(event.getDescription());
            if (event.getGeolocationRequired()) { eventGeolocationRequired.setChecked(true); }
        }
    }

    /**
     * Updates the event values to the field values currently selected
     * @param args the bundle of arguments passed to the fragment
     */
    private void updateEventFields(Bundle args) {

        if (args.getSerializable("event") != null) {
            Event event = (Event) getArguments().getSerializable("event");

//            eventImage = PLACEHOLDER;

            event.setName(eventName.toString());
            eventDate.setText(event.getDate().toString());
            eventRegistration.setText(event.getRegistrationDate().toString());
            event.setLocation(eventLocation.toString());
            event.setMaxEntrants(eventMaxEntrants.getValue());
            eventDescription.setText(event.getDescription());
            if (event.getGeolocationRequired()) { eventGeolocationRequired.setChecked(true); }
        }
    }

    /**
     * Creates the event using the fields selected by the user and updates the users FireStore document
     */
    private void createNewEvent() {

        String expectedFormat = "MMMM d, yyyy 'at' h:mm:ss a z";
        SimpleDateFormat fmt = new SimpleDateFormat("MMMM d, yyyy 'at' h:mm:ss a z", Locale.getDefault());

        // Get the text from the EditText fields
        String eventDateString = eventDate.getText().toString();
        String registrationDateString = eventRegistration.getText().toString();

        Date eventdate;
        Date registrationdate;

        try {
            eventdate = fmt.parse(eventDate.getText().toString());
            registrationdate = fmt.parse(eventRegistration.getText().toString());
            Log.d("to string", eventDate.getText().toString());
        } catch (ParseException e) {
            Toast.makeText(getContext(), "Invalid Date, Try Again", Toast.LENGTH_LONG).show();
            return;
        }

        // Constructs event based on inputted data

        Log.d("organizer name", organizerId.getName());

        Event newEvent = new Event(eventdate,
                eventName.getText().toString(),
                eventLocation.getText().toString(),
                "waiting",
                organizerId.getName(),
                eventDescription.getText().toString(),
                eventGeolocationRequired.isChecked(),
                registrationdate,
                registrationdate,
                eventMaxEntrants.getValue());


        // Adds event to firebase

        db.collection("events")
                .add(newEvent)

                .addOnSuccessListener(documentReference -> {

                    String docId = documentReference.getId();

                    Toast.makeText(getContext(), "Event Created!", Toast.LENGTH_SHORT).show();

                    // Returns to Events fragment

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


}
