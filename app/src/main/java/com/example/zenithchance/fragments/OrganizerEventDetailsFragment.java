package com.example.zenithchance.fragments;

import android.app.AlertDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.zenithchance.R;
import com.example.zenithchance.models.Event;
import com.example.zenithchance.models.Organizer;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Fragment used by organizers to view detailed information about a single event.
 * Organizers can delete the event using the delete button, which shows
 * a confirmation dialog before removing the event from Firestore.
 *
 * FUTURE UPDATES (To be implemented):
 * Here, We will also include:
 * Edit Event, View Waitlist, Draw Random Users (from waitlist),
 * Send Notifications to selected and rejected entrants
 *
 * @author Kiran Kaur, Sabrina Ghadieh
 * @version 1.0
 */

public class OrganizerEventDetailsFragment extends Fragment {

    private Event event;
    private Organizer organizer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_organizer_event_details, container, false);
        SimpleDateFormat dateTimeFmt = new SimpleDateFormat("EEE, MMM d • h:mm a", Locale.getDefault());

        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable("event");
            organizer = (Organizer) getArguments().getSerializable("organizer");
        } else {
            Toast.makeText(requireContext(), "Error: No event data passed", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
            return view;
        }

        // Toolbar back arrow
        MaterialToolbar toolbar = view.findViewById(R.id.organizer_event_toolbar);
        toolbar.setNavigationOnClickListener(
                v -> requireActivity().getSupportFragmentManager().popBackStack()
        );

        // Header image & title
        ImageView headerImage = view.findViewById(R.id.header_image);
        TextView tvEventName = view.findViewById(R.id.event_name);

        TextView tvDate = view.findViewById(R.id.tv_date);
        TextView tvLocation = view.findViewById(R.id.tv_location);
        TextView tvRegistrationDeadline = view.findViewById(R.id.tv_registration_deadline);
        TextView tvMaxEntrants = view.findViewById(R.id.tv_max_entrants);
        TextView tvGeolocation = view.findViewById(R.id.tv_geolocation);
        TextView tvOrganizer = view.findViewById(R.id.tv_organizer);
        TextView tvDescription = view.findViewById(R.id.tv_description);

        MaterialButton deleteButton = view.findViewById(R.id.delete_button);
        MaterialButton editButton = view.findViewById(R.id.edit_button);

        // Title & image
        tvEventName.setText(event.getName()); // required, so no "None" here

        Glide.with(requireContext())
                .load(event.getImageUrl())
                .placeholder(R.drawable.celebration_placeholder)
                .error(R.drawable.celebration_placeholder)
                .into(headerImage);

        // Date (required)
        if (event.getDate() != null) {
            tvDate.setText(dateTimeFmt.format(event.getDate()));
            tvDate.setTypeface(null, Typeface.NORMAL);
        } else {
            tvDate.setText("None");
            tvDate.setTypeface(null, Typeface.ITALIC);
        }

        // Registration deadline (optional)
        if (event.getRegistrationDate() != null) {
            tvRegistrationDeadline.setText(dateTimeFmt.format(event.getRegistrationDate()));
            tvRegistrationDeadline.setTypeface(null, Typeface.NORMAL);
        } else {
            tvRegistrationDeadline.setText("None");
            tvRegistrationDeadline.setTypeface(null, Typeface.ITALIC);
        }

        // Fill fields with fallback "None"
        setOptionalText(tvLocation, event.getLocation());                             // location
        setOptionalInteger(tvMaxEntrants, event.getMaxEntrants());                    // status
        setOptionalBoolean(tvGeolocation, event.getGeolocationRequired());           // geolocation_required (Boolean)
        setOptionalText(tvOrganizer,
                event.getOrganizer());                                                // organizer (String, or swap to organizer.getName())
        setOptionalText(tvDescription, event.getDescription());                       // description

        // DELETE BUTTON
        deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete Event?")
                    .setMessage("This action cannot be undone.")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        FirebaseFirestore.getInstance()
                                .collection("events")
                                .document(event.getDocId())
                                .delete()
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(requireContext(),
                                            "Event deleted", Toast.LENGTH_SHORT).show();
                                    requireActivity().getSupportFragmentManager().popBackStack();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(requireContext(),
                                                "Delete failed: " + e.getMessage(),
                                                Toast.LENGTH_LONG).show());
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // UPDATE / EDIT BUTTON
        editButton.setOnClickListener(v -> {
            OrganizerCreateEventFragment fragment = new OrganizerCreateEventFragment();

            Bundle bundle = new Bundle();
            bundle.putSerializable("event", event);        // the existing event
            bundle.putSerializable("organizer", organizer);
            fragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void setOptionalText(TextView tv, @Nullable String value) {
        if (value == null || value.trim().isEmpty()) {
            tv.setText("None");
            tv.setTypeface(null, Typeface.ITALIC);
        } else {
            tv.setText(value);
            tv.setTypeface(null, Typeface.NORMAL);
        }
    }

    private void setOptionalBoolean(TextView tv, @Nullable Boolean value) {
        if (value == null) {
            tv.setText("None");
            tv.setTypeface(null, Typeface.ITALIC);
        } else {
            tv.setText(value ? "Yes" : "No");
            tv.setTypeface(null, Typeface.NORMAL);
        }
    }

    private void setOptionalInteger(TextView tv, @Nullable Integer value) {
        if (value == null || value <= 0) {
            tv.setText("None");
            tv.setTypeface(null, Typeface.ITALIC);
        } else {
            tv.setText(String.valueOf(value));
            tv.setTypeface(null, Typeface.NORMAL);
        }
    }
}
