package com.example.zenithchance.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.widget.NumberPicker;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.zenithchance.models.Event;
import com.example.zenithchance.R;
import com.example.zenithchance.models.Organizer;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
        eventLocation.setText(event.getLocation());
        eventMaxEntrants.setText(String.valueOf(event.getMaxEntrants()));
        eventDescription.setText(event.getDescription());
        eventGeolocationRequired.setChecked(event.getGeolocationRequired());

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

        Event event = (Event) args.getSerializable("event");
        if (event == null) return;

        SimpleDateFormat fmt =
                new SimpleDateFormat("MMMM d, yyyy 'at' h:mm:ss a z", Locale.getDefault());

        try {
            // parse dates from the buttons, same format as createNewEvent
            Date eventDate = fmt.parse(eventDateButton.getText().toString());
            Date registrationDate = fmt.parse(eventRegistrationButton.getText().toString());

            if (eventDate != null) {
                event.setDate(eventDate);
            }
            if (registrationDate != null) {
                event.setRegistrationDate(registrationDate);
            }
        } catch (ParseException e) {
            Toast.makeText(getContext(), "Invalid Date, Try Again", Toast.LENGTH_LONG).show();
            return;
        }

        // update rest of fields from UI
        event.setName(eventName.getText().toString());
        event.setLocation(eventLocation.getText().toString());
        event.setDescription(eventDescription.getText().toString());
        event.setGeolocationRequired(eventGeolocationRequired.isChecked());

        String text = eventMaxEntrants.getText().toString().trim();
        int maxEntrants = 0;   // default if empty

        if (!text.isEmpty()) {
            try {
                maxEntrants = Integer.parseInt(text);
            } catch (NumberFormatException e) {
                // invalid number format
            }
        }

        event.setMaxEntrants(maxEntrants);

        // push the updated event to Firestore
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

        SimpleDateFormat fmt =
                new SimpleDateFormat("MMMM d, yyyy 'at' h:mm:ss a z", Locale.getDefault());

        Date eventdate;
        Date registrationdate;

        try {
            eventdate = fmt.parse(eventDateButton.getText().toString());
            registrationdate = fmt.parse(eventRegistrationButton.getText().toString());
            Log.d("to string", eventDateButton.getText().toString());
        } catch (ParseException e) {
            Toast.makeText(getContext(), "Invalid Date, Try Again", Toast.LENGTH_LONG).show();
            return;
        }

        String text = eventMaxEntrants.getText().toString().trim();
        int maxEntrants = 0;   // default if empty

        if (!text.isEmpty()) {
            try {
                maxEntrants = Integer.parseInt(text);
            } catch (NumberFormatException e) {
                // you might want a Toast here
            }
        }

        // Constructs event based on inputted data
        Log.d("organizer name", organizerId.getName());

        Event newEvent = new Event(
                eventdate,
                eventName.getText().toString(),
                eventLocation.getText().toString(),
                "waiting",
                organizerId.getName(),
                eventDescription.getText().toString(),
                eventGeolocationRequired.isChecked(),
                registrationdate,
                registrationdate,
                maxEntrants
        );

        // handle image + save
        if (selectedImageUri != null) {
            // upload image first, then save event with download URL
            uploadImageAndCreateEvent(newEvent);
        } else {
            // no image; just save event
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
            Log.w("ImageUpload", "selectedImageUri is null");
            saveNewEventToFirestore(newEvent);
            return;
        }

        try {
            Log.d("ImageUpload", "Starting image upload with URI: " + selectedImageUri.toString());

            // Convert URI to bytes
            InputStream inputStream = getContext().getContentResolver().openInputStream(selectedImageUri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;

            while ((length = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, length);
            }

            byte[] imageBytes = byteArrayOutputStream.toByteArray();
            inputStream.close();

            Log.d("ImageUpload", "Image converted to bytes, size: " + imageBytes.length);

            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference imgRef = storageRef.child("event_images/" + UUID.randomUUID() + ".jpg");

            imgRef.putBytes(imageBytes)
                    .addOnSuccessListener(taskSnapshot -> {
                        Log.d("ImageUpload", "Upload successful, getting download URL");
                        imgRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                    String downloadUrl = uri.toString();
                                    Log.d("ImageUpload", "Download URL: " + downloadUrl);
                                    newEvent.setImageUrl(downloadUrl);
                                    saveNewEventToFirestore(newEvent);
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("ImageUpload", "Failed to get download URL", e);
                                    Toast.makeText(getContext(), "Failed to get download URL", Toast.LENGTH_LONG).show();
                                    saveNewEventToFirestore(newEvent);
                                });
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ImageUpload", "Image upload failed: " + e.getMessage(), e);
                        Toast.makeText(getContext(), "Image upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        saveNewEventToFirestore(newEvent);
                    });

        } catch (IOException e) {
            Log.e("ImageUpload", "Failed to read image file", e);
            Toast.makeText(getContext(), "Failed to read image file", Toast.LENGTH_LONG).show();
            saveNewEventToFirestore(newEvent);
        }
    }

    private void saveNewEventToFirestore(Event newEvent) {
        db.collection("events")
                .add(newEvent)
                .addOnSuccessListener(documentReference -> {

                    String docId = documentReference.getId();

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
        // Store the original image URL in case upload fails
        String originalImageUrl = event.getImageUrl();

        if (selectedImageUri == null) {
            Log.w("ImageUpload", "selectedImageUri is null");
            saveUpdatedEventToFirestore(event);
            return;
        }

        try {
            Log.d("ImageUpload", "Starting image upload with URI: " + selectedImageUri.toString());

            // Convert URI to bytes
            InputStream inputStream = getContext().getContentResolver().openInputStream(selectedImageUri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;

            while ((length = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, length);
            }

            byte[] imageBytes = byteArrayOutputStream.toByteArray();
            inputStream.close();

            Log.d("ImageUpload", "Image converted to bytes, size: " + imageBytes.length);

            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference imgRef = storageRef.child("event_images/" + UUID.randomUUID() + ".jpg");

            imgRef.putBytes(imageBytes)
                    .addOnSuccessListener(taskSnapshot -> {
                        Log.d("ImageUpload", "Upload successful, getting download URL");
                        imgRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                    String downloadUrl = uri.toString();
                                    Log.d("ImageUpload", "Download URL: " + downloadUrl);
                                    event.setImageUrl(downloadUrl);
                                    saveUpdatedEventToFirestore(event);
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("ImageUpload", "Failed to get download URL", e);
                                    Toast.makeText(getContext(), "Failed to get download URL", Toast.LENGTH_LONG).show();
                                    event.setImageUrl(originalImageUrl);
                                    saveUpdatedEventToFirestore(event);
                                });
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ImageUpload", "Image upload failed: " + e.getMessage(), e);
                        Toast.makeText(getContext(), "Image upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        event.setImageUrl(originalImageUrl);
                        saveUpdatedEventToFirestore(event);
                    });

        } catch (IOException e) {
            Log.e("ImageUpload", "Failed to read image file", e);
            Toast.makeText(getContext(), "Failed to read image file", Toast.LENGTH_LONG).show();
            event.setImageUrl(originalImageUrl);
            saveUpdatedEventToFirestore(event);
        }
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
