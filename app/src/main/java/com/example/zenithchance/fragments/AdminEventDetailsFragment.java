package com.example.zenithchance.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.zenithchance.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Fragment used by admin users to view detailed information about a single event.
 * Admins can delete the event using the delete button, which shows
 * a confirmation dialog before removing the event from Firestore.
 *
 * <p>
 * Expected bundle arguments to exist (in event model class):
 * <ul>
 *     <li>event_doc_id (String) – Firestore document ID of the event</li>
 *     <li>event_name (String) – Event name</li>
 *     <li>event_location (String) – Event location</li>
 *     <li>event_organizer (String) – Event organizer name</li>
 *     <li>event_time (String) – Formatted event date/time</li>
 *     <li>event_description (String) – Event description</li>
 *     <li>event_image_url (String) – URL of the event image</li>
 * </ul>
 * </p>
 *
 * @author Kiran Kaur
 * @version 1.0
 */
public class AdminEventDetailsFragment extends Fragment {

    /**
     * Default constructor for the fragment.
     */
    public AdminEventDetailsFragment() {}

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views.
     * @param container          If non-null, this is the parent view that the fragment's UI should attach to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.admin_event_details, container, false);

        // Initialize UI components
        TextView name = view.findViewById(R.id.event_name);
        TextView location = view.findViewById(R.id.location);
        TextView organizer = view.findViewById(R.id.organizer_name);
        TextView time = view.findViewById(R.id.time);
        TextView desc = view.findViewById(R.id.description);
        ImageView image = view.findViewById(R.id.header_image);
        MaterialButton deleteBtn = view.findViewById(R.id.delete_button);

        // Retrieve arguments passed from previous fragment/activity
        Bundle args = getArguments();
        if (args != null) {
            String docId = args.getString("event_doc_id");
            name.setText(args.getString("event_name"));
            location.setText(args.getString("event_location"));
            organizer.setText(args.getString("event_organizer"));
            time.setText(args.getString("event_time"));
            desc.setText(args.getString("event_description"));

            // Load event image using Glide with placeholder and error fallback
            String imageUrl = args.getString("event_image_url");
            Glide.with(requireContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_my_events)
                    .error(R.drawable.ic_my_events)
                    .into(image);

            // Configure toolbar back navigation
            MaterialToolbar toolbar = view.findViewById(R.id.admin_toolbar);
            toolbar.setNavigationOnClickListener(v1 ->
                    requireActivity().getSupportFragmentManager().popBackStack());

            // Set delete button listener
            deleteBtn.setOnClickListener(v -> {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Delete Event?")
                        .setMessage("This action cannot be undone.")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            FirebaseFirestore.getInstance()
                                    .collection("events")
                                    .document(docId)
                                    .delete()
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(requireContext(), "Event deleted", Toast.LENGTH_SHORT).show();
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
        }

        return view;
    }
}
