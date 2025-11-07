package com.example.zenithchance.fragments;

import android.app.AlertDialog;
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

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Fragment used by organizers to view detailed information about a single event.
 * Organizers can delete the event using the delete button, which shows
 * a confirmation dialog before removing the event from Firestore.
 *
 *
 * @author Kiran Kaur
 * @version 1.0
 */

public class OrganizerEventDetailsFragment extends Fragment {

    private Event event;
    private Organizer organizer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_organizer_event_details, container, false);
        SimpleDateFormat fmt = new SimpleDateFormat("EEE, MMM d • h:mm a", Locale.getDefault());


        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable("event");
            organizer = (Organizer) getArguments().getSerializable("organizer");
        } else {
            Toast.makeText(requireContext(), "Error: No event data passed", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
            return view;
        }

        // BACK ARROW
        MaterialToolbar toolbar = view.findViewById(R.id.organizer_event_toolbar);
        toolbar.setNavigationOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        // UI elements
        ImageView headerImage = view.findViewById(R.id.header_image);
        TextView eventName = view.findViewById(R.id.event_name);
        TextView location = view.findViewById(R.id.location);
        TextView time = view.findViewById(R.id.time);
        TextView description = view.findViewById(R.id.description);
        MaterialButton deleteButton = view.findViewById(R.id.delete_button);

        // UI
        eventName.setText(event.getName());
        location.setText(event.getLocation());
        time.setText(fmt.format(event.getDate()));
        description.setText(event.getDescription());

        Glide.with(requireContext())
                .load(event.getImageUrl())
                .placeholder(R.drawable.celebration_placeholder)
                .error(R.drawable.celebration_placeholder)
                .into(headerImage);

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
                                    Toast.makeText(requireContext(), "Event deleted", Toast.LENGTH_SHORT).show();
                                    requireActivity().getSupportFragmentManager().popBackStack();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(requireContext(), "Delete failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        return view;
    }
}
