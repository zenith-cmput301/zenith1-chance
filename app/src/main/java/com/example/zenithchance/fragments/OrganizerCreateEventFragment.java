package com.example.zenithchance.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;

import androidx.fragment.app.Fragment;

import com.example.zenithchance.models.Event;
import com.example.zenithchance.R;

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

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // inflates fragment
        View root = inflater.inflate(R.layout.organizer_create_event, container, false);
        Bundle args = getArguments();

        // Initializing views that may require modification
        eventImage = root.findViewById(R.id.event_image);
        eventName = root.findViewById(R.id.event_name_box);
        eventDate = root.findViewById(R.id.event_date_box);
        eventRegistration = root.findViewById(R.id.event_registration_box);
        eventLocation = root.findViewById(R.id.event_location_box);
        eventMaxEntrants = root.findViewById(R.id.event_max_entrants_box);
        eventDescription = root.findViewById(R.id.event_description_box);
        eventGeolocationRequired = root.findViewById(R.id.event_geolocation_box);

        // Initializing buttons
        Button discardButton = root.findViewById(R.id.event_creation_discard_button);
        Button submitButton = root.findViewById(R.id.event_creation_save_button);

        eventMaxEntrants.setMinValue(0);
        eventMaxEntrants.setMaxValue(100);

        updateToExisting();

        discardButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });
        submitButton.setOnClickListener(v -> {
            if (args != null){
                updateEventFields();
            } else {
//                createNewEvent();
            }
            requireActivity().getSupportFragmentManager().popBackStack();
        });



        return root;
    }

    /**
     * Updates the text within the selection boxes to the pre-existing values if they exist
     */

    private void updateToExisting() {

        Bundle args = getArguments();
        if (args != null) {
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
     */
    private void updateEventFields() {

        Bundle args = getArguments();
        if (args != null) {
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
//    private void createNewEvent() {
//        Event newEvent = new Event(eventName.toString(),
//                eventDate.toString(), event):
//    }

}
